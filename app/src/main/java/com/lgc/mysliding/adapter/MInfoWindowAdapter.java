package com.lgc.mysliding.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 * 高德地图自定义 infoWindow 的 adapter
 */
public class MInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private Context mContext= MyApp.getMyApp().getBaseContext();
    private int signal;
    private String mac;
    private LatLng latLng;
    private double lat;
    private double lon;
//    private Double[] doubles;
    private String address;
    private TextView tv_signal;
    private TextView tv_mac;
    private TextView tv_address;

    @Override
    public View getInfoWindow(Marker marker) {
        initMarkerData(marker);
        View view=initView();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initMarkerData(Marker marker){
        latLng=marker.getPosition();
        signal=marker.getPeriod();
        mac=marker.getTitle();
        lat=latLng.latitude;
        lon=latLng.longitude;
        //换算经纬度为地址
        Geocoder geocoder=new Geocoder(mContext);
        try {
            List<Address> addressList=geocoder.getFromLocation(lat,lon,1);
            if(addressList.size()>0){
                Address addr=addressList.get(0);
                address =addr.getAddressLine(0).substring(3).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        doubles=new Double[]{lat,lon};
    }

    @NonNull
    private View initView(){
        View mView = LayoutInflater.from(mContext).inflate(R.layout.amap_infowindow,null);
        tv_signal = (TextView) mView.findViewById(R.id.tv_set_signal);
        tv_mac = (TextView) mView.findViewById(R.id.tv_set_mac);
        tv_address = (TextView) mView.findViewById(R.id.tv_set_address);

        //异步设置位置信息
//        new MyDeviceAdapter.AddressTask(tv_address,doubles).execute(doubles);

        tv_signal.setText(String.valueOf(signal));//设置信号强度
        tv_mac.setText(mac);//设置mac地址
        tv_address.setText(address);//设置位置信息
        return mView;
    }
}
