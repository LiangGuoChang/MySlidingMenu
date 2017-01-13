package com.lgc.mysliding.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.lgc.mysliding.model.model_interface.ModelInterface;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeviceAsyncTask extends AsyncTask<String,Void,byte[]>{
    private static final String TAG="DeviceAsyncTask";

    //json网络地址
    private String urlPath;
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

        Log.d(TAG,"doInBackground--"+urlPath);
//        Log.d(TAG,"doInBackground-返回数据的长度--"+getURLDevice(urlPath).length);
        return getURLDevice(urlPath);
    }

    /**
     * 获取返回的数据后执行相应的操作
     * @param bytes 获取后传过来的数据
     *
     */
    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        //如果返回的数据不为空，接口实例不为空，实现接口的方法
        if ((null != bytes) && (null != dataCompleteListener)){
            dataCompleteListener.onLoadComplete(bytes,urlPath);

            Log.d(TAG,"onPostExecute实现接口的方法");
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

            Log.d(TAG,"getURLDevice返回码--"+code);
            //返回码为200,访问成功
            if (code==HttpURLConnection.HTTP_OK){
                //获取返回的字节流
                InputStream is=connection.getInputStream();
                BufferedInputStream bis=new BufferedInputStream(is);
                //保存数据到内存
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                int len;
                byte[] bytes=new byte[1024*4];
                while ((len=bis.read(bytes))!=-1){
                    bos.write(bytes,0,len);
                }

                Log.d(TAG,"getURLDevice保存到内存的数据长度--"+len);
                bis.close();return bos.toByteArray();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
