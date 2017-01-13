package com.lgc.mysliding.fragment;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.Gson;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.MyMainActivity;
import com.lgc.mysliding.adapter.TraceAmapInfoWin;
import com.lgc.mysliding.bean.TraceInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackFragment extends Fragment implements View.OnClickListener {

    private static final String TAG="TrackFragment";
    private static final long aWeek=99999999;
    private static final long aDay=000000;
    private long startTime;//开始时间
    private long endTime;//结束时间
    private boolean isSelected=false;//是否选择时间段
    private boolean selectCustom=false;//设置选择自定义标志
    private boolean isReplay;//设置回放标记

    private View mView;
    private MyMainActivity myMainActivity;
    private ImageView iv_search_trace;
    private PopupWindow popupWindow;
    private RadioGroup radioGroup;
    private EditText et_device_id;
    private RadioButton rb_week;
    private RadioButton rb_day;
    private RadioButton rb_custom;
    private View popupView;
    private EditText startYear;
    private EditText startDay;
    private EditText startMount;
    private EditText endYear;
    private EditText endMount;
    private EditText endDay;
    private Button btn_cancel;
    private Button btn_ensure;
    private Button btn_replay;
    private SeekBar sb_play;
    private MapView map_view;
    private AMap aMap;
    private Marker marker=null;//轨迹点
    private LatLng latLng;//轨迹点经纬度
    //解析到的原始数据集合
    private List<TraceInfo.TraceBean> traceInfoList=new ArrayList<TraceInfo.TraceBean>();
    //匹配时间的集合
    private List<TraceInfo.TraceBean> deltaList=new ArrayList<TraceInfo.TraceBean>();
    //用于画轨迹点集合
    private List<TraceInfo.TraceBean> path_list=new ArrayList<TraceInfo.TraceBean>();//暂不使用
    //匹配时间的经纬度集合
    private ArrayList<LatLng> deltaLatlngs=new ArrayList<LatLng>();
    //用于画线的经纬度集合
    private ArrayList<LatLng> delta_path=new ArrayList<LatLng>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_track, container, false);
            initView(savedInstanceState);
//        }

        //解析assets目录下的json数据
        traceInfoList=getJson();
        //异步解析assets目录下的json数据
