package com.lgc.mysliding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.AMapFragmentActivity;
import com.lgc.mysliding.adapter.MyDeviceAdapter;
import com.lgc.mysliding.bean.DetectorInfoBean;
import com.lgc.mysliding.presenter.DevicePresenter;
import com.lgc.mysliding.view_interface.ViewInterface;

import java.util.List;

public class NeedleFragment extends Fragment implements ViewInterface, AdapterView.OnItemClickListener {

    private static final String TAG="NeedleFragment";
    private View mView;
    private EditText et_mac;
    private ListView lv_detector;
//    private String url="http://192.168.1.184:8080/json/detectorInfo.json";
    private String url="http://o1510u4870.iok.la/1/json/detectorInfo.json";
//    private String mapUriStr = "http://maps.google.cn/maps/api/geocode/json?latlng={0},{1}&sensor=true&language=zh-CN";
    private MyDeviceAdapter adapter;
    private List<DetectorInfoBean.DeviceListBean> mDeviceList;
    private List<MarkerOptions> markerOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_needle, container, false);
            initView();

            new DevicePresenter(this).load(String.format(url,2));

        }

        Log.d(TAG,"onCreateView");
        return mView;
    }

    //初始化控件
    private void initView(){
//        et_mac = (EditText)mView.findViewById(R.id.et_search);
        lv_detector = (ListView) mView.findViewById(R.id.lv_detector);
        lv_detector.setOnItemClickListener(this);
    }

    @Override
    public void showDevice(List<DetectorInfoBean.DeviceListBean> deviceListBeen) {

        adapter = new MyDeviceAdapter(getContext(),deviceListBeen);
        lv_detector.setAdapter(adapter);
        Log.d(TAG,"showDevice--"+ adapter);

        MyApp myApp= (MyApp) getActivity().getApplicationContext();
//        if (deviceListBeen.size()>0){
//        for (int i=0;i<deviceListBeen.size();i++){
//            LatLng latlng=new LatLng(deviceListBeen.get(i).getLatitude(),deviceListBeen.get(i).getLongitude());
//            MarkerOptions markerOption=new MarkerOptions();
//            markerOption.position(latlng);
//            markerOption.title(deviceListBeen.get(i).getMac());
//            markerOption.visible(true);
//            markerOptions.add(markerOption);
//        }
//        myApp.setMarkerOptions(markerOptions);
//        }

        mDeviceList=deviceListBeen;
        myApp.setDeviceListBeen(deviceListBeen);
        Log.d(TAG,"mDeviceList--"+mDeviceList.size());
        Log.d(TAG,"myApp--"+myApp.getDeviceListBeen().size());
    }

    //lv_detector 条目点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView tvMac= (TextView) view.findViewById(R.id.tv_mac);
        String mac= tvMac.getText().toString();
        Log.d(TAG,"mac--"+mac);
        Intent start=new Intent(getContext(), AMapFragmentActivity.class);
//        Intent start=new Intent(getContext(), MAmapActivity.class);
        startActivity(start);
    }

}
