package com.lgc.mysliding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.AlertMsgBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 * 报警信息列表适配器
 */
public class AlertMsgAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<AlertMsgBean> alertMsgBeanList;

    public AlertMsgAdapter(Context context){
        this.mContext=context;
        layoutInflater=LayoutInflater.from(mContext);
    }

    public List<AlertMsgBean> getAlertMsgBeanList() {
        return alertMsgBeanList;
    }

    public void setAlertMsgBeanList(List<AlertMsgBean> alertMsgBeanList) {
        this.alertMsgBeanList = alertMsgBeanList;
    }

    @Override
    public int getCount() {
        return (null == alertMsgBeanList ? 0 : alertMsgBeanList.size());
    }

    @Override
    public Object getItem(int i) {
        return (null == alertMsgBeanList ? 0 : alertMsgBeanList.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MsgViewHolder msgViewHolder;
        AlertMsgBean alertMsgBean=alertMsgBeanList.get(i);
        if (view==null){
            msgViewHolder=new MsgViewHolder();
            view=layoutInflater.inflate(R.layout.item_alert_msg,null);
            msgViewHolder.msg_id_tv= (TextView) view.findViewById(R.id.push_msg_id);
            msgViewHolder.title_tv= (TextView) view.findViewById(R.id.push_title);
            msgViewHolder.content_tv= (TextView) view.findViewById(R.id.push_content);
            msgViewHolder.time_tv= (TextView) view.findViewById(R.id.push_time);
            view.setTag(msgViewHolder);
        }else {
            msgViewHolder= (MsgViewHolder) view.getTag();
        }
        msgViewHolder.msg_id_tv.setText(String.valueOf(alertMsgBean.getMsg_id()));
        msgViewHolder.title_tv.setText(alertMsgBean.getTitle());
        msgViewHolder.content_tv.setText(alertMsgBean.getContent());
        msgViewHolder.time_tv.setText(alertMsgBean.getUpdate_time());
        return view;
    }

   private class MsgViewHolder{
        TextView msg_id_tv;
        TextView title_tv;
        TextView time_tv;
        TextView content_tv;
    }
}
