package com.lgc.mysliding.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.baseadapter.DeviceBaseAdapter;
import com.lgc.mysliding.bean.DetectorInfoBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyDeviceAdapter extends DeviceBaseAdapter{

    public MyDeviceAdapter(List<DetectorInfoBean.DeviceListBean> deviceListBeanList) {
        super(deviceListBeanList);
    }

    /**
     * 解析布局，展示在界面上
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //先拿到要展示的数据
        DetectorInfoBean.DeviceListBean deviceListBean=getDeviceListBeen().get(i);
        //优化布局帮助类
        ViewHolder viewHolder=null;
        if (view==null){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detector_list,null);
            viewHolder=new ViewHolder();
            //查找相应的控件
            viewHolder.mac_tv= (TextView) view.findViewById(R.id.tv_mac);
            viewHolder.time_tv=(TextView) view.findViewById(R.id.tv_time);
            viewHolder.rssi_tv=(TextView) view.findViewById(R.id.tv_rssi);
            viewHolder.address_tv=(TextView) view.findViewById(R.id.tv_address);
            //将 viewHolder 保存到每个对象标记中，下次获取
            view.setTag(viewHolder);
        }
        viewHolder= (ViewHolder) view.getTag();
        viewHolder.mac_tv.setText(deviceListBean.getMac());
        //换算时间戳
        int time=deviceListBean.getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(new Date(time*1000L));
        viewHolder.time_tv.setText(date);
        viewHolder.rssi_tv.setText(String.valueOf(deviceListBean.getRssi()));
        // TODO: 2016/12/29 时间戳，经纬度需要换算
        double latitude=deviceListBean.getLatitude();
        double longitude=deviceListBean.getLongitude();
        String titude=String.valueOf(latitude)+"+"+String.valueOf(longitude);
        viewHolder.address_tv.setText("惠州市");

        return view;
    }

    static class ViewHolder{
        TextView mac_tv;
        TextView time_tv;
        TextView rssi_tv;
        TextView address_tv;
    }
}
