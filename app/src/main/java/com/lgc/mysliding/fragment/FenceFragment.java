package com.lgc.mysliding.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.MyMainActivity;
import com.lgc.mysliding.adapter.AlertMsgAdapter;
import com.lgc.mysliding.adapter.FenceListAdapter;
import com.lgc.mysliding.bean.AlertMsgBean;
import com.lgc.mysliding.bean.FenceBean;
import com.lgc.mysliding.presenter.FenceListPresenter;
import com.lgc.mysliding.service.AlertMsgService;
import com.lgc.mysliding.view_interface.FenceViewInterface;
import com.lgc.mysliding.views.MyCustomDialog;
import com.lgc.mysliding.views.MyEditTextDel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FenceFragment extends Fragment implements View.OnClickListener, AMap.OnMapClickListener, RadioGroup.OnCheckedChangeListener, FenceViewInterface, AdapterView.OnItemLongClickListener {

    private static final String TAG="FenceFragment";
    //报警三种类型
    private static final int ALERT_IN=0;
    private static final int ALERT_OUT=1;
    private static final int ALERT_INOUT=2;
    private String fenceName;//围栏名称
    private int fenceRadius=0;//围栏半径,初始值为0
    private LatLng fenceLatlng;//围栏圆心
    private double fenceLat;//围栏中心经度
    private double fenceLng;//围栏中心纬度
    private List<String> fencePhoneList=new ArrayList<String>();//监控号码表
    private String fence_phone;//单个监控号码
    private int alertType=-1;//报警类型初始值为0
    private boolean isUpdate=false;//修改围栏标志
    private Circle updateCircle;//修改的围栏圆圈
    private String longClickId;//长按围栏列表项的id
    private String longClickMsg_Id;//长按报警信息的msg_id
    private boolean isSetList;//显示围栏列表或者显示所有围栏
    private View mView;
    private MapView mv_fence;
    private AMap aMap;
    private View popupView;
    private PopupWindow popupWindow;
    private RelativeLayout rl_fence;
    private MyEditTextDel et_fName;
    private MyEditTextDel et_fRadio;
    private RadioGroup rg_alert;
    private RadioButton rb_in;
    private RadioButton rb_out;
    private RadioButton rb_inout;
    private Button btn_ensure;
    private Button btn_cancel;
    private ImageView iv_fence_menu;
    private MyMainActivity mainActivity;
    private View fenceseView;
    private PopupWindow fenceListWin;
    private ImageView iv_dismiss_fence;
    private ListView lv_fenceList;
    private List<FenceBean.FenceListBean> mFenceListBeanList=new ArrayList<>();
    private String urlPath="http://218.15.154.6:8080/fence/get?request={\"username\":\"a\"}";
    private FenceListPresenter fenceListPresenter;
    private FenceListAdapter mFenceListAdapter;
    private Spinner spinner_phones;
    private ImageView iv_addPhone;
    private ArrayAdapter<String> spinnerAdapter;
    private ListView msgListView;
    private AlertMsgService alertMsgService;//报警信息服务
    private List<AlertMsgBean> alertMsgBeanList=new ArrayList<AlertMsgBean>();//报警信息集合
    private LocationSource.OnLocationChangedListener locationChangedListener;//地图定位回调
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_fence, container, false);
            initView(savedInstanceState);
        }

        //通过presenter层去模型层获取json数据
        fenceListPresenter = new FenceListPresenter(this);
        return mView;
    }

    //初始化view
    private void initView(final Bundle bundle){
        rl_fence = (RelativeLayout) mView.findViewById(R.id.rl_fence);
        iv_fence_menu =mainActivity.iv_fenceMenu;
        mv_fence = (MapView) mView.findViewById(R.id.map_view_fence);
        mv_fence.onCreate(bundle);
        if (aMap==null){
            aMap = mv_fence.getMap();
        }
        iv_fence_menu.setOnClickListener(this);
        //地图点击事件
        aMap.setOnMapClickListener(this);
//        //地图定位事件
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);

        /*mainActivity.getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                boolean b=aMap.getUiSettings().isZoomGesturesEnabled();
                Log.d(TAG,"onClosed-"+b);
            }
        });
        mainActivity.getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                boolean b=aMap.getUiSettings().isZoomGesturesEnabled();
                Log.d(TAG,"onOpened-"+b);
            }
        });*/
    }

    //初始化用户输入围栏信息PopupWindow窗口
    private void initPopup(){
        popupView = getLayoutInflater(this.getArguments()).inflate(R.layout.fence_popupwin,null,false);
        //创建PopupWindow，参数true为获取焦点
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //初始化popupView上控件
        et_fName = (MyEditTextDel) popupView.findViewById(R.id.et_fence_name);
        et_fRadio = (MyEditTextDel) popupView.findViewById(R.id.et_fence_radio);
        spinner_phones = (Spinner) popupView.findViewById(R.id.sp_phone_list);
        iv_addPhone = (ImageView) popupView.findViewById(R.id.iv_add_phone);
        rg_alert = (RadioGroup) popupView.findViewById(R.id.rg_alert);
        rb_in = (RadioButton) popupView.findViewById(R.id.rb_in);
        rb_out = (RadioButton) popupView.findViewById(R.id.rb_out);
        rb_inout = (RadioButton) popupView.findViewById(R.id.rb_inout);
        btn_ensure = (Button) popupView.findViewById(R.id.btn_set);
        btn_cancel = (Button) popupView.findViewById(R.id.btn_unset);
        //设置输入框输入监听
        et_fName.addTextChangedListener(new MyEditTextWatcher(et_fName));
        et_fRadio.addTextChangedListener(new MyEditTextWatcher(et_fRadio));
        //设置报警选项选择监听
        rg_alert.setOnCheckedChangeListener(this);
        //点击添加监控号码事件监听
        iv_addPhone.setOnClickListener(this);
        //设置按钮点击事件监听
        btn_ensure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        //popupWindow消失监听事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //将围栏各个参数设置为初始值
                if (isUpdate){
                    Toast.makeText(getContext(),"取消修改",Toast.LENGTH_LONG).show();
                    isUpdate=false;//回到非修改状态
                }
                fenceName=null;
                fenceRadius=0;
                fence_phone=null;
                alertType=-1;
                fencePhoneList.clear();
                Log.d(TAG,"popup-dismiss");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //添加监控号码按钮
            case R.id.iv_add_phone:
                MyCustomDialog myCustomDialog=new MyCustomDialog(getContext(),"请输入监控号码", new MyCustomDialog.onCustomDialogListener() {
                    @Override
                    public void getPhone(String phone) {
                        fencePhoneList.add(phone);
                        Log.d(TAG,"phone--"+phone);
                    }
                });
                myCustomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Log.d(TAG,"myCustomDialog--dismiss");
                        Log.d(TAG,"fencePhoneList-size-"+fencePhoneList.size());
                        //如果监控号码列表大于0，则显示spinner
                        if (fencePhoneList.size()>0){
                            spinnerAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,fencePhoneList);
                            spinner_phones.setAdapter(spinnerAdapter);
                        }
                    }
                });
                myCustomDialog.show();
                break;
            //弹出菜单栏
            case R.id.iv_fence_menu:
                showFenceMenu(iv_fence_menu);
                break;
            //确定按钮事件
            case R.id.btn_set:
                if (fenceName!=null && fenceRadius >0 && fencePhoneList.size()>0 && alertType >=0){
                    //创建围栏
                    if (!isUpdate){
                        //绘制创建的围栏
                        drawFence(fenceLatlng,(double) fenceRadius);
                        //获取组拼的url
                        String createUrl=createFence();
                        //创建新围栏到服务器
                        fenceListPresenter.CRUDFence(createUrl);
                        Log.d(TAG,"确定按钮-createUrl"+"\n"+createUrl);
                        Toast.makeText(getActivity().getApplicationContext(),"创建成功"
                                , Toast.LENGTH_SHORT).show();
                    } else { //修改围栏
                        //先清除修改前的围栏
                        Circle before=getUpdateCircle();
                        before.remove();
                        //绘制修改的围栏
                        drawFence(fenceLatlng,(double) fenceRadius);
                        //获取组拼的修改url
                        String updateUrl=updateFence();
                        //修改围栏到服务器
                        fenceListPresenter.CRUDFence(updateUrl);
                        Log.d(TAG,"确定按钮-updateUrl"+"\n"+updateUrl);
                        Toast.makeText(getActivity().getApplicationContext(),"修改成功"
                                , Toast.LENGTH_SHORT).show();
                    }
                    if (popupWindow!=null && popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"请输入详细围栏信息"
                            , Toast.LENGTH_SHORT).show();
                }
                break;
            //取消按钮事件
            case R.id.btn_unset:
                if (popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                break;
            //取消围栏列表按钮
            case R.id.iv_dismiss_fence:
                if (fenceListWin!=null && fenceListWin.isShowing()){
                    fenceListWin.dismiss();
                }
                break;
         }
    }

    //选择报警选项选择监听
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rb_in:
                alertType=ALERT_IN;
                break;
            case R.id.rb_out:
                alertType=ALERT_OUT;
                break;
            case R.id.rb_inout:
                alertType=ALERT_INOUT;
                break;
        }
        Log.d(TAG,"报警类型alertType--"+alertType);
    }

    //弹出电子围栏右上角菜单
    private void showFenceMenu(View view){
        View v=LayoutInflater.from(getContext()).inflate(R.layout.fence_menu,null);
        final PopupWindow pw=new PopupWindow(v,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.showAsDropDown(view);
        TextView add_fence=(TextView)v.findViewById(R.id.tv_add_fence);
        TextView show_all_fences= (TextView) v.findViewById(R.id.tv_fence_list);
        TextView show_fence=(TextView)v.findViewById(R.id.tv_show_fence);
        TextView show_alert=(TextView)v.findViewById(R.id.tv_show_alert);
        //添加围栏
        add_fence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                Log.d(TAG,"添加围栏");
                Toast.makeText(getContext(),"选择地图位置添加围栏",Toast.LENGTH_SHORT).show();
            }
        });
        //显示围栏列表
        show_fence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                Log.d(TAG,"选择围栏列表");
                if (fenceListWin != null && fenceListWin.isShowing()){
                    fenceListWin.dismiss();
                }else {
                    popupFenceWin();
//                    fenceListWin.showAtLocation(rl_fence,Gravity.CENTER,0,0);
                    isSetList=true;//显示围栏列表标志
                }
                //获取服务器围栏列表数据
                fenceListPresenter.loadFenceList(urlPath);
