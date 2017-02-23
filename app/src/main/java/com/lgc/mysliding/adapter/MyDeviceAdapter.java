package com.lgc.mysliding.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.baseadapter.DeviceBaseAdapter;
import com.lgc.mysliding.bean.DetectorInfoBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyDeviceAdapter extends DeviceBaseAdapter{

    private static final String TAG="MyDeviceAdapter";
    private static Context context;

    public MyDeviceAdapter(Context context,List<DetectorInfoBean.DeviceListBean> deviceListBeanList) {
        super(deviceListBeanList);
        this.context=context;
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
        Log.d(TAG,"getView--"+"i::"+i);

        //先拿到要展示的数据
//        DetectorInfoBean.DeviceListBean deviceListBean=getDeviceListBeen().get(i);
//        Log.d(TAG,"deviceListBean--"+String.valueOf(deviceListBean));
        DetectorInfoBean.DeviceListBean deviceListBean= (DetectorInfoBean.DeviceListBean) getItem(i);
        Log.d(TAG,"object--"+String.valueOf(deviceListBean));

        //换算时间戳
        int time=deviceListBean.getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(new Date(time*1000L));
        // 经纬度换算为地址信息
        double latitude=deviceListBean.getLatitude();
        double longitude=deviceListBean.getLongitude();
        Double[] mAddr=new Double[]{latitude,longitude};
        Log.d(TAG,"LngLat--"+String.valueOf(latitude)+"-"+String.valueOf(longitude));

        //优化布局类
        ViewHolder viewHolder=null;
        if (view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detector_list,null);
            //查找相应的控件
            viewHolder.mac_tv= (TextView) view.findViewById(R.id.tv_mac);
            viewHolder.time_tv=(TextView) view.findViewById(R.id.tv_time);
            viewHolder.rssi_tv=(TextView) view.findViewById(R.id.tv_rssi);
            viewHolder.address_tv=(TextView) view.findViewById(R.id.tv_address);
            //将 viewHolder 保存到每个对象标记中，下次获取
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }

        viewHolder.address_tv.setText("");
        //异步加载地址信息
        new AddressTask(viewHolder.address_tv,mAddr).execute(mAddr);
        viewHolder.mac_tv.setText(deviceListBean.getMac());
        viewHolder.time_tv.setText(date);
        viewHolder.rssi_tv.setText(String.valueOf(deviceListBean.getRssi()));

        if(i%2==0){
            view.setBackgroundResource(R.color.double_item);
        }else {
            view.setBackgroundResource(R.color.single_item);
        }

        return view;
    }

    static class ViewHolder{
        TextView mac_tv;
        TextView time_tv;
        TextView rssi_tv;
        TextView address_tv;
    }

    //异步加载地址信息
    class AddressTask extends AsyncTask<Double,Void,String>{
        private TextView  textView;
        private Double[] mAddress;

        public AddressTask(TextView tv,Double[] mAddr){
            this.textView=tv;
            this.mAddress=mAddr;
        }

        @Override
        protected String doInBackground(Double... params) {
            double latitude=params[0];
            double longitude=params[1];
            //换算经纬度为地址
            Geocoder geocoder=new Geocoder(context);
            String strAdd = null;
            try {
                List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                if(addressList.size()>0){
                    Address address=addressList.get(0);
                    strAdd =address.getAddressLine(0).substring(3);
                    Log.d(TAG,"strAdd--"+ strAdd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strAdd;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }
    }

}
