package com.lgc.mysliding.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.MyDeviceAdapter;
import com.lgc.mysliding.bean.DetectorInfoBean;
import com.lgc.mysliding.presenter.DevicePresenter;
import com.lgc.mysliding.view_interface.ViewInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NeedleFragment extends Fragment implements ViewInterface{

    private static final String TAG="NeedleFragment";
    private View mView;
    private EditText et_mac;
    private ListView lv_detector;
//    private String url="http://192.168.1.184:8080/json/detectorInfo.json";
    private String url="http://o1510u4870.iok.la/1/json/detectorInfo.json";
    private MyDeviceAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_needle, container, false);
            initView();

            new DevicePresenter(this).load(String.format(url,2));

            //换算时间戳
            int unixTime=1463095281;
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date=sdf.format(new Date(unixTime*1000L));
            Log.d(TAG,"date--"+date);
        }
        Log.d(TAG,"onCreateView");
        return mView;
    }

    //初始化控件
    private void initView(){
//        et_mac = (EditText)mView.findViewById(R.id.et_search);
        lv_detector = (ListView) mView.findViewById(R.id.lv_detector);
    }

    @Override
    public void onAttach(Context context) {

        Log.d(TAG,"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG,"onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void showDevice(List<DetectorInfoBean.DeviceListBean> deviceListBeen) {

        adapter = new MyDeviceAdapter(deviceListBeen);
        lv_detector.setAdapter(adapter);
        Log.d(TAG,"showDevice--"+ adapter);
    }
}
