package com.lgc.mysliding.view_interface;

import com.lgc.mysliding.bean.DetectorInfoBean;

import java.util.List;

/**
 * 一个通用接口，所有需要更细界面的功能都发在这里
 */
public interface ViewInterface {

    void showDevice(List<DetectorInfoBean.DeviceListBean> deviceListBeen);
}
