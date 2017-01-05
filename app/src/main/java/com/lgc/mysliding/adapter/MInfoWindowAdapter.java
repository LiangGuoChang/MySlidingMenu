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
 * Created by Administrator on 2017/1/5.
 * 高德地图自定义 infoWindow 的 adapter
 */
public class MInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private Context mContext= MyApp.getMyApp().getBaseContext();
    private String signal;
    private String mac;
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
        signal=marker.getId();
        mac=marker.getTitle();
        address=marker.getSnippet();
    }

    @NonNull
    private View initView(){
        View mView = LayoutInflater.from(mContext).inflate(R.layout.amap_infowindow,null);
        tv_signal = (TextView) mView.findViewById(R.id.tv_set_signal);
        tv_mac = (TextView) mView.findViewById(R.id.tv_set_mac);
        tv_address = (TextView) mView.findViewById(R.id.tv_set_address);

        tv_signal.setText(signal);
        tv_mac.setText(mac);
        tv_address.setText(address);
        return mView;
    }
}
