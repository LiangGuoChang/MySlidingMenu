package com.lgc.mysliding.presenter;

import com.google.gson.Gson;
import com.lgc.mysliding.bean.TraceBean;
import com.lgc.mysliding.model.model_imp.DeviceModelImp;
import com.lgc.mysliding.model.model_interface.ModelInterface;
import com.lgc.mysliding.view_interface.TraceInterface;

import java.util.List;

/**
 * Created by lgc on 2017/4/11.
 * presenter层
 * 获取轨迹
 */
public class TracePresenter {
    private static final String TAG="TracePresenter";
    private TraceInterface traceInterface;
    private ModelInterface modelInterface;

    public TracePresenter(TraceInterface mTraceInterface){
        this.traceInterface=mTraceInterface;
        //创建模型层实例,拿到数据
        modelInterface=new DeviceModelImp();
    }

    /**
     * 外部调用该方法获取轨迹列表
     * 通过模型层的 modelInterface.getData 方法拿到数据
     * @param url 获取轨迹列表的地址
     */
    public void loadFeatureList(String url){
        modelInterface.getData(new ModelInterface.onDataCompleteListener() {
            @Override
            public void onLoadComplete(byte[] bytes, String urlPath) {
                String json=new String(bytes);
                Gson gson=new Gson();
                TraceBean traceBean=gson.fromJson(json,TraceBean.class);
                List<TraceBean.FeatureListBean> feature_list=traceBean.getFeature_list();
                traceInterface.showFeatureList(feature_list);
            }
        },url);
    }

}
