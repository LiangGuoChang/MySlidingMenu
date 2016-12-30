package com.lgc.mysliding.model.model_interface;

/**
 * 模型层中所有功能都写在这个接口类中
 */
public interface ModelInterface {

    /**
     * 这个方法是通过网络获取json数据
     * @param dataCompleteListener 网络数据回调实例
     * @param urlPath json数据网络地址
     */
    void getData(onDataCompleteListener dataCompleteListener,String urlPath);

    /**
     * 网络请求完成后的接口回调方法
     * 调用这个方法可以获得json数据
     */
    public interface onDataCompleteListener{
        void onLoadComplete(byte[] bytes,String urlPath);
    }
}
