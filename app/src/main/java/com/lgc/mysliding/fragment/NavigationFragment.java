package com.lgc.mysliding.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
import com.lgc.mysliding.R;
import com.lgc.mysliding.views.MyEditTextDel;

/**
 * @author lgc 2017/3/22 导航页
 */
public class NavigationFragment extends Fragment implements View.OnClickListener {

    private static String TAG="NavigationFragment";
    private View navigation_view;
    private MyEditTextDel et_navi_num;
    private ImageView iv_search_navi;
    private TextureMapView mapView;
    private AMap aMap;
    private LocationSource.OnLocationChangedListener locationChangedListener;//地图定位回调
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private LatLonPoint mLocationPoint;//定位我的位置
    private Marker mLocationMarker;//定位标志
    private Button btn_navigate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (navigation_view==null){
            navigation_view = inflater.inflate(R.layout.fragment_navigation, container, false);
        }
        initView(savedInstanceState);
        return navigation_view;
    }

    private void initView(Bundle bundle){
        et_navi_num = (MyEditTextDel) navigation_view.findViewById(R.id.et_navi_num);
        iv_search_navi = (ImageView) navigation_view.findViewById(R.id.iv_navi);
        btn_navigate = (Button) navigation_view.findViewById(R.id.btn_navigate);
        mapView = (TextureMapView) navigation_view.findViewById(R.id.t_map_view);
        mapView.onCreate(bundle);
        if (aMap==null){
            aMap = mapView.getMap();
        }
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);
        iv_search_navi.setOnClickListener(this);
        btn_navigate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_navi:
                btn_navigate.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_navigate:
                btn_navigate.setVisibility(View.INVISIBLE);
                break;
        }
    }

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


}
