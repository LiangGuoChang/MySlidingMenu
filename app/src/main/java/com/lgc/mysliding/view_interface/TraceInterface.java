package com.lgc.mysliding.view_interface;

import com.lgc.mysliding.bean.TraceBean;

import java.util.List;

/**
 * Created by lgc on 2017/4/11.
 * 获取轨迹接口
 */
public interface TraceInterface {
    void showFeatureList(List<TraceBean.FeatureListBean> feature_list);
}
