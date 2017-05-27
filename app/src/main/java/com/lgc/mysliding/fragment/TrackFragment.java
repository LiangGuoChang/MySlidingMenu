package com.lgc.mysliding.fragment;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lgc.mysliding.adapter.TraceFeatureAdapter;
import com.lgc.mysliding.adapter.TracePointsAdapter;
import com.lgc.mysliding.bean.TraceBean;
import com.lgc.mysliding.bean.TraceInfo;
import com.lgc.mysliding.bean.TracePoints;
import com.lgc.mysliding.presenter.TracePresenter;
import com.lgc.mysliding.view_interface.TraceInterface;
import com.lgc.mysliding.views.MyEditTextDel;
import com.lgc.mysliding.wheelview.DateUtils;
import com.lgc.mysliding.wheelview.JudgeDate;
import com.lgc.mysliding.wheelview.ScreenInfo;
import com.lgc.mysliding.wheelview.WheelMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrackFragment extends Fragment implements View.OnClickListener, TraceInterface {

    private static final String TAG="TrackFragment";
    private long startTime;//开始时间
    private long endTime;//结束时间
    private boolean isSelected=false;//是否选择时间段
    private boolean isReplay;//设置回放标记

    private View mView;
    private MyMainActivity myMainActivity;
    private ImageView iv_search_trace;
    private PopupWindow popupWindow;
    private View popupView;
//    private Button btn_cancel;
//    private Button btn_ensure;
//    private Button btn_replay;
    private SeekBar sb_play;
    private MapView map_view;
    private AMap aMap;
    private Marker marker=null;//轨迹点
    private LatLng latLng;//轨迹点经纬度
    //解析到的原始数据集合
//    private List<TraceInfo.TraceBean> traceInfoList=new ArrayList<TraceInfo.TraceBean>();
    //匹配时间的集合
//    private List<TraceInfo.TraceBean> deltaList=new ArrayList<TraceInfo.TraceBean>();
    //用于画轨迹点集合
//    private List<TraceInfo.TraceBean> path_list=new ArrayList<TraceInfo.TraceBean>();//暂不使用

    //匹配时间的经纬度集合
    private ArrayList<LatLng> deltaLatlngs=new ArrayList<LatLng>();
    //用于画线的经纬度集合
    private ArrayList<LatLng> delta_path=new ArrayList<LatLng>();
    //轨迹点集合
    private List<TracePoints.TraceBean> mPoints=new ArrayList<TracePoints.TraceBean>();

    private ListView lv_trace;
    private ImageView iv_dismiss_trace;
    private TracePresenter tracePresenter;
    private PopupWindow searchWin;//搜索框
    private ProgressDialog progDialog;//进度框
    private PopupWindow traceWindow;//轨迹弹出框

    private WheelMain wheelMainDate;
    private TextView tv_search_mac;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private PopupWindow listWin;
    private TracePointsAdapter tracePointsAdapter;
    private TextView tv_replay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_track, container, false);
            initView(savedInstanceState);
        }

        //通过presenter层获取json数据
        tracePresenter = new TracePresenter(this);

        //解析assets目录下的json数据