//                mFenceListAdapter = new FenceListAdapter(getContext(),mFenceListBeanList);
//                lv_fenceList.setAdapter(mFenceListAdapter);
            }
        });
        //显示报警信息
        show_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                Log.d(TAG,"选择报警信息");
                popupAlertMsgWin();
            }
        });
        //显示所有围栏
        show_all_fences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                Log.d(TAG,"显示所有围栏");
                isSetList=false;//显示所有围栏
                //获取服务器所有围栏数据
                fenceListPresenter.loadFenceList(urlPath);
            }
        });
    }

    //显示围栏列表
    private void popupFenceWin(){
        fenceseView = getLayoutInflater(this.getArguments())
                .inflate(R.layout.fence_list_popupwin,null);
        fenceListWin = new PopupWindow(fenceseView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        fenceListWin.setContentView(fenceseView);
        fenceListWin.setOutsideTouchable(true);
        fenceListWin.setFocusable(true);
        fenceListWin.setBackgroundDrawable(new BitmapDrawable());
//        fenceListWin.showAtLocation(rl_fence,Gravity.CENTER,0,0);
        fenceListWin.showAsDropDown(mainActivity.findViewById(R.id.relative_tittle));
        iv_dismiss_fence = (ImageView) fenceseView.findViewById(R.id.iv_dismiss_fence);
        lv_fenceList = (ListView) fenceseView.findViewById(R.id.lv_fence_list);
        //长按列表项事件监听
        lv_fenceList.setOnItemLongClickListener(this);
        //取消围栏列表窗口按钮点击事件监听
        iv_dismiss_fence.setOnClickListener(this);
    }

    //地图点击事件，弹出用户添加围栏popupWindow窗口
    @Override
    public void onMapClick(LatLng latLng) {
        if (popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }else {
            initPopup();
            popupWindow.showAtLocation(rl_fence,Gravity.CENTER,0,0);
            Log.d(TAG,"popup-showing");
        }
        //获取点击位置的经纬度
        fenceLatlng=latLng;
        fenceLat=latLng.latitude;
        fenceLng=latLng.longitude;
        Log.d(TAG,"fenceLat--"+fenceLat+"\n"+"fenceLng--"+fenceLng);
    }

    //在高德地图上画围栏圆圈
    private Circle drawFence(LatLng latLng,double radio){
        Circle createCircle=aMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radio)
                .fillColor(Color.argb(100,247,101,101))
                .strokeColor(Color.argb(100,101,101,101))
                .strokeWidth(1));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        return createCircle;
    }

    //根据用户输入的围栏信息组拼URL路径,访问服务器,创建新的围栏
    private String createFence(){
        String createURL="http://218.15.154.6:8080/fence/update?request=";
        try {
            JSONObject createJson=new JSONObject();
            JSONArray phoneArray=new JSONArray();
            for (String str:fencePhoneList) {
                phoneArray.put(str);
            }
            createJson.put("username","a");
            createJson.put("name",fenceName);
            createJson.put("longitude",fenceLng);
            createJson.put("latitude",fenceLat);
            createJson.put("radius",fenceRadius);
            createJson.put("phone_list",phoneArray);
            createJson.put("type",alertType);
            String createStr= String.valueOf(createJson);
            createURL=createURL.trim()+createStr.trim();
            Log.d(TAG,"createStr--"+"\n"+createStr);
            Log.d(TAG,"createURL--"+"\n"+createURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return createURL;
    }

    //根据用户输入的围栏信息组拼URL路径,访问服务器,修改围栏
    private String updateFence(){
        String updateURL="http://218.15.154.6:8080/fence/update?request=";
        try {
            JSONObject updateJson=new JSONObject();
            JSONArray phoneArray=new JSONArray();
            for (String str:fencePhoneList) {
                phoneArray.put(str);
            }
            updateJson.put("id",getLongClickId());
            updateJson.put("username","a");
            updateJson.put("name",fenceName);
            updateJson.put("longitude",fenceLng);
            updateJson.put("latitude",fenceLat);
            updateJson.put("radius",fenceRadius);
            updateJson.put("phone_list",phoneArray);
            updateJson.put("type",alertType);
            String updateStr= String.valueOf(updateJson);
            updateURL=updateURL.trim()+updateStr.trim();
            Log.d(TAG,"updateStr--"+"\n"+updateStr);
            Log.d(TAG,"updateURL--"+"\n"+updateURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateURL;
    }

    //根据用户输入的围栏信息组拼URL路径,访问服务器,修改围栏
    private String deleteFence(){
        String deleteURL="http://218.15.154.6:8080/fence/update?request=";
        try {
            JSONObject deleteJson=new JSONObject();
            deleteJson.put("id",getLongClickId());
            String deleteStr= String.valueOf(deleteJson);
            deleteURL=deleteURL.trim()+deleteStr.trim();
            Log.d(TAG,"deleteStr--"+"\n"+deleteStr);
            Log.d(TAG,"deleteURL--"+"\n"+deleteURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deleteURL;
    }


    //得到服务器围栏信息，显示围栏列表
    @Override
    public void showFenceList(List<FenceBean.FenceListBean> fenceListBeen) {
        mFenceListBeanList=fenceListBeen;
        Log.d(TAG,"mFenceListBeanList-size-"+mFenceListBeanList.size());
        if (isSetList){
            //展示围栏列表
            mFenceListAdapter = new FenceListAdapter(getContext(),fenceListBeen);
            lv_fenceList.setAdapter(mFenceListAdapter);
        }else {
            //在地图上绘制所有围栏
            drawAllFence(fenceListBeen);
        }
    }

    //绘制所有围栏
    private void drawAllFence(List<FenceBean.FenceListBean> fenceListBeen){
        for (FenceBean.FenceListBean fenceBean:fenceListBeen) {
//            drawFence(new LatLng(fenceBean.getLatitude(),fenceBean.getLongitude()),
//                    (double) fenceBean.getRadius());
            aMap.addCircle(new CircleOptions()
                    .center(new LatLng(fenceBean.getLatitude(),fenceBean.getLongitude()))
                    .radius((double) fenceBean.getRadius())
                    .fillColor(Color.argb(100,247,101,101))
                    .strokeColor(Color.argb(100,101,101,101))
                    .strokeWidth(1));
        }

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(fenceListBeen.get(0).getLatitude(),fenceListBeen.get(0).getLongitude())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    //对围栏进行增删改时获取服务器返回的信息
    @Override
    public void showCRUDResult(String result) {
        Log.d(TAG,"增删改返回的信息-"+"\n"+result);
    }

    //围栏列表项长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        FenceBean.FenceListBean fenceListBean=mFenceListBeanList.get(i);
        setLongClickId(fenceListBean.getId());
        showFenceMenuDialog(fenceListBean);
//        setLongClickId(fenceListBean.getId());
        Log.d(TAG,"onItemLongClick-"+i);
        Log.d(TAG,"onItemLongClick-"+fenceListBean.getId());
        return false;
    }

    //长按围栏列表弹出菜单
    private void showFenceMenuDialog(final FenceBean.FenceListBean fenceListBean){
        LayoutInflater inflater=LayoutInflater.from(getContext());
        final View dialogView=inflater.inflate(R.layout.fence_list_dialog,null);
        TextView updFence= (TextView) dialogView.findViewById(R.id.tv_update_fence);
        TextView delFence= (TextView) dialogView.findViewById(R.id.tv_del_fence);
        final AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setView(dialogView);
        final AlertDialog fenceMenuDia=mBuilder.create();
        fenceMenuDia.show();
        //修改围栏
        updFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng updateLatLng=new LatLng(fenceListBean.getLatitude(),fenceListBean.getLongitude());
                //取消列表窗口
                if (fenceListWin != null && fenceListWin.isShowing()){
                    fenceListWin.dismiss();
                }
                //绘画修改前的围栏
                Circle mUpdateCircle=drawFence(updateLatLng,(double) fenceListBean.getRadius());
                setUpdateCircle(mUpdateCircle);
                //设置修改标志
                isUpdate=true;
                Toast.makeText(getContext(),"点击地图进行修改",Toast.LENGTH_LONG).show();
                fenceMenuDia.dismiss();
            }
        });
        //删除围栏
        delFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取组拼的删除的url
                String deleteUrl=deleteFence();
                //删除围栏到服务器
                fenceListPresenter.CRUDFence(deleteUrl);
                Log.d(TAG,"确定按钮-deleteUrl"+"\n"+deleteUrl);
                //获取服务器围栏列表数据
                fenceListPresenter.loadFenceList(urlPath);
                Toast.makeText(getContext(),"删除成功", Toast.LENGTH_SHORT).show();
                fenceMenuDia.dismiss();
            }
        });
    }

    //弹出显示报警信息的窗口
    private void popupAlertMsgWin(){
        View alertView=getLayoutInflater(this.getArguments())
                .inflate(R.layout.fence_alert_msg,null);
        final PopupWindow alertMsgWin=new PopupWindow(alertView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        alertMsgWin.setContentView(alertView);
        alertMsgWin.setOutsideTouchable(true);
        alertMsgWin.setFocusable(true);
        alertMsgWin.setBackgroundDrawable(new BitmapDrawable());
//        alertMsgWin.showAtLocation(rl_fence,Gravity.CENTER,0,0);
        alertMsgWin.showAsDropDown(mainActivity.findViewById(R.id.relative_tittle));
        ImageView iv_no_data= (ImageView) alertView.findViewById(R.id.iv_no_data);
        msgListView = (ListView)alertView.findViewById(R.id.push_list);
        alertView.findViewById(R.id.iv_dismiss_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMsgWin.dismiss();
            }
        });
        //获取报警信息服务实例
        alertMsgService = AlertMsgService.getMsgServiceInstance(getContext());
        int msgCount=alertMsgService.getMsgCount();
        if (msgCount!=0){
            iv_no_data.setVisibility(View.GONE);
        }
        alertMsgBeanList=alertMsgService.getScrollData();
        AlertMsgAdapter alertMsgAdapter=new AlertMsgAdapter(getContext());
        alertMsgAdapter.setAlertMsgBeanList(alertMsgBeanList);
        msgListView.setAdapter(alertMsgAdapter);
        //列表长按事件
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertMsgBean alertMsgBean=alertMsgService.getScrollData().get(i);
                showAlertDialog(alertMsgBean);
                Log.d(TAG,"长按-msg_id-"+alertMsgBean.getMsg_id());
                return false;
            }
        });
    }

    //长按报警信息列表弹出菜单
    private void showAlertDialog(final AlertMsgBean alertMsgBean){
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View dialogView=inflater.inflate(R.layout.fence_alert_dialog,null);
        TextView see_alert= (TextView) dialogView.findViewById(R.id.tv_see_alert);
        TextView del_alert= (TextView) dialogView.findViewById(R.id.tv_del_alert);
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setView(dialogView);
        final AlertDialog alertMenuDia=mBuilder.create();
        alertMenuDia.show();
        final String msg_id= String.valueOf(alertMsgBean.getMsg_id());
        //弹出框消失时更新列表
        /*alertMenuDia.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                List<AlertMsgBean> alertMsgList=AlertMsgService.getMsgServiceInstance(getContext()).getScrollData();
                AlertMsgAdapter alertMsgAdapter=new AlertMsgAdapter(getContext());
                alertMsgAdapter.setAlertMsgBeanList(alertMsgList);
                msgListView.setAdapter(alertMsgAdapter);
            }
        });*/
        //查看报警信息
        see_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMenuDia.dismiss();
            }
        });
        //删除报警信息
        del_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int del_line=AlertMsgService.getMsgServiceInstance(getContext()).deletOne(msg_id);
                //更新列表
                List<AlertMsgBean> alertMsgList=AlertMsgService.getMsgServiceInstance(getContext()).getScrollData();
                AlertMsgAdapter alertMsgAdapter=new AlertMsgAdapter(getContext());
                alertMsgAdapter.setAlertMsgBeanList(alertMsgList);
                msgListView.setAdapter(alertMsgAdapter);

                alertMenuDia.dismiss();
                Toast.makeText(getContext(),"删除id为："+msg_id+"的报警信息",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"删除-msg_id-"+msg_id+"\n"+"del_line-"+del_line);
            }
        });
    }

    private Circle getUpdateCircle() {
        return updateCircle;
    }

    private void setUpdateCircle(Circle updateCircle) {
        this.updateCircle = updateCircle;
    }

    private String getLongClickId() {
        return longClickId;
    }

    private void setLongClickId(String longClickId) {
        this.longClickId = longClickId;
    }

    private String getLongClickMsg_Id() {
        return longClickMsg_Id;
    }

    private void setLongClickMsg_Id(String longClickMsg_Id) {
        this.longClickMsg_Id = longClickMsg_Id;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG,"onAttach");
        super.onAttach(context);
        mainActivity = (MyMainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mv_fence.onResume();
//        if (aMap==null){
//            aMap = mv_fence.getMap();
//        }

        //地图定位事件
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);
        //启动定位
        aMap.clear();
        mapLocationClient.startLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mv_fence.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
        mv_fence.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mv_fence.onDestroy();
