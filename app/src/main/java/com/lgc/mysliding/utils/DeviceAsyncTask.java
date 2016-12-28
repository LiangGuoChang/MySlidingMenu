package com.lgc.mysliding.utils;

import android.os.AsyncTask;

import com.lgc.mysliding.model.model_interface.ModelInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DeviceAsyncTask extends AsyncTask<String,Void,byte[]>{

    private String urlPath; //json网络地址
    //接口回调实例
    private ModelInterface.onDataCompleteListener dataCompleteListener;

    public DeviceAsyncTask(ModelInterface.onDataCompleteListener dataCompleteListener){
        this.dataCompleteListener=dataCompleteListener;
    }

    /**
     * 异步任务方法，在子线程中执行
     * @param strings 访问json数据网络地址
     * @return 返回请求的数据，然后执行 onPostExecute
     */
    @Override
    protected byte[] doInBackground(String... strings) {
        //保存当前地址
        urlPath=strings[0];
        return getURLDevice(urlPath);
    }

    /**
     * 获取返回的数据后执行相应的操作
     * @param bytes 获取后传过来的数据
     */
    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        //如果返回的数据不为空，接口实例不为空，实现接口的方法
        if ((null != bytes) && (null != dataCompleteListener)){
            dataCompleteListener.onLoadComplete(bytes,urlPath);
        }
    }

    /**
     * 网络请求json数据
     * @param path json数据地址
     * @return 返回json数据
     */
    private byte[] getURLDevice(String path){

        try {
            URL url=new URL(path);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.connect();
            int code=connection.getResponseCode();
            //返回码为200,访问成功
            if (code==HttpURLConnection.HTTP_OK){
                //获取返回的字节流
                InputStream is=connection.getInputStream();
               // BufferedInputStream bis=new BufferedReader(is); // TODO: 2016/12/28 访问网络，获取json

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
