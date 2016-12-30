package com.lgc.mysliding;

import android.app.Application;

import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.bean.DetectorInfoBean;

import java.util.List;

public class MyApp extends Application{

    private static final String TAG="MyApp";

    private List<DetectorInfoBean.DeviceListBean> deviceListBeen;
    private List<MarkerOptions> markerOptions;

    public List<MarkerOptions> getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(List<MarkerOptions> markerOptions) {
        this.markerOptions = markerOptions;
    }

    public List<DetectorInfoBean.DeviceListBean> getDeviceListBeen() {
        return deviceListBeen;
    }

    public void setDeviceListBeen(List<DetectorInfoBean.DeviceListBean> deviceListBeen) {
        this.deviceListBeen = deviceListBeen;
    }

}