//        traceInfoList=getJson();

        Log.d(TAG,"onCreateView");
        Log.d(TAG,"sb_play--"+sb_play.getProgress());
        return mView;
    }

    //初始化view
    private void initView(Bundle bundle){
        sb_play = (SeekBar) mView.findViewById(R.id.sb_play);
//        btn_replay = (Button) mView.findViewById(R.id.btn_replay);
        tv_replay = (TextView) mView.findViewById(R.id.tv_replay);
        tv_replay.setOnClickListener(this);
        TextView tv_list= (TextView) mView.findViewById(R.id.tv_list);
        tv_list.setOnClickListener(this);
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
//        btn_replay.setOnClickListener(this);
        Log.d(TAG,"initView");

    }

    /**
     * 初始化popupwindow
     */
    private void initPopupWindow(final String strMac){
        popupView = getLayoutInflater(this.getArguments())
                .inflate(R.layout.track_popupwindow,null,false);
        //创建popupWindow,设置高宽，并获取焦点true
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(myMainActivity.findViewById(R.id.relative_tittle));
        //设置popupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //窗口消失，设置时间段选择标志
                isSelected=false;
                iv_search_trace.setImageResource(R.drawable.search_trace_up);

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
        tv_search_mac = (TextView) popupView.findViewById(R.id.tv_search_id);
        tv_search_mac.setText(strMac);
        RadioGroup radioGroup = (RadioGroup) popupView.findViewById(R.id.radio_group);
        RadioButton rb_week = (RadioButton) popupView.findViewById(R.id.rb_week);
        RadioButton rb_day = (RadioButton) popupView.findViewById(R.id.rb_day);
        RadioButton rb_custom = (RadioButton) popupView.findViewById(R.id.rb_custom);
        tv_start_time = (TextView) popupView.findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) popupView.findViewById(R.id.tv_end_time);

        //取消、确定
        Button btn_cancel = (Button) popupView.findViewById(R.id.btn_cancel);
        Button btn_ensure = (Button) popupView.findViewById(R.id.btn_ensure);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //测试数据-->
                startTime = 1462198037;
                endTime = 1494226620;
                //测试数据-->

                //选择了时间段并且时间段不为0才能执行
                if (isSelected){
                    //先清除地图
                    if (aMap!=null){
                        aMap.clear();
                    }

                    /*//获取时间匹配的列表
                    getDeltaList();

                    //在地图上显示标记
                    setAmapMarker();*/

                    //获取轨迹点
                    getTracePoints(strMac,startTime,endTime);
                    //在地图上显示标记
//                    setAmapMarker();

                    //如果开始时间，结束时间不为0才取消窗口
                    if ((startTime>0 && endTime>0) && (popupWindow!=null && popupWindow.isShowing())){
                        popupWindow.dismiss();

                        //隐藏轨迹列表窗体
                        if (traceWindow!=null && traceWindow.isShowing()){
                            traceWindow.dismiss();
                        }
                        //设置回放按钮可以点击
                        /*if (!tv_replay.isEnabled()){
                            tv_replay.setEnabled(true);
                        }*/
                    }
                }else {
                    Toast.makeText(getContext(),"请选择时间段",Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        aWeekORaDay(R.id.rb_week);
                        break;

                    case R.id.rb_day:
                        Log.d(TAG,"选择一天前");
                        aWeekORaDay(R.id.rb_day);
                        break;

                    case R.id.rb_custom:
                        Log.d(TAG,"选择自定义");
                        //先把开始时间和结束时间设为0
                        startTime=endTime=0L;
                        Log.d(TAG,"startTime=="+startTime+"-"+"endTime=="+endTime);
                        setCustomUI();
                        break;
                }
            }
        });

        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottoPopupWindow("选择开始时间",1);
            }
        });
        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottoPopupWindow("选择结束时间",2);
            }
        });

    }

    private java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private void showBottoPopupWindow(final String title, final int timeStyle) {
        WindowManager manager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(getContext()).inflate(R.layout.show_popup_window,null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        ScreenInfo screenInfoDate = new ScreenInfo(getActivity());
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        final String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours,minute);
        final String currentTime = wheelMainDate.getTime().toString();
//        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(myMainActivity.findViewById(R.id.relative_tittle), Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
//        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText(title);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
//                backgroundAlpha(1f);
            }
        });

        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String beginTime = wheelMainDate.getTime().toString();
                try {
                    //获得选择的时间
                    String selectTime=DateUtils.formateStringH(beginTime,DateUtils.yyyyMMddHHmm);
                    if (!TextUtils.isEmpty(selectTime)){
                        if (timeStyle==1){
                            tv_start_time.setText(selectTime);
                            startTime=getSelectTime(selectTime)/1000L;
                            Log.d(TAG,title+"-"+startTime);
                        }else if (timeStyle==2){
                            tv_end_time.setText(selectTime);
                            endTime=getSelectTime(selectTime)/1000L;
                            Log.d(TAG,title+"-"+endTime);
                        }
                    }
                    Log.d(TAG,title+"-"+selectTime);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPopupWindow.dismiss();
//                backgroundAlpha(1f);
            }
        });
    }

    //获取选择的时间的时间戳
    private long getSelectTime(String timeStr){
        SimpleDateFormat cuStart=new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //显示和隐藏popupwindow
            case R.id.iv_search_trace:
                if (searchWin!=null && searchWin.isShowing()){
                    searchWin.dismiss();
                    iv_search_trace.setImageResource(R.drawable.search_trace_up);
                }else {
                    popupSearchWin();
                    iv_search_trace.setImageResource(R.drawable.search_trace_down);
                }
                break;

            //取消按钮
            /*case R.id.btn_cancel:
                if (popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    return;
                }
                break;*/

            //确定按扭
            /*case R.id.btn_ensure:

                //设置回放按钮可以点击
                *//*if (!btn_replay.isEnabled()){
                    btn_replay.setEnabled(true);
                }*//*

                //测试数据-->
                startTime = 1462198037;
                endTime = 1462198837;
                //测试数据-->

                //选择了时间段并且时间段不为0才能执行
                if (isSelected){
                    //先清除地图
                    if (aMap!=null){
                        aMap.clear();
                    }

                    //获取时间匹配的列表
                    getDeltaList();

                    //在地图上显示标记
                    setAmapMarker();
                    //如果开始时间，结束时间不为0才取消窗口
                    if ((startTime>0 && endTime>0) && (popupWindow!=null && popupWindow.isShowing())){
                        popupWindow.dismiss();

                        *//*popupTraceList();//13680739741
                        String url="http://218.15.154.6:8080/feature/query/phone?request={\"phone\":\"13680739741\",\"get_trace_num\":true}";
                        tracePresenter.loadFeatureList(url);*//*

                        //隐藏轨迹列表窗体
                        if (traceWindow!=null && traceWindow.isShowing()){
                            traceWindow.dismiss();
                        }

                        //设置回放按钮可以点击
                        if (!btn_replay.isEnabled()){
                            btn_replay.setEnabled(true);
                        }
                        return;
                    }
                }
                break;*/

            //回放按钮
            /*case R.id.btn_replay:
                Log.d(TAG,"回放-deltaLatlngs.size"*//*+deltaLatlngs.size()*//*);
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
                break;*/
            case R.id.tv_replay:
                if (mPoints!=null && mPoints.size() > 0) {
                    //判断是否在回放
                    if (!isReplay) {
                        //如果处于停止
                        if (deltaLatlngs.size() > 0) {
                            //假如回放到最后，则设为 0
                            if (sb_play.getProgress() == sb_play.getMax()) {
                                sb_play.setProgress(0);
                            }
                            //设置为回放状态
                            isReplay = true;
                            tv_replay.setText("停止");
                            timer.postDelayed(runnable, 10);//延迟0.01秒再执行
                        }
                    } else {
                        //如果处于回放状态,移除定时器,设置为停止
                        timer.removeCallbacks(runnable);
                        isReplay = false;
                        tv_replay.setText("回放");
                    }
                }else {
                    Toast.makeText(getContext(),"未有查询的设备",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_list:
                if (mPoints!=null && mPoints.size() > 0){
                    if (listWin!=null && listWin.isShowing()){
                        listWin.dismiss();
                    }else {
                        showTraceListWin();
                    }
                }else {
                    Toast.makeText(getContext(),"未有查询的设备",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
    
    /*//根据开始和结束时间获取时间匹配的列表
    private void getDeltaList() {
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
    }*/

    // TODO: 2017/5/26 弹出轨迹列表窗
    private void showTraceListWin(){
        View v=getLayoutInflater(getArguments()).inflate(R.layout.trace_points_list,null);
        listWin = new PopupWindow(v,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);
        listWin.setContentView(v);
        listWin.setOutsideTouchable(true);
        listWin.setBackgroundDrawable(new BitmapDrawable());
        //android 7.0 弹出窗另外设置
        if (Build.VERSION.SDK_INT == 24){
            int[] location=new int[2];
            myMainActivity.findViewById(R.id.relative_tittle).getLocationOnScreen(location);
            listWin.showAtLocation(myMainActivity.findViewById(R.id.relative_tittle),
                    Gravity.NO_GRAVITY,
                    0,
                    location[1]+myMainActivity.findViewById(R.id.relative_tittle).getHeight()+15);
        }else {
            listWin.showAsDropDown(myMainActivity.findViewById(R.id.relative_tittle),0,15);
        }
        ListView lv_trace_points= (ListView) v.findViewById(R.id.lv_trace_points);
        if (tracePointsAdapter!=null){
            lv_trace_points.setAdapter(tracePointsAdapter);
        }
        ImageView iv_dimiss_list= (ImageView) v.findViewById(R.id.iv_dismiss_list);
        iv_dimiss_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listWin.dismiss();
            }
        });

    }


    //组拼URL并且获取轨迹点
    private void getTracePoints(String mac,long enter_time,long leave_time){
        String url="http://218.15.154.6:8080/trace?request=";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("query_type","01");
            jsonObject.put("mac",mac);
            jsonObject.put("start_time",enter_time);
            jsonObject.put("end_time",leave_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        url=url.trim()+String.valueOf(jsonObject).trim();
        Log.d(TAG,"轨迹点URL-"+url);
        tracePresenter.loadTracePoints(url);
        showProgressDialog();
    }

    //设选择一周前或者一天前的时间
    private void aWeekORaDay(int i){
        //设置结束时间为当前时间
        Date startDate=new Date();
        long currentTime=startDate.getTime();
        endTime=currentTime/1000L;//获取当前10位时间戳
        Log.d(TAG,"currentTime=="+currentTime+"\n"+"endTime=="+endTime);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentStr=sdf.format(startDate);
        Log.d(TAG,"currentStr=="+currentStr);

        tv_end_time.setText(currentStr);
        tv_end_time.setClickable(false);
        tv_end_time.setEnabled(false);

        //开始时间
        String startStr="";
        switch (i){
            //一周前的零点开始
            case R.id.rb_week:
                startTime=((currentTime/1000-60*60*24*7)/(60*60*24))*60*60*24-60*60*8;
                Log.d(TAG,"startTime=="+startTime);
                Date weekDate=new Date(startTime*1000L);
                SimpleDateFormat weekSdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                startStr=weekSdf.format(weekDate);
                Log.d(TAG,"weekStr=="+startStr);
                break;
            //一天前的零点开始
            case R.id.rb_day:
                startTime=((currentTime/1000-60*60*24)/(60*60*24))*60*60*24-60*60*8;
                Log.d(TAG,"startTime=="+startTime);
                Date dayDate=new Date(startTime*1000L);
                SimpleDateFormat daySdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                startStr=daySdf.format(dayDate);
                Log.d(TAG,"dayStr=="+startStr);
                break;

            default:
                break;
        }
        //设置开始时间
        if (!("".equals(startStr))){
            tv_start_time.setText(startStr);
            tv_start_time.setClickable(false);
            tv_start_time.setEnabled(false);
        }
    }

    //设置为自定义选择时间
    private void setCustomUI(){
        tv_start_time.setEnabled(true);
        tv_start_time.setClickable(true);
        tv_start_time.setText("");
        tv_end_time.setEnabled(true);
        tv_end_time.setClickable(true);
        tv_end_time.setText("");
    }

    //解析assets目录下的json文件数据
    @Nullable
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
     *获取匹配的时间集合后，在地图上显示标记
     */
    private void setAmapMarker(){
        Log.d(TAG,"setAmapMarker");
        if (aMap!=null){
            Log.d(TAG,"aMap!=null");
        }else {
            Log.d(TAG,"aMap==null");
        }
         if (!mPoints.isEmpty()){
             ArrayList<LatLng> lngList=deltaLatlngs;
             LatLng first;
             first=lngList.get(0);
             aMap.moveCamera(CameraUpdateFactory.changeLatLng(first));
             aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

             Log.d(TAG,"放大地图");
             //在第一次成功显示之后，启动回放任务
             runnable=new Runnable() {
                    @Override
                    public void run() {
                        handler.sendMessage(Message.obtain(handler,1));
                    }
                };
         }
    }

    /**
     * 弹出搜索框
     */
    private void popupSearchWin(){
        View searchView=getLayoutInflater(this.getArguments())
                .inflate(R.layout.trace_search_popup,null);
        searchWin = new PopupWindow(searchView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);
        searchWin.setContentView(searchView);
        searchWin.setOutsideTouchable(true);
        searchWin.setBackgroundDrawable(new BitmapDrawable());
        searchWin.showAsDropDown(myMainActivity.findViewById(R.id.relative_tittle));
        final MyEditTextDel et_search_id= (MyEditTextDel) searchView.findViewById(R.id.et_search_id);
        Button btn_search= (Button) searchView.findViewById(R.id.btn_search);
        Button btn_unsearch= (Button) searchView.findViewById(R.id.btn_unsearch);
        searchWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_search_trace.setImageResource(R.drawable.search_trace_up);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_search_id.getText().toString().trim())){
                    String searchUrl=getSearchUrl(et_search_id.getText().toString().trim());
                    popupTraceList();
                    tracePresenter.loadFeatureList(searchUrl);
                    showProgressDialog();//显示进度框
                    searchWin.dismiss();
                }else {
                    Toast.makeText(getContext(),"请输入MAC/手机号",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_unsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchWin.dismiss();
            }
        });
    }

    /**
     * 组拼查询URL
     * @param searchId
     * @return
     */
    private String getSearchUrl(String searchId){
        //http://218.15.154.6:8080/feature/query/phone?request={"phone":"13680739741","get_trace_num":true}
        String searchURL="http://218.15.154.6:8080/feature/query/phone?request=";
        JSONObject searchObject=new JSONObject();
        try {
            searchObject.put("phone",searchId);
            searchObject.put("get_trace_num",true);
            String searchStr=String.valueOf(searchObject);
            searchURL=searchURL.trim()+searchStr.trim();
            Log.d(TAG,"-searchURL-"+searchURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchURL;
    }

    /**
     * 弹出轨迹列表框，显示列表
     */
    private void popupTraceList(){
        View traceView=getLayoutInflater(this.getArguments())
                .inflate(R.layout.trace_mac_list_popup,null);
        traceWindow = new PopupWindow(traceView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);
        traceWindow.setContentView(traceView);
        traceWindow.setOutsideTouchable(true);
        traceWindow.setBackgroundDrawable(new BitmapDrawable());
        //android 7.0 弹出窗另外设置
        if (Build.VERSION.SDK_INT == 24){
            int[] location=new int[2];
            myMainActivity.findViewById(R.id.relative_tittle).getLocationOnScreen(location);
            traceWindow.showAtLocation(myMainActivity.findViewById(R.id.relative_tittle),
                    Gravity.NO_GRAVITY,
                    0,
                    location[1]+myMainActivity.findViewById(R.id.relative_tittle).getHeight()+15);
        }else {
            traceWindow.showAsDropDown(myMainActivity.findViewById(R.id.relative_tittle),0,15);
        }

        lv_trace = (ListView) traceView.findViewById(R.id.lv_trace_list);
        iv_dismiss_trace = (ImageView) traceView.findViewById(R.id.iv_dismiss_trace);
        //取消窗口按钮
        iv_dismiss_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                traceWindow.dismiss();
            }
        });
        //选择
        lv_trace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_mac= (TextView) view.findViewById(R.id.tv_trace_mac);
                String mac=tv_mac.getText().toString().trim();
                initPopupWindow(mac);
            }
        });
    }

    //获得返回的轨迹列表
    @Override
    public void showFeatureList(List<TraceBean.FeatureListBean> feature_list) {
        dissmissProgressDialog();//隐藏进度框
        TraceFeatureAdapter featureAdapter=new TraceFeatureAdapter(getContext(),feature_list);
        lv_trace.setAdapter(featureAdapter);
    }

    //获得某一设备的轨迹点
    @Override
    public void showTracePoints(List<TracePoints.TraceBean> tracePoints) {
        dissmissProgressDialog();//隐藏进度框
        deltaLatlngs.clear();
        mPoints.clear();
//        mPoints=tracePoints;
        mPoints.addAll(tracePoints);
        for (TracePoints.TraceBean traceBean : mPoints){
            LatLng latlng=new LatLng(traceBean.getLatitude(),traceBean.getLongitude());
            deltaLatlngs.add(latlng);
        }

        //创建适配器实例
        tracePointsAdapter = new TracePointsAdapter(getContext(),mPoints);

        Log.d(TAG,"points-"+mPoints.size());
        Log.d(TAG,"deltaLatlngs-"+deltaLatlngs.size());

        sb_play.setMax(deltaLatlngs.size());
        sb_play.setProgress(0);
        //在地图上显示标记
        setAmapMarker();
//        sb_play.setOnSeekBarChangeListener(new MSeekBarChangeListener());
        Log.d(TAG,"sb_play.max="+sb_play.getMax());
        Log.d(TAG,"sb_play.progress="+sb_play.getProgress());
        Log.d(TAG,"deltaLatlngs.size()="+deltaLatlngs.size());

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
        mView=null;
        aMap=null;
        sb_play.setProgress(0);// TODO: 2017/2/13 设置进度为0
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
//        sb_play.setProgress(0);// TODO: 2017/2/13 设置进度为0
        Log.d(TAG,"onDetach");
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
                    tv_replay.setText("回放");
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
        latLng=new LatLng(mPoints.get(0).getLatitude(),mPoints.get(0).getLongitude());
        enDate=new Date(mPoints.get(0).getEnter_time()*1000L);
        leDate=new Date(mPoints.get(0).getLeave_time()*1000L);
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
        latLng=new LatLng(mPoints.get(curretPos-1).getLatitude(),mPoints.get(curretPos-1).getLongitude());
        enDate=new Date(mPoints.get(curretPos-1).getEnter_time()*1000L);
        leDate=new Date(mPoints.get(curretPos-1).getLeave_time()*1000L);
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

            Log.d(TAG,"添加起点后画线");
        }

        //添加终点
        if (delta_path.size()==deltaLatlngs.size()){
            latLng=new LatLng(mPoints.get(mPoints.size()-1).getLatitude(),mPoints.get(mPoints.size()-1).getLongitude());
            enDate=new Date(mPoints.get(mPoints.size()-1).getEnter_time()*1000L);
            leDate=new Date(mPoints.get(mPoints.size()-1).getLeave_time()*1000L);
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

            Log.d(TAG,"添加终点");
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
            if (i != 0 && deltaLatlngs.size()>0 ){
                //获取画线轨迹 // TODO: 2017/1/13
                for (int j=0;j < sb_play.getProgress();j++){
                    delta_path.add(deltaLatlngs.get(j));
                }
                //画线
                drawLine(i);

                Log.d(TAG,"onProgressChanged-i-"+i);
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

            Log.d(TAG,"onStopTrackingTouch-current-"+current);
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

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
//            backgroundAlpha(1f);
        }

    }

}