//        locationChangedListener=null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        //mView和aMap同时设为null，切换页面后回来才不出错
        mView=null;
        aMap=null;
    }

    /**
     * 地图定位声明
     */
    private LocationSource mLocationSource=new LocationSource() {
        //启动定位
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            locationChangedListener = onLocationChangedListener;
            if (mapLocationClient==null){
                mapLocationClient=new AMapLocationClient(getContext());
                mapLocationClientOption=new AMapLocationClientOption();
                //设置客户端定位监听回调
                mapLocationClient.setLocationListener(aMapLocationListener);
                //设置客户端监听参数
                mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                mapLocationClientOption.setNeedAddress(true);//返回地址信息
                mapLocationClientOption.setWifiScan(true);//强行刷新WiFi
                mapLocationClientOption.setMockEnable(false);//不允许模拟位置
                mapLocationClientOption.setOnceLocation(true);//是否定位一次
//                mapLocationClientOption.setInterval(5000);//定位时间间隔
                mapLocationClient.setLocationOption(mapLocationClientOption);
                //启动监听
                mapLocationClient.startLocation();
                Log.d(TAG,"activate开始定位");
            }
        }

        //停止定位
        @Override
        public void deactivate() {
            locationChangedListener=null;
            if (mapLocationClient!=null){
                mapLocationClient.stopLocation();
                Log.d(TAG,"deactivate停止定位");
            }
            mapLocationClient=null;
        }
    };
    //客户端定位监听回调
    private AMapLocationListener aMapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (locationChangedListener!=null && aMapLocation!=null){
                if (aMapLocation.getErrorCode()==0){
                    //获取当前位置
                    LatLng latLng=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    //显示当前定位
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                    MarkerOptions options=new MarkerOptions();
                    options.position(latLng);
                    options.title("您的位置");
                    options.visible(true);
                    BitmapDescriptor bd= BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.loc_current));
                    options.icon(bd);
                    //设置当前位置标记
                    Marker marker=aMap.addMarker(options);
                    marker.showInfoWindow();
                    Log.d(TAG,"定位成功");
                }else {
                    Log.d(TAG,"定位失败--"+"error code--"+aMapLocation.getErrorCode()+"\n"+
                            "error info--"+aMapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 自定义输入框输入监听类
     */
    private class MyEditTextWatcher implements TextWatcher{

        private MyEditTextDel myEditTextDel;

        private MyEditTextWatcher(MyEditTextDel editTextDel){
            this.myEditTextDel=editTextDel;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (myEditTextDel.getId()){
                case R.id.et_fence_name:
                    fenceName=charSequence.toString();
                    Log.d(TAG,"fenceName--"+fenceName);
                    break;
                case R.id.et_fence_radio:
                    if (!TextUtils.isEmpty(charSequence.toString())){
                        fenceRadius=Integer.valueOf(charSequence.toString());
                    }
                    Log.d(TAG,"fenceRadius--"+fenceRadius);
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (TextUtils.isEmpty(myEditTextDel.getText().toString())){
                switch (myEditTextDel.getId()){
                    case R.id.et_fence_name:
                        fenceName=null;
                        Log.d(TAG,"a-fenceName--"+fenceName);
                        break;
                    case R.id.et_fence_radio:
                        fenceRadius=0;
                        Log.d(TAG,"a-fenceRadius--"+fenceRadius);
                        break;
                }
            }
        }
    }


}
