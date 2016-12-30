package com.lgc.mysliding.adapter.baseadapter;

import android.widget.BaseAdapter;

import com.lgc.mysliding.bean.DetectorInfoBean;

import java.util.List;

public abstract class DeviceBaseAdapter extends BaseAdapter{

    private List<DetectorInfoBean.DeviceListBean> deviceListBeen;

    public DeviceBaseAdapter(List<DetectorInfoBean.DeviceListBean> deviceListBeanList){
        this.deviceListBeen=deviceListBeanList;
    }

    @Override
    public int getCount() {
        return deviceListBeen==null?0:deviceListBeen.size();
    }

    @Override
    public Object getItem(int i) {
        return deviceListBeen.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   public List<DetectorInfoBean.DeviceListBean> getDeviceListBeen(){
       return deviceListBeen;
   }
}
