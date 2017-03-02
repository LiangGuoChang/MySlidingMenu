package com.lgc.mysliding.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.FenceBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/18.
 * 围栏列表适配器
 */
public class FenceListAdapter extends BaseAdapter{

    private static final String TAG="FenceListAdapter";
    private static final String[] strAlert={"进报警","出报警","进+出报警"};
    private List<FenceBean.FenceListBean> mFenceListBeanList;
    private FenceBean.FenceListBean fenceListBean;
    private Context mContext;
    private int count;

    public FenceListAdapter(Context context, List<FenceBean.FenceListBean> fenceListBeanList){
        this.mContext=context;
        this.mFenceListBeanList=fenceListBeanList;
    }

    @Override
    public int getCount() {
        count=mFenceListBeanList==null?0:mFenceListBeanList.size();
        Log.d(TAG,"mFenceListBeanList-size-"+count);
        return count;
    }

    @Override
    public Object getItem(int i) {
        fenceListBean=mFenceListBeanList.get(i);
//        fencePhones=fenceListBean.getPhone_list();
        return fenceListBean;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.fence_list_item,null);
            viewHolder.tv_fenceName= (TextView) view.findViewById(R.id.tv_fence_name);
            viewHolder.tv_longitude=(TextView)view.findViewById(R.id.tv_longitude);
            viewHolder.tv_latitude=(TextView)view.findViewById(R.id.tv_latitude);
            viewHolder.tv_fenceAlert= (TextView) view.findViewById(R.id.tv_fence_alter);
            viewHolder.tv_fenceRadius= (TextView) view.findViewById(R.id.tv_fence_radius);
            viewHolder.tv_fencePhone= (TextView) view.findViewById(R.id.tv_fence_phone);
            //将 viewHolder 保存到每个对象标记中，下次获取
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }

        FenceBean.FenceListBean mFenceListBean= (FenceBean.FenceListBean) getItem(i);
        List<String> fencePhones=mFenceListBean.getPhone_list();
        Log.d(TAG,"fencePhones-size-"+fencePhones.size());
        //添加内容到控件
        String lat= String.valueOf(mFenceListBean.getLatitude());
        String lon= String.valueOf(mFenceListBean.getLongitude());
        String radius=String.valueOf(mFenceListBean.getRadius());
        int alert=mFenceListBean.getType();
        String alertStr="";
        switch (alert){
            case 0:
                alertStr=strAlert[0];
                break;
            case 1:
                alertStr=strAlert[1];
                break;
            case 2:
                alertStr=strAlert[2];
                break;
        }
        String phones="";
        for (int j=0;j<fencePhones.size();j++) {
            phones=phones+fencePhones.get(j);
            if (j<fencePhones.size()-1){
                phones=phones+"/";
//                phones=phones+"\n";
            }
            Log.d(TAG,"fencePhones-"+j+"-"+fencePhones.get(j));
        }
        viewHolder.tv_fenceName.setText(mFenceListBean.getName());
        viewHolder.tv_longitude.setText(lon);
        viewHolder.tv_latitude.setText(lat);
        viewHolder.tv_fenceAlert.setText(alertStr);
        viewHolder.tv_fenceRadius.setText(radius);
        viewHolder.tv_fencePhone.setText(phones);
        return view;
    }

    //布局优化内嵌类
    static class ViewHolder{
        TextView tv_fenceName;
        TextView tv_longitude;
        TextView tv_latitude;
        TextView tv_fenceAlert;
        TextView tv_fenceRadius;
        TextView tv_fencePhone;
    }
}