//        new getJsonAsyncTask(traceInfoList,"trace.json").execute("trace.json");
        Log.d(TAG,"onCreateView");
        return mView;
    }

    //初始化view
    private void initView(Bundle bundle){
        sb_play = (SeekBar) mView.findViewById(R.id.sb_play);
        btn_replay = (Button) mView.findViewById(R.id.btn_replay);
        //初始化地图
        map_view = (MapView) mView.findViewById(R.id.map_view_2d);
        map_view.onCreate(bundle);
        if (aMap==null){
            aMap = map_view.getMap();
        }
        // TODO: 2017/1/13 amap设置自定义 infowindow
        aMap.setInfoWindowAdapter(new TraceAmapInfoWin());
        //弹出popupView窗口按钮
        iv_search_trace = myMainActivity.iv_search_trace;
        iv_search_trace.setOnClickListener(this);

        sb_play.setOnSeekBarChangeListener(new MSeekBarChangeListener());
        btn_replay.setOnClickListener(this);
        Log.d(TAG,"initView");
    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow(){
        popupView = getLayoutInflater(this.getArguments())
                .inflate(R.layout.track_popupwindow,null,false);
        //创建popupWindow,设置高宽，并获取焦点true
        popupWindow = new PopupWindow(popupView,mView.getMeasuredWidth()
                ,mView.getMeasuredHeight()/3 + 100,true);
        //设置popupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //窗口消失，设置时间段选择标志
                isSelected=false;
                iv_search_trace.setImageResource(R.drawable.arrows_up);

                // TODO: 2017/1/12  popupWindow 消失，启动回放任务
//                runnable=new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.sendMessage(Message.obtain(handler,1));
//                    }
//                };
            }
        });

        //初始 popupView 控件
        et_device_id = (EditText) popupView.findViewById(R.id.et_device_id);
        radioGroup = (RadioGroup) popupView.findViewById(R.id.radio_group);
        rb_week = (RadioButton) popupView.findViewById(R.id.rb_week);
        rb_day = (RadioButton) popupView.findViewById(R.id.rb_day);
        rb_custom = (RadioButton) popupView.findViewById(R.id.rb_custom);
        //开始时间
        startYear = (EditText) popupView.findViewById(R.id.et_start_year);
        startMount = (EditText) popupView.findViewById(R.id.et_start_mount);
        startDay = (EditText) popupView.findViewById(R.id.et_start_day);
        //设置输入监听
        startYear.addTextChangedListener(new MeditextWatcher(startYear));
        startMount.addTextChangedListener(new MeditextWatcher(startMount));
        startDay.addTextChangedListener(new MeditextWatcher(startDay));
        //结束时间
        endYear = (EditText) popupView.findViewById(R.id.et_end_year);
        endMount = (EditText) popupView.findViewById(R.id.et_end_mount);
        endDay = (EditText) popupView.findViewById(R.id.et_end_day);
        //设置输入监听
        endYear.addTextChangedListener(new MeditextWatcher(endYear));
        endMount.addTextChangedListener(new MeditextWatcher(endMount));
        endDay.addTextChangedListener(new MeditextWatcher(endDay));
        //取消、确定
        btn_cancel = (Button) popupView.findViewById(R.id.btn_cancel);
        btn_ensure = (Button) popupView.findViewById(R.id.btn_ensure);

        btn_cancel.setOnClickListener(this);
        btn_ensure.setOnClickListener(this);
        //时间段选项监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                //选择了时间段，设置选择标志
                isSelected=true;

                Log.d(TAG,"onCheckedChanged-"+i);
                switch (i){
                    case R.id.rb_week:
                        Log.d(TAG,"选择一周前");
                        //设置标志为不选择自定义
                        selectCustom=false;
                        aWeekORaDay(R.id.rb_week);
                        //测试数据--> // TODO: 2017/1/12 这里获取匹配的集合的经纬度集合
                        startTime = 1462198037;
                        endTime = 1462198837;
                        //测试数据-->
                        break;

                    case R.id.rb_day:
                        Log.d(TAG,"选择一天前");
                        //设置标志为不选择自定义
                        selectCustom=false;
                        aWeekORaDay(R.id.rb_day);
                        //测试数据--> // TODO: 2017/1/12 这里获取匹配的集合的经纬度集合
                        startTime = 1462198037;
                        endTime = 1462198837;
                        //测试数据-->
                        break;

                    case R.id.rb_custom:
                        Log.d(TAG,"选择自定义");
                        //设置标志为选择自定义
                        selectCustom=true;
                        //把开始时间和结束时间设为0
                        startTime=endTime=0L;
                        Log.d(TAG,"startTime=="+startTime+"-"+"endTime=="+endTime);
                        setCustomUI();
                        break;
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //显示和隐藏popupwindow
            case R.id.iv_search_trace:
                if (popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    iv_search_trace.setImageResource(R.drawable.arrows_up);
                    return;
                }else {
                    initPopupWindow();
                    popupWindow.showAsDropDown(view,0,20);
                    iv_search_trace.setImageResource(R.drawable.arrows_down);
                }
                break;

            //取消按钮
            case R.id.btn_cancel:
                if (popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    return;
                }
                break;

            //确定按扭
            case R.id.btn_ensure:

                //设置回放按钮可以点击
                if (!btn_replay.isEnabled()){
                    btn_replay.setEnabled(true);
                }

                //选择了时间段并且时间段不为0才能执行
                if (isSelected&&(startTime>0&&endTime>0)){
                    // TODO: 2017/1/13 先清除地图
                    if (aMap!=null){
                        aMap.clear();
                    }
                    //判断是否为自定义的
                    if (selectCustom==true){
                        getCustomTime();
                    }
                    //获取时间匹配的列表
                    getDeltaList();
                    // TODO: 2017/1/13
//                    sb_play.setMax(deltaList.size());
//                    sb_play.setProgress(0);
                    //在地图上显示标记
                    setAmapMarker();
                    //如果开始时间，结束时间不为0才取消窗口
                    if ((startTime>0&&endTime>0)&&(popupWindow!=null && popupWindow.isShowing())){
                        popupWindow.dismiss();
                        return;
                    }
                }
                break;

            //回放按钮
            case R.id.btn_replay:
                //判断是否在回放
                if (!isReplay){
                    //如果处于停止
                    if (deltaLatlngs.size()>0){
                        //假如回放到最后，则设为 0
                        if (sb_play.getProgress()==sb_play.getMax()){
                            sb_play.setProgress(0);
                        }
                        //设置为回放状态
                        isReplay = true;
                        btn_replay.setText("停止");
                        timer.postDelayed(runnable,10);//延迟0.01秒再执行
                    }
                }else {
                    //如果处于回放状态,移除定时器,设置为停止
                    timer.removeCallbacks(runnable);
                    isReplay=false;
                    btn_replay.setText("回放");
                }
                break;
        }
    }
    
    //根据开始和结束时间获取时间匹配的列表
    private void getDeltaList() {
//        //测试数据--> // TODO: 2017/1/12 这里获取匹配的集合的经纬度集合
//        startTime = 1462198037;
//        endTime = 1462198837;
//        //测试数据-->
        Log.d(TAG, "getDeltaList-" + "startTime-" + startTime + "-endTime-" + endTime);
        //先清空列表
        path_list.clear();
        deltaList.clear();
        deltaLatlngs.clear();
        if (startTime > 0L && endTime > 0L) {
            for (TraceInfo.TraceBean traceBean:traceInfoList) {
                long enter_time=(long) traceBean.getEnter_time();
                long leave_time=(long) traceBean.getLeave_time();
                if (((startTime<=enter_time)&&(enter_time<=endTime))&&((startTime<=leave_time)&&(leave_time<=endTime))){
                    Log.d(TAG,"enter_time=="+enter_time+"--leave_time=="+leave_time);
                    //添加匹配列表
                    deltaList.add(traceBean);
                    //添加经纬度列表
                    LatLng latlng=new LatLng(traceBean.getLatitude(),traceBean.getLongitude());
                    deltaLatlngs.add(latlng);
                }
            }
            //循环完将 deltaList 赋值给 path_list
            path_list.addAll(deltaList);
            //设置 SeekBar 最大值 和当前进度为 0 // TODO: 2017/1/13
            sb_play.setMax(deltaLatlngs.size());
            sb_play.setProgress(0);
            Log.d(TAG,"sb_play.max="+sb_play.getMax());
            Log.d(TAG,"sb_play.progress="+sb_play.getProgress());
            Log.d(TAG,"deltaLatlngs.size()="+deltaLatlngs.size());

            Log.d(TAG, "deltaList--" + deltaList.size());
            Log.d(TAG,"path_list--"+path_list.size());
            Log.d(TAG, "deltaLatlngs--" + deltaLatlngs.size());
        }
    }

    //设选择一周前或者一天前的时间
    private void aWeekORaDay(int i){
        //设置结束时间为当前时间
        Date startDate=new Date();
        long currentTime=startDate.getTime();
        endTime=currentTime/1000L;//获取当前10位时间戳
        Log.d(TAG,"currentTime=="+currentTime+"\n"+"endTime=="+endTime);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentStr=sdf.format(startDate);
        Log.d(TAG,"currentStr=="+currentStr);

        String currentYear=currentStr.substring(0,4);
        String currentMount=currentStr.substring(5,7);
        String currentDay=currentStr.substring(8,10);
        endYear.setText(currentYear);
        endYear.setEnabled(false);
        endMount.setText(currentMount);
        endMount.setEnabled(false);
        endDay.setText(currentDay);
        endDay.setEnabled(false);
        //开始时间
        String startStr="";
        switch (i){
            //一周前的零点开始
            case R.id.rb_week:
                startTime=((currentTime/1000-60*60*24*7)/(60*60*24))*60*60*24-60*60*8;
                Log.d(TAG,"startTime=="+startTime);
                Date weekDate=new Date(startTime*1000L);
                SimpleDateFormat weekSdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startStr=weekSdf.format(weekDate);
                Log.d(TAG,"weekStr=="+startStr);
                break;
            //一天前的零点开始
            case R.id.rb_day:
                startTime=((currentTime/1000-60*60*24)/(60*60*24))*60*60*24-60*60*8;
                Log.d(TAG,"startTime=="+startTime);
                Date dayDate=new Date(startTime*1000L);
                SimpleDateFormat daySdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startStr=daySdf.format(dayDate);
                Log.d(TAG,"dayStr=="+startStr);
                break;

            default:
                break;
        }
        //设置开始时间
        if (!("".equals(startStr))){
            String year=startStr.substring(0,4);
            String mount=startStr.substring(5,7);
            String day=startStr.substring(8,10);
            startYear.setText(year);
            startYear.setEnabled(false);
            startMount.setText(mount);
            startMount.setEnabled(false);
            startDay.setText(day);
            startDay.setEnabled(false);
        }
    }

    //设置为自定义
    private void setCustomUI(){
        startYear.setEnabled(true);
        startYear.setText("");
        startMount.setEnabled(true);
        startMount.setText("");
        startDay.setEnabled(true);
        startDay.setText("");
        endYear.setEnabled(true);
        endYear.setText("");
        endMount.setEnabled(true);
        endMount.setText("");
        endDay.setEnabled(true);
        endDay.setText("");
    }

    //获取自定义的时间换算为时间戳
    private void getCustomTime(){
        //获取开始时间
        String custStart = "";
        if (!StartCustYear.equals("") && !StartCustMount.equals("") && !StartCustDay.equals("")){
            custStart=StartCustYear.trim()+StartCustMount.trim()+StartCustDay.trim();
            Log.d(TAG,"custStart=="+custStart);
        }
        //获取结束时间
        String custEnd = "";
        if (!EndCustYear.equals("") && !EndCustMount.equals("") && !EndCustDay.equals("")){
            custEnd=EndCustYear.trim()+EndCustMount.trim()+EndCustDay.trim();
            Log.d(TAG,"custEnd=="+custEnd);
        }
        if (!custStart.equals("")&&!custEnd.equals("")){
            long startT=getCustom(custStart)/1000;
            Log.d(TAG,"stratT=="+startT);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startD=new Date(startT*1000L);
            Log.d(TAG,"开始:"+sdf.format(startD));

            long endT=getCustom(custEnd)/1000;
            Log.d(TAG,"endT=="+endT);
            Date endD=new Date(endT*1000L);
            Log.d(TAG,"结束：:"+sdf.format(endD));
        }
    }
    private long getCustom(String timeStr){
        SimpleDateFormat cuStart=new SimpleDateFormat("yyyyMMdd");
        Date date;
        long cusTime = 0;
        try {
            date=cuStart.parse(timeStr);
            cusTime=date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cusTime;
    }

    //解析assets目录下的json文件数据
    private List<TraceInfo.TraceBean> getJson(){
        AssetManager assetManager= MyApp.getMyApp().getBaseContext().getAssets();
        try {
            InputStream is=assetManager.open("trace.json");
            InputStreamReader isr=new InputStreamReader(is);
            BufferedReader br=new BufferedReader(isr);
            StringBuffer sb=new StringBuffer();
            String json="";
            while ((json=br.readLine())!=null){
                sb.append(json);
            }
            br.close();
            json=sb.toString().trim();
            Log.d(TAG,"json1::"+json);
            try {
                Gson gson=new Gson();
                TraceInfo traceBeanList = gson.fromJson(json, TraceInfo.class);
                List<TraceInfo.TraceBean> traceList=traceBeanList.getTrace();
                if (traceList!=null){
                    Log.d(TAG,"traceList--"+traceList.size());
                    return traceList;
                }else {
                    Log.d(TAG,"traceList==null");
                    return null;
                }
            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *获去匹配的时间集合后，在地图上显示标记
     */
    private void setAmapMarker(){
        Log.d(TAG,"setAmapMarker");
        if (aMap!=null){
            Log.d(TAG,"aMap");
        }else {
            Log.d(TAG,"aMap==null");
        }
         if (!deltaList.isEmpty()){
//             List<LatLng> lngList=new ArrayList<LatLng>();
             ArrayList<LatLng> lngList=deltaLatlngs;
//             int daltaSize=deltaList.size();
//             double lat;
//             double lon;
//             LatLng latLng;
//             MarkerOptions options;
//             Marker marker;
             LatLng first;
//             for (TraceInfo.TraceBean traceBean:deltaList){
//                 lat=traceBean.getLatitude();
//                 lon=traceBean.getLongitude();
//                 latLng=new LatLng(lat,lon);
////                 lngList.add(latLng);
//                 options=new MarkerOptions();
//                 options.position(latLng);
//                 options.title(String.valueOf(traceBean.getEnter_time()));
//                 options.visible(true);
//                 marker=aMap.addMarker(options);
//                 if (traceBean.equals(deltaList.get(0))){
//                     marker.showInfoWindow();
//                 }
//             }
             first=lngList.get(0);
             aMap.moveCamera(CameraUpdateFactory.changeLatLng(first));
             aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

//             Polyline polyline=aMap.addPolyline(new PolylineOptions()
//                     .addAll(lngList)
//                     .color(Color.rgb(9,129,260))
//                     .width(8f)
//                     .visible(true));

               // TODO: 2017/1/12  setAmapMarker 在第一次成功显示之后，启动回放任务
                runnable=new Runnable() {
                    @Override
                    public void run() {
                        handler.sendMessage(Message.obtain(handler,1));
                    }
                };
         }
    }

    //必须重写
    @Override
    public void onResume() {
        super.onResume();
        map_view.onResume();

        Log.d(TAG,"onResume");
    }

    //必须重写
    @Override
    public void onPause() {
        super.onPause();
        map_view.onPause();

        Log.d(TAG,"onPause");
    }

    //必须重写
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map_view.onSaveInstanceState(outState);

        Log.d(TAG,"onSaveInstanceState");
    }

    //必须重写
    @Override
    public void onDestroy() {
        super.onDestroy();
        map_view.onDestroy();
//        mView=null;

        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myMainActivity = (MyMainActivity) getActivity();

        Log.d(TAG,"onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"onCreate");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG,"onDetach");
    }

    private String StartCustYear;
    private String StartCustMount;
    private String StartCustDay;
    private String EndCustYear;
    private String EndCustMount;
    private String EndCustDay;

    /**
     * 自定义EditText输入监听类
     */
    private class MeditextWatcher implements TextWatcher {

        private EditText editext;

        private MeditextWatcher(EditText mEditext){
            this.editext=mEditext;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (editext.getId()){
                case R.id.et_start_year:
                    StartCustYear= charSequence.toString();
                    Log.d(TAG,"StartCustYear::"+StartCustYear);
                    break;
                case R.id.et_start_mount:
                    StartCustMount= charSequence.toString();
                    Log.d(TAG,"StartCustMount::"+StartCustMount);
                    break;
                case R.id.et_start_day:
                    StartCustDay= charSequence.toString();
                    Log.d(TAG,"StartCustDay::"+StartCustDay);
                    break;
                case R.id.et_end_year:
                    EndCustYear= charSequence.toString();
                    Log.d(TAG,"EndCustYear::"+EndCustYear);
                    break;
                case R.id.et_end_mount:
                    EndCustMount= charSequence.toString();
                    Log.d(TAG,"EndCustMount::"+EndCustMount);
                    break;
                case R.id.et_end_day:
                    EndCustDay= charSequence.toString();
                    Log.d(TAG,"EndCustDay::"+EndCustDay);
                    break;
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private Handler timer=new Handler();//定时器
    private Runnable runnable=null;
    /**
     * 根据定时器线程传递过来指令执行任务
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                int currentPro=sb_play.getProgress();
                if (currentPro < sb_play.getMax()){
//                    isReplay=true; // TODO: 2017/1/12 回放标记
                    sb_play.setProgress(currentPro+1);
                    timer.postDelayed(runnable,300);//延迟0.3秒再执行
                }else {
                    //进度条到达最大值，停止回放任务
                    isReplay=false;
                    btn_replay.setText("回放");
                }
            }
        }
    };

    //根据当前位置画线
    private void drawLine(/*ArrayList<LatLng> list,*/int curretPos){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date enDate;
        Date leDate;
        String enStr;
        String leStr;

        aMap.clear();
//        LatLng replayPos=deltaLatlngs.get(curretPos-1);
//        if (marker!=null){
//            marker.destroy();
//        }

        //添加起点
//        aMap.addMarker(new MarkerOptions()
//                .position(deltaLatlngs.get(0))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)));
        latLng=new LatLng(deltaList.get(0).getLatitude(),deltaList.get(0).getLongitude());
        enDate=new Date(deltaList.get(0).getEnter_time()*1000L);
        leDate=new Date(deltaList.get(0).getLeave_time()*1000L);
        enStr=sdf.format(enDate);
        leStr=sdf.format(leDate);
        marker=aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(enStr)
                .title(leStr)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)));
        marker.showInfoWindow();

//        //添加起点后画线
//        if (delta_path.size()>1){
//            PolylineOptions options=(new PolylineOptions())
//                    .addAll(delta_path)
//                    .color(Color.rgb(9,129,260))
//                    .width(8f);
//            aMap.addPolyline(options);
//        }

        //添加到达的位置点
//        marker=aMap.addMarker(new MarkerOptions()
//                .position(replayPos)
//                .title("在这里")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_here)));
        latLng=new LatLng(deltaList.get(curretPos-1).getLatitude(),deltaList.get(curretPos-1).getLongitude());
        enDate=new Date(deltaList.get(curretPos-1).getEnter_time()*1000L);
        leDate=new Date(deltaList.get(curretPos-1).getLeave_time()*1000L);
        enStr=sdf.format(enDate);
        leStr=sdf.format(leDate);
        marker=aMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(enStr)
                .title(leStr)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_here)));
        marker.showInfoWindow();

//        //添加起点
////        aMap.addMarker(new MarkerOptions()
////                .position(deltaLatlngs.get(0))
////                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)));
//        latLng=new LatLng(deltaList.get(0).getLatitude(),deltaList.get(0).getLongitude());
//        enDate=new Date(deltaList.get(0).getEnter_time()*1000L);
//        leDate=new Date(deltaList.get(0).getLeave_time()*1000L);
//        enStr=sdf.format(enDate);
//        leStr=sdf.format(leDate);
//        marker=aMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .snippet(enStr)
//                .title(leStr)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)));
//        marker.showInfoWindow();

        //添加起点后画线
        if (delta_path.size()>1){
            PolylineOptions options=(new PolylineOptions())
                    .addAll(delta_path)
                    .color(Color.rgb(9,129,260))
                    .width(8f);
            aMap.addPolyline(options);
        }

        //添加终点
        if (delta_path.size()==deltaLatlngs.size()){
            latLng=new LatLng(deltaList.get(deltaList.size()-1).getLatitude(),deltaList.get(deltaList.size()-1).getLongitude());
            enDate=new Date(deltaList.get(deltaList.size()-1).getEnter_time()*1000L);
            leDate=new Date(deltaList.get(deltaList.size()-1).getLeave_time()*1000L);
            enStr=sdf.format(enDate);
            leStr=sdf.format(leDate);
//            aMap.addMarker(new MarkerOptions()
//                    .position(deltaLatlngs.get(deltaLatlngs.size()-1))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)));
            marker=aMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet(enStr)
                    .title(leStr)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)));
            marker.showInfoWindow();
        }
    }

    /**
     * 定义SeekBar拖动监听
     */
    private class MSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        //进度条在变化
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            delta_path.clear();
            if (i != 0 && deltaLatlngs.size()>0){
                //获取画线轨迹 // TODO: 2017/1/13
                for (int j=0;j < sb_play.getProgress();j++){
                    delta_path.add(deltaLatlngs.get(j));
                }
                //画线
                drawLine(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        //停止拖动
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            delta_path.clear();
            int current=seekBar.getProgress();
            if (current != 0 && deltaLatlngs.size()>0){
                //获取画线轨迹
                for (int j=0;j < sb_play.getProgress();j++){
                    delta_path.add(deltaLatlngs.get(j));
                }
                //画线
                drawLine(current);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
