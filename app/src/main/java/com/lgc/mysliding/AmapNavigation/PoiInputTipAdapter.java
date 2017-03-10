package com.lgc.mysliding.AmapNavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.lgc.mysliding.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 * 输入关键字获取相关位置的适配器
 */
public class PoiInputTipAdapter extends BaseAdapter{
    private Context mContext;
    private List<Tip> mList;

    public PoiInputTipAdapter(Context context, List<Tip> tipList){
        this.mContext=context;
        this.mList=tipList;
    }

    @Override
    public int getCount() {
        return (null==mList ? 0 : mList.size());
    }

    @Override
    public Object getItem(int i) {
        return (null==mList ? null : mList.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PoiViewHolder poiViewHolder;
        Tip mTip=mList.get(i);
        if (view==null){
            poiViewHolder=new PoiViewHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.item_poisearch,null);
            poiViewHolder.poiAddr_tv= (TextView) view.findViewById(R.id.tv_poi_address);
            poiViewHolder.poiDistr_tv= (TextView) view.findViewById(R.id.tv_poi_district);
            poiViewHolder.poiName_tv= (TextView) view.findViewById(R.id.tv_poi_name);
            view.setTag(poiViewHolder);
        }else {
            poiViewHolder= (PoiViewHolder) view.getTag();
        }
        poiViewHolder.poiAddr_tv.setText(mTip.getAddress());
        poiViewHolder.poiDistr_tv.setText(mTip.getDistrict());
        poiViewHolder.poiName_tv.setText(mTip.getName());
        return view;
    }

    class PoiViewHolder{
        TextView poiName_tv;
        TextView poiAddr_tv;
        TextView poiDistr_tv;
    }
}
