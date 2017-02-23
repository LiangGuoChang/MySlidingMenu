package com.lgc.mysliding.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.bean.DetectorInfoBean;
import com.lgc.mysliding.model.model_imp.DeviceModelImp;
import com.lgc.mysliding.model.model_interface.ModelInterface;
import com.lgc.mysliding.view_interface.ViewInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * presenter层
 * 获取探针列表
 */
public class DevicePresenter {
    private static final String TAG="DevicePresenter";
    private Context context= MyApp.getMyApp().getBaseContext();
    //模型层数据类型
    ModelInterface modelInterface;
    //view 层数据类型
    ViewInterface viewInterface;

    private List<String> addrList=new ArrayList<>();

    public DevicePresenter(ViewInterface viewInterface){
        this.viewInterface=viewInterface;
        //创建模型层实例,拿到数据
        modelInterface=new DeviceModelImp();
    }

    /**
     * 外部通过调用该方法获取数据
     * 通过模型层的 modelInterface.getData 方法拿到数据
     * @param path
     */
    public void load(String path){

        modelInterface.getData(new ModelInterface.onDataCompleteListener() {
            @Override
            public void onLoadComplete(byte[] bytes, String urlPath) {
                //传过来的 byte转化为字符串
                String json=new String(bytes);
                Log.d(TAG,"load获取传过来的json数据"+"\n"+json);

                //使用Gson解析到对应的实体类
                Gson gson=new Gson();
                DetectorInfoBean detectorInfoBean=gson.fromJson(json,DetectorInfoBean.class);
                List<DetectorInfoBean.DeviceListBean> device_list=detectorInfoBean.getDevice_list();
                Log.d(TAG,"getDevice_list--"+device_list.size());
                //返回数据集合
                viewInterface.showDevice(device_list);
            }
        },path);
    }
}
