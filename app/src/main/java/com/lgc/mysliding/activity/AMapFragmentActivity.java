package com.lgc.mysliding.activity;

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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.DetectorInfoBean;

import java.util.List;

public class AMapFragmentActivity extends FragmentActivity implements LocationSource, AMapLocationListener {
    private static final String TAG="AMapFragmentActivity";
    private AMap aMap;
    private OnLocationChangedListener locationChangedListener;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private List<DetectorInfoBean.DeviceListBean> deviceListBeen;
    private List<MarkerOptions> markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_fragment);
        setUpMApIfNeeded();

        //获取解析的数据，进行描点
        MyApp myApp= (MyApp) getApplicationContext();
        deviceListBeen=myApp.getDeviceListBeen();
        int deviceSize=deviceListBeen.size();
        Log.d(TAG,"deviceSize--"+deviceSize);

        if (deviceSize>0){
            for (int i=0;i<deviceSize;i++){
                Log.d(TAG,"deviceListBeen--"+deviceListBeen.get(i).getMac());

                LatLng latLng =
                        new LatLng(deviceListBeen.get(i).getLatitude(), deviceListBeen.get(i).getLongitude());
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(deviceListBeen.get(i).getMac());
                options.visible(true);
                aMap.addMarker(options);
            }
        }

        //设置定位监听
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(24.291004, 116.099656)));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(28));
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMApIfNeeded();
    }

    private void setUpMApIfNeeded(){
        if (aMap==null){
            aMap=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment_id))
                    .getMap();
        }
    }

    //启动定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener=onLocationChangedListener;
        if (mapLocationClient==null){
            mapLocationClient=new AMapLocationClient(getApplicationContext());
            mapLocationClientOption=new AMapLocationClientOption();
            //设置定位回调监听
            mapLocationClient.setLocationListener(this);
            //定位精度
            mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mapLocationClient.setLocationOption(mapLocationClientOption);
            //启动定位
            mapLocationClient.startLocation();
        }

    }

    //停止定位
    @Override
    public void deactivate() {
        locationChangedListener=null;
        if (null != mapLocationClient){
            mapLocationClient.stopLocation();
            mapLocationClient.onDestroy();
        }
        mapLocationClient=null;
    }

    //在定位回调监听中显示定位标记
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (locationChangedListener!=null && aMapLocation!=null){
            if (null != aMapLocation && aMapLocation.getErrorCode()==0){
                Log.d(TAG,"定位");
                //显示系统蓝点标记
                locationChangedListener.onLocationChanged(aMapLocation);
            }else {
                Log.d(TAG,"定位失败");
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mapLocationClient){
            mapLocationClient.onDestroy();
        }
    }
}
