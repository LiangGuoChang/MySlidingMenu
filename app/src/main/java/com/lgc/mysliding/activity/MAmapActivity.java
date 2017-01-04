package com.lgc.mysliding.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.R;

public class MAmapActivity extends AppCompatActivity {

    private static final String TAG="MAmapActivity";

    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mamap);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        if (mapLocationClient==null){
            mapLocationClient=new AMapLocationClient(getApplicationContext());
        }
        mapLocationClient.setLocationListener(mapLocationListener);

       initAmap();
    }

    //初始化Amap对象
    private void initAmap(){
        if (aMap==null){
            aMap=mapView.getMap();
        }

        //设置定位参数
        if (mapLocationClientOption==null){
            mapLocationClientOption=new AMapLocationClientOption();
        }
        //定位精度模式
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mapLocationClientOption.setNeedAddress(true);//返回地址
        mapLocationClientOption.setOnceLocation(false);//是否定位一次
        mapLocationClientOption.setWifiScan(true);//强行刷新WiFi
        mapLocationClientOption.setMockEnable(false);//允许模拟位置
        mapLocationClientOption.setInterval(3000);//定位时间间隔
        mapLocationClient.setLocationOption(mapLocationClientOption);
        //开始定位
        mapLocationClient.startLocation();
    }

    //在定位回调监听中显示定位标记
    private AMapLocationListener mapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation!=null){
                if(aMapLocation.getErrorCode()==0){

                    //设置地图显示当前定位
                    lat=aMapLocation.getLatitude();
                    lon=aMapLocation.getLongitude();
                    LatLng latLng=new LatLng(lat,lon);
                    aMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(latLng,18));
                    MarkerOptions options=new MarkerOptions();
                    options.position(latLng);
                    options.title("当前位置");
                    options.visible(true);
                    BitmapDescriptor bd= BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.current_loc));
                    options.icon(bd);
                    //设置当前位置标记
                    aMap.addMarker(options);

                    Log.d(TAG,"定位成功");
                }else {
                    Log.d(TAG,"定位失败--"+"error code--"+aMapLocation.getErrorCode()+"\n"+
                            "error info--"+aMapLocation.getErrorInfo());
                }
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mapLocationClient!=null){
            mapLocationClient.stopLocation();//停止定位
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(mapLocationClient!=null){
            mapLocationClient.onDestroy();//销毁定位
        }
    }
}
