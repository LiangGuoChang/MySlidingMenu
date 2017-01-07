package com.lgc.mysliding.adapter.baseadapter;

import android.util.Log;
import android.widget.BaseAdapter;

import com.lgc.mysliding.bean.DetectorInfoBean;

import java.util.List;

public abstract class DeviceBaseAdapter extends BaseAdapter{

    private static final String TAG="DeviceBaseAdapter";
    private List<DetectorInfoBean.DeviceListBean> deviceList;
    private DetectorInfoBean.DeviceListBean deviceBean;
    private static int count;

    public DeviceBaseAdapter(List<DetectorInfoBean.DeviceListBean> deviceListBeanList){
        this.deviceList=deviceListBeanList;
    }

    @Override
    public int getCount() {
        count = deviceList==null?0:deviceList.size();
        Log.d(TAG,"getCount--"+count);
        return count;
    }

    @Override
    public Object getItem(int i) {
        deviceBean=deviceList.get(i);
        Log.d(TAG,"getItem--"+String.valueOf(deviceBean));
        return deviceBean;
    }

    @Override
    public long getItemId(int i) {
        Log.d(TAG,"getItemId--"+i);
        return i;
    }

   public List<DetectorInfoBean.DeviceListBean> getDeviceListBeen(){
       Log.d(TAG,"getDeviceListBeen");
       return deviceList;
   }
}
