package com.lgc.mysliding.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.lgc.mysliding.bean.FenceBean;
import com.lgc.mysliding.bean.FenceResultBean;
import com.lgc.mysliding.model.model_imp.DeviceModelImp;
import com.lgc.mysliding.model.model_interface.ModelInterface;
import com.lgc.mysliding.view_interface.FenceViewInterface;

import java.util.List;

/**
 *presenter层
 * 获取围栏列表
 */
public class FenceListPresenter {

    private static final String TAG="FenceListPresenter";
    private FenceViewInterface mFenceViewInterface;
    private ModelInterface modelInterface;

    public FenceListPresenter(FenceViewInterface fenceViewInterface){
        this.mFenceViewInterface=fenceViewInterface;
        //创建模型层实例,拿到数据
        modelInterface=new DeviceModelImp();
    }

    /**
     * 外部调用该方法获取围栏列表
     * 通过模型层的 modelInterface.getData 方法拿到数据
     * @param urlPath 查询围栏列表的地址
     */
    public void loadFenceList(String urlPath){
        modelInterface.getData(new ModelInterface.onDataCompleteListener() {
            @Override
            public void onLoadComplete(byte[] bytes, String urlPath) {
                //将传过来的byte[]转化为字符串
                String json=new String(bytes);
                Log.d(TAG,"loadFenceList传过来的json数据"+"\n"+json);
                //使用Gson解析到对应的实体类
                Gson gson=new Gson();
                FenceBean fenceBean=gson.fromJson(json,FenceBean.class);
                List<FenceBean.FenceListBean> fence_list=fenceBean.getFence_list();
                Log.d(TAG,"getFence_list--size"+fence_list.size());
                //返回围栏列表
                mFenceViewInterface.showFenceList(fence_list);
            }
        },urlPath);
    }

    /**
     *外部调用该方法创建围栏，并从服务器获取返回信息
     * 通过模型层的 modelInterface.getData 方法拿到数据
     * @param CRUDurL 增删改的URL地址
     */
    public void CRUDFence(String CRUDurL){
        modelInterface.getData(new ModelInterface.onDataCompleteListener() {
            @Override
            public void onLoadComplete(byte[] bytes, String urlPath) {
                //将传过来的byte[]转化为字符串
                String cJson=new String(bytes);
                Log.d(TAG,"createFence传过来的json数据"+"\n"+cJson);
                //使用Gson解析到对应的实体类
                Gson gson=new Gson();
                FenceResultBean fenceResultBean=gson.fromJson(cJson,FenceResultBean.class);
                int ret_code=fenceResultBean.getRet_code();
                String ret_msg=fenceResultBean.getRet_msg();
                String ret_id=fenceResultBean.getId();
                //返回服务器响应信息
                String result="fenceResultBean-"+"ret_code::"+String.valueOf(ret_code)+"\n"+"ret_msg::"+ret_msg+"\n"+"ret_id::"+ret_id;
                mFenceViewInterface.showCRUDResult(result);
                Log.d(TAG,"fenceResultBean-"+"ret_code::"+ret_code+"\n"+"ret_msg::"+ret_msg+"\n"+"ret_id::"+ret_id);
            }
        },CRUDurL);
    }

}
