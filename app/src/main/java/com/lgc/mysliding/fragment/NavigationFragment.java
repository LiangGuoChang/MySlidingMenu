package com.lgc.mysliding.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.lgc.mysliding.AmapNavigation.GPSNaviActivity;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.MyMainActivity;
import com.lgc.mysliding.bean.TraceBean;
import com.lgc.mysliding.bean.TracePoints;
import com.lgc.mysliding.presenter.TracePresenter;
import com.lgc.mysliding.view_interface.TraceInterface;
import com.lgc.mysliding.views.MyEditTextDel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lgc 2017/3/22 导航页
 */
public class NavigationFragment extends Fragment implements View.OnClickListener, TraceInterface {

    private static String TAG="NavigationFragment";
    private View navigation_view;
    private TextureMapView mapView;
    private AMap aMap;
    private LocationSource.OnLocationChangedListener locationChangedListener;//地图定位回调
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private LatLonPoint mLocationPoint;//定位我的位置
    private Marker mLocationMarker;//定位标志
    private Button btn_navigate;
    private String search_id;
    private MyMainActivity mainActivity;
    private ImageView iv_search_navi;
    private PopupWindow naviWin;
    private MyEditTextDel et_input;
    private int inputType;
    private TracePresenter tracePresenter;
    private ProgressDialog progDialog;//进度框
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private int mRouteType=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (navigation_view==null){
            navigation_view = inflater.inflate(R.layout.fragment_navigation, container, false);
        }
        initView(savedInstanceState);
        tracePresenter = new TracePresenter(this);
        return navigation_view;
    }

    private void initView(Bundle bundle){
        btn_navigate = (Button) navigation_view.findViewById(R.id.btn_navigate);
        mapView = (TextureMapView) navigation_view.findViewById(R.id.t_map_view);
        iv_search_navi = mainActivity.iv_navi_search;
        mapView.onCreate(bundle);
        if (aMap==null){
            aMap = mapView.getMap();
        }
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);
        btn_navigate.setOnClickListener(this);
        iv_search_navi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_navigate:
