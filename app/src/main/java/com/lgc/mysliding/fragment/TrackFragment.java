package com.lgc.mysliding.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.RefreshAdapter;
import com.lgc.mysliding.views.MyRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class TrackFragment extends Fragment implements MyRefreshListView.OnRefreshListener, MyRefreshListView.OnLoadListener {

    private View mView;
    private MyRefreshListView rflv;
    private List<String> mList=new ArrayList<String>();
    private RefreshAdapter mAdapter;

    //更新列表
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<String> result= (List<String>) msg.obj;
            switch (msg.what){
                case MyRefreshListView.LV_REFRESH:
                    rflv.OnRefreshComplete();
                    mList.clear();
                    mList.addAll(result);
                    break;
                case MyRefreshListView.LV_LOAD:
                    rflv.OnLoadComplete();
                    mList.addAll(result);
                    break;
            }
            rflv.setResultSize(result.size());
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_track, container, false);
            initView();
        }
        return mView;
    }

    //初始化view
    private void initView(){
        rflv = (MyRefreshListView) mView.findViewById(R.id.refresh_lv);
        mList = getData();
        mAdapter = new RefreshAdapter(getContext(), mList);
        rflv.setAdapter(mAdapter);
        rflv.setOnRefreshListener(this);
        rflv.setOnLoadListener(this);
        setData(MyRefreshListView.LV_REFRESH);
    }

    // 测试数据
    public List<String> getData() {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < 22; i++) {
            result.add("当前条目的ID：" + i);
        }
        return result;
    }

    @Override
    public void onRefresh() {
        setData(MyRefreshListView.LV_REFRESH);
    }

    @Override
    public void onLoad() {
        setData(MyRefreshListView.LV_LOAD);
    }

    //模拟刷新（或加载）时的方法
    private void setData(final int msgWhat){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg=handler.obtainMessage();
                msg.obj=getData();
                msg.what=msgWhat;
                handler.sendMessage(msg);
            }
        }).start();
    }

}
