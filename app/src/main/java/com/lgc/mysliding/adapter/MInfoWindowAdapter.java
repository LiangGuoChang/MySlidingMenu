package com.lgc.mysliding.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.Marker;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;

/**
 * 探针管理功能的
 * 高德地图自定义 infoWindow 的 adapter
 */
public class MInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private Context mContext= MyApp.getMyApp().getBaseContext();
    private static final String[] sstatus={"正常","离线"};
    private String status;
    private String mac;
    private String address;
    private TextView tv_status;
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
        mac=marker.getTitle();
        address=marker.getSnippet();
        if (marker.getPeriod()==1){
            status=sstatus[0];
        }else if (marker.getPeriod()==2){
            status=sstatus[1];
        }
    }

    @NonNull
    private View initView(){
        View mView = LayoutInflater.from(mContext).inflate(R.layout.amap_infowindow,null);
        tv_status = (TextView) mView.findViewById(R.id.tv_set_signal);
        tv_mac = (TextView) mView.findViewById(R.id.tv_set_mac);
        tv_address = (TextView) mView.findViewById(R.id.tv_set_address);

        //异步设置位置信息
//        new MyDeviceAdapter.AddressTask(tv_address,doubles).execute(doubles);

        tv_status.setText(status);//设置状态
        tv_mac.setText(mac);//设置mac地址
        tv_address.setText(address);//设置位置信息
        return mView;
    }
}
