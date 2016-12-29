package com.lgc.mysliding.model.model_imp;

import android.util.Log;

import com.lgc.mysliding.model.model_interface.ModelInterface;
import com.lgc.mysliding.utils.DeviceAsyncTask;

public class DeviceModelImp implements ModelInterface{
    private static final String TAG="DeviceModelImp";

    /**
     * 调用异步任务获取json数据
     * @param dataCompleteListener 网络数据回调实例
     * @param urlPath json数据网络地址
     */
    @Override
    public void getData(onDataCompleteListener dataCompleteListener, String urlPath) {
        //调用异步任务获取json数据
        new DeviceAsyncTask(dataCompleteListener).execute(urlPath);

        Log.d(TAG,"getData--调用异步任务获取数据");
    }
}
