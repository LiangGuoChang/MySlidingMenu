package com.lgc.mysliding.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.MInfoWindowAdapter;
import com.lgc.mysliding.bean.DetectorLists;
import com.lgc.mysliding.bean.Gps;
import com.lgc.mysliding.utils.PositionUtil;

import java.util.List;

public class AMapFragmentActivity extends FragmentActivity {
    private static final String TAG="AMapFragmentActivity";
    private AMap aMap;
//    private LocationSource.OnLocationChangedListener locationChangedListener;
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;

//    private List<DetectorInfoBean.DeviceListBean> deviceListBeen;

    private List<DetectorLists.DetectorListBean> mDeviceListBeen;
//    private List<MarkerOptions> markerOptions;

    private double lat;
    private double lon;
    private String select_mac;
    private MInfoWindowAdapter mInfoWindowAdapter;//自定义的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_fragment);
        setUpMApIfNeeded();

        savedInstanceState=getIntent().getExtras();
        select_mac=savedInstanceState.getString("select_mac");
        Log.d(TAG,"select_mac---"+select_mac);
        //描点
        initMarker();
        //设置定位监听
        aMap.setLocationSource(mLocationSource);
        aMap.setMyLocationEnabled(true);

//        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(24.291004, 116.099656)));
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(28));
    }

    //获取解析的数据，进行描点
    private void initMarker(){
        //获取解析的数据，进行描点
        MyApp myApp= (MyApp) getApplicationContext();
        mDeviceListBeen=myApp.getDeviceListBeen();
        int deviceSize=mDeviceListBeen.size();
        Log.d(TAG,"deviceSize--"+deviceSize);

        LatLng select = null;
        if (deviceSize>0){
            for (int i=0;i<deviceSize;i++){
                String mac=mDeviceListBeen.get(i).getMac();
                Log.d(TAG,"deviceListBeen--"+mac);

                lat=mDeviceListBeen.get(i).getLatitude();
                lon=mDeviceListBeen.get(i).getLongitude();
                Gps gps=PositionUtil.gps84_To_Gcj02(lat,lon);
                LatLng latLng = null;
                if (gps != null) {
                    latLng = new LatLng(gps.getWgLat(),gps.getWgLon());
                }
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                if (mDeviceListBeen.get(i).getStatus().equals("01")){//正常
                    options.period(1);
                    BitmapDescriptor bd = BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.on_line));
                    options.icon(bd);
                }else if (mDeviceListBeen.get(i).getStatus().equals("02")){//离线
                    options.period(2);
                    BitmapDescriptor bd = BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.off_line));
                    options.icon(bd);
                }
                options.title(mDeviceListBeen.get(i).getMac());//mac地址
                options.snippet(mDeviceListBeen.get(i).getAddress());//位置信息
                options.visible(true);

                if (mac.equals(select_mac)) {
                    select=latLng;
                    /*BitmapDescriptor bd = BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.oselect));
                    options.icon(bd);*/
                    Log.d(TAG,"select--"+mac.equals(select_mac));

                    Marker marker = aMap.addMarker(options);
                    marker.showInfoWindow();
                }else {
                    Marker marker = aMap.addMarker(options);
                }
            }
            //显示当前选择的探针
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(select));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(24));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMApIfNeeded();
    }

    private void setUpMApIfNeeded(){
        mInfoWindowAdapter = new MInfoWindowAdapter();
        if (aMap==null){
            aMap=((SupportMapFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.map_fragment_id))
                    .getMap();
        }
        //设置自定义的适配器
        aMap.setInfoWindowAdapter(mInfoWindowAdapter);
    }

    /**
     * 声明定位监听
     */
    private LocationSource mLocationSource=new LocationSource() {

        //设置定位参数,启动定位
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {

            mLocationChangedListener=onLocationChangedListener;
            if(null == mapLocationClient){
                mapLocationClient=new AMapLocationClient(getApplicationContext());
                mapLocationClientOption=new AMapLocationClientOption();
                //设置定位回调监听
                mapLocationClient.setLocationListener(mapLocationListener);
                //设置客户端监听参数
                mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                mapLocationClientOption.setNeedAddress(true);//返回地址信息
                mapLocationClientOption.setWifiScan(true);//强行刷新WiFi
                mapLocationClientOption.setMockEnable(false);//不允许模拟位置
                mapLocationClientOption.setOnceLocation(true);//是否定位一次
                mapLocationClientOption.setInterval(5000);//定位时间间隔
                //给客户端设置参数
                mapLocationClient.setLocationOption(mapLocationClientOption);
                //启动监听
                mapLocationClient.startLocation();
            }
        }

        //停止定位
        @Override
        public void deactivate() {
            mLocationChangedListener=null;
            if(null!=mapLocationClient){
                mapLocationClient.stopLocation();
            }
            mapLocationClient=null;
        }
    };


    //在定位回调监听中显示定位标记
    private AMapLocationListener mapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(mLocationChangedListener!=null && aMapLocation!=null){
                if(aMapLocation!=null && aMapLocation.getErrorCode()==0){

                    //设置地图显示当前定位
                    lat=aMapLocation.getLatitude();
                    lon=aMapLocation.getLongitude();
                    LatLng latLng=new LatLng(lat,lon);
                    //显示定位
//                    aMap.moveCamera(CameraUpdateFactory
//                            .newLatLngZoom(latLng,18));
                    MarkerOptions options=new MarkerOptions();
                    options.position(latLng);
                    options.title("您的位置");
                    options.visible(true);
                    BitmapDescriptor bd= BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.loc_current));
                    options.icon(bd);
                    //设置当前位置标记
                    Marker marker=aMap.addMarker(options);
//                    marker.showInfoWindow();
                    Log.d(TAG,"定位成功");
                }else {
                    Log.d(TAG,"定位失败--"+"error code--"+aMapLocation.getErrorCode()+"\n"+
                            "error info--"+aMapLocation.getErrorInfo());
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mapLocationClient){
            mapLocationClient.onDestroy();
        }
    }
}
