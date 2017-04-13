package com.lgc.mysliding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.TraceBean;

import java.util.List;

/**
 * Created by lgc on 2017/4/11.
 * 查询轨迹列表适配器
 */
public class TraceFeatureAdapter extends BaseAdapter{

    private static final String TAG="TraceFeatureAdapter";
    private Context mContext;
    private List<TraceBean.FeatureListBean> mFeatureList;
    private TraceBean.FeatureListBean mFeatureBean;

    public TraceFeatureAdapter(Context context,List<TraceBean.FeatureListBean> list){
        this.mContext=context;
        this.mFeatureList=list;
    }

    @Override
    public int getCount() {
        return mFeatureList==null ? 0 : mFeatureList.size();
    }

    @Override
    public Object getItem(int i) {
        mFeatureBean=mFeatureList.get(i);
        return mFeatureBean;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TraceViewHolder traceViewHolder;
        if (view==null){
            traceViewHolder=new TraceViewHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.trace_list_item,null);
            traceViewHolder.tv_mac= (TextView) view.findViewById(R.id.tv_trace_mac);
            traceViewHolder.tv_phone= (TextView) view.findViewById(R.id.tv_trace_phone);
            traceViewHolder.tv_num= (TextView) view.findViewById(R.id.tv_trace_num);
            view.setTag(traceViewHolder);
        }else {
            traceViewHolder= (TraceViewHolder) view.getTag();
        }

        TraceBean.FeatureListBean featureListBean= (TraceBean.FeatureListBean) getItem(i);
        traceViewHolder.tv_mac.setText(featureListBean.getMac());
        traceViewHolder.tv_phone.setText(featureListBean.getPhone());
        traceViewHolder.tv_num.setText(String.valueOf(featureListBean.getTrace_num()));

        return view;
    }

    static class TraceViewHolder{
        TextView tv_mac;
        TextView tv_phone;
        TextView tv_num;
    }
}