//            btn_navigate.setVisibility(View.INVISIBLE);
                if (mStartPoint!=null && mEndPoint!=null){
                    Intent start=new Intent(getContext(), GPSNaviActivity.class);
                    Bundle naviBundle=new Bundle();
                    naviBundle.putParcelable("startPoint",mStartPoint);
                    naviBundle.putParcelable("endPoint",mEndPoint);
                    naviBundle.putInt("route_type",mRouteType);
                    start.putExtras(naviBundle);
                    startActivity(start);
                }else {
                    Toast.makeText(getContext(),"起点或终点不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_navigate_search:
                if (naviWin!=null && naviWin.isShowing()){
                    naviWin.dismiss();
                }else {
                    popupNavigate();
                }
                break;
        }
    }

    //弹出输入目标MAC/手机号窗口
    private void popupNavigate(){
        View view=getLayoutInflater(this.getArguments())
                .inflate(R.layout.navigation_popupwin,null);
        naviWin = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true);
        naviWin.setContentView(view);
        naviWin.setOutsideTouchable(true);
        naviWin.setBackgroundDrawable(new BitmapDrawable());
        naviWin.showAsDropDown(getActivity().findViewById(R.id.relative_tittle));
        et_input = (MyEditTextDel) view.findViewById(R.id.et_input);
        RadioGroup rg_input= (RadioGroup) view.findViewById(R.id.rg_input_type);
        Button btn_search= (Button) view.findViewById(R.id.btn_navi_search);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel_search);
        rg_input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_mac:
                        if (!TextUtils.isEmpty(et_input.getText().toString())){
                            et_input.setText("");
                        }
                        et_input.setVisibility(View.VISIBLE);
                        et_input.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD|EditorInfo.TYPE_CLASS_NUMBER);
                        et_input.setHint("输入MAC地址");
                        InputFilter[] macFilters={new InputFilter.LengthFilter(12)};
                        et_input.setFilters(macFilters);
                        inputType=1;
                        break;
                    case R.id.rb_phone:
                        if (!TextUtils.isEmpty(et_input.getText().toString())){
                            et_input.setText("");
                        }
                        et_input.setVisibility(View.VISIBLE);
                        et_input.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                        et_input.setHint("输入手机号码");
                        InputFilter[] phFilters={new InputFilter.LengthFilter(11)};
                        et_input.setFilters(phFilters);
                        inputType=2;
                        break;
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (inputType){
                    case 1:
                        String mac=et_input.getText().toString().trim();
                        if (mac.length() < 12){
                            Toast.makeText(getContext(),"MAC地址不完整",Toast.LENGTH_SHORT).show();
                        }else {
                            String macUrl=getSearchURL(inputType,mac);
                            macUrl="http://218.15.154.6:8080/trace?request={%22query_type%22:%2201%22,%22mac%22:%221c77f60352c8%22,%22start_time%22:1462198037,%22end_time%22:1494226620,%22limit%22:1}";
                            tracePresenter.loadTracePoints(macUrl);
                            if (naviWin!=null && naviWin.isShowing()){
                                naviWin.dismiss();
                            }
                            showProgressDialog();
                        }
                        break;
                    case 2:
                        String phone=et_input.getText().toString().trim();
                        if (phone.length() < 11){
                            Toast.makeText(getContext(),"手机号码不完整",Toast.LENGTH_SHORT).show();
                        }else {
                            String phoneUrl=getSearchURL(inputType,phone);
                            phoneUrl="http://218.15.154.6:8080/trace?request={%22query_type%22:%2202%22,%22phone%22:%2215200906159%22,%22start_time%22:1462198037,%22end_time%22:1494226620,%22limit%22:1}";
                            tracePresenter.loadTracePoints(phoneUrl);
                            if (naviWin!=null && naviWin.isShowing()){
                                naviWin.dismiss();
                            }
                            showProgressDialog();
                        }
                        break;
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (naviWin!=null && naviWin.isShowing()){
                    naviWin.dismiss();
                }
            }
        });
    }

    //获取查询目标的URL
    private String getSearchURL(int type,String id){
        String url="http://218.15.154.6:8080/trace?request=";
        try {
            switch (type){
                case 1:
                    JSONObject macObject=new JSONObject();
                    macObject.put("query_type","01");
                    macObject.put("mac",id);
                    macObject.put("limit",1);
                    url=url.trim()+String.valueOf(macObject).trim();
                    Log.d(TAG,"查询MAC-URL-"+url);
                    break;
                case 2:
                    JSONObject phObject=new JSONObject();
                    phObject.put("query_type","02");
                    phObject.put("phone",id);
                    phObject.put("limit",1);
                    url=url.trim()+String.valueOf(phObject).trim();
                    Log.d(TAG,"查询phone-URL-"+url);
                    break;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    //输入监听
    private TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input=charSequence.toString().trim().toLowerCase();
            Log.d(TAG,"输入::"+input);
            if (input.length()==12){
                Pattern p1=Pattern.compile("[a-z]");
                Matcher m1=p1.matcher(input);
                if (m1.matches()){
                    Log.d(TAG,"输入::"+input+"-含有字母-mac地址");
                }
            }else if (input.length()==11){
                Pattern p=Pattern.compile("[a-z]");
                Matcher m=p.matcher(input);
                if (!m.matches()){
                    Log.d(TAG,"输入::"+input+"-没有字母-电话号码");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * 定位声明
     */
    private com.amap.api.maps.LocationSource mLocationSource=new com.amap.api.maps.LocationSource() {
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
                mapLocationClientOption.setOnceLocation(false);//是否定位一次
                mapLocationClientOption.setInterval(300000);//定位时间间隔为5分钟
                mapLocationClient.setLocationOption(mapLocationClientOption);
                //启动监听
                mapLocationClient.startLocation();
                Log.d(TAG,"activate开始定位");
            }
        }

        @Override
        public void deactivate() {
            Log.d(TAG,"deactivate停止定位");
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
                    mLocationPoint=new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    mStartPoint=mLocationPoint;
                    //显示当前定位
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));
                    if (mLocationMarker!=null){
                        mLocationMarker.remove();
                    }
                    MarkerOptions options=new MarkerOptions();
                    options.position(latLng);
                    options.title("您的位置");
                    options.visible(true);
                    BitmapDescriptor bd= BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.my_location));
                    options.icon(bd);
                    //设置当前位置标记
                    mLocationMarker = aMap.addMarker(options);
//                    mLocationMarker.showInfoWindow();
                    Log.d(TAG,"定位成功");
                }else {
                    Log.d(TAG,"定位失败--"+"error code--"+aMapLocation.getErrorCode()+"\n"+
                            "error info--"+aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MyMainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

       /* //地图定位事件
        aMap.clear();
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);
        //启动定位
        mapLocationClient.startLocation();*/
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapView=null;
        aMap=null;
    }

    @Override
    public void showFeatureList(List<TraceBean.FeatureListBean> feature_list) {

    }

    /**
     * 返回轨迹点
     * @param tracePoints
     */
    @Override
    public void showTracePoints(List<TracePoints.TraceBean> tracePoints) {
        dissmissProgressDialog();
        TracePoints.TraceBean traceBean=tracePoints.get(tracePoints.size()-1);
        LatLonPoint latLon=new LatLonPoint(traceBean.getLatitude(),traceBean.getLongitude());
        mEndPoint=latLon;
        btn_navigate.setVisibility(View.VISIBLE);
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

}
