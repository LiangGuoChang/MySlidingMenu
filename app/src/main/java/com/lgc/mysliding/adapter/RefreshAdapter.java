package com.lgc.mysliding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lgc.mysliding.R;

import java.util.List;

public class RefreshAdapter extends BaseAdapter{

    private List<String> mList;
    private Context mContext;

    public RefreshAdapter(Context context, List<String> list){
        this.mContext=context;
        this.mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHold viewHold=null;
        if (view==null){
            viewHold=new ViewHold();
            view= LayoutInflater.from(mContext).inflate(R.layout.item_refresh_list,null);
            viewHold.textView=(TextView)view.findViewById(R.id.tv_test);
            view.setTag(viewHold);
        }else {
            viewHold= (ViewHold) view.getTag();
        }
        viewHold.textView.setText(mList.get(i));
        return view;
    }

    class ViewHold{
        TextView textView;
    }
}
