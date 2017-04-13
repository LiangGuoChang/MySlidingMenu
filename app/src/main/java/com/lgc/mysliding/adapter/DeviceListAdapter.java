package com.lgc.mysliding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.DetectorLists;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lgc on 2017/4/10.
 * 探针列表适配器
 */
public class DeviceListAdapter extends BaseAdapter {

    private static final String TAG="DeviceListAdapter";
    private static final String[] status={"正常","离线"};
    private List<DetectorLists.DetectorListBean> detectorList;
    private DetectorLists.DetectorListBean detectorListBean;
    private Context mContext;

    public DeviceListAdapter(Context context, List<DetectorLists.DetectorListBean> list){
        this.mContext=context;
        this.detectorList=list;
    }

    @Override
    public int getCount() {
        return detectorList==null ? 0 : detectorList.size();
    }

    @Override
    public Object getItem(int i) {
        detectorListBean=detectorList.get(i);
        return detectorListBean;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VHolder vHolder;
        if (view==null){
            vHolder=new VHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.item_detector_list,null);
            vHolder.tv_mac= (TextView) view.findViewById(R.id.tv_mac);
            vHolder.tv_time= (TextView) view.findViewById(R.id.tv_time);
            vHolder.tv_status= (TextView) view.findViewById(R.id.tv_rssi);
            vHolder.tv_address= (TextView) view.findViewById(R.id.tv_address);
            view.setTag(vHolder);
        }else {
            vHolder= (VHolder) view.getTag();
        }

        DetectorLists.DetectorListBean mDetectorListBean= (DetectorLists.DetectorListBean) getItem(i);
        //换算时间戳
        int time=mDetectorListBean.getLast_active_time();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(new Date(time*1000L));
        vHolder.tv_time.setText(date);//上次活跃时间
        vHolder.tv_mac.setText(mDetectorListBean.getMac());//mac地址
        String strStatus=mDetectorListBean.getStatus();//状态
        if (strStatus.equals("01")){
            vHolder.tv_status.setText(status[0]);
        }else if (strStatus.equals("02")){
            vHolder.tv_status.setText(status[1]);
        }
        vHolder.tv_address.setText(mDetectorListBean.getAddress());//位置

        if(i%2==0){
            view.setBackgroundResource(R.color.double_item);
        }else {
            view.setBackgroundResource(R.color.single_item);
        }

        return view;
    }

    static class VHolder{
        TextView tv_mac;
        TextView tv_time;
        TextView tv_status;
        TextView tv_address;
    }

}
