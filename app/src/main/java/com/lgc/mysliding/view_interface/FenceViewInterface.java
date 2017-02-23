package com.lgc.mysliding.view_interface;

import com.lgc.mysliding.bean.FenceBean;

import java.util.List;

/**
 *获取围栏列表的接口
 */
public interface FenceViewInterface {
    void showFenceList(List<FenceBean.FenceListBean> fenceListBeen);
    void showCRUDResult(String result);
}
