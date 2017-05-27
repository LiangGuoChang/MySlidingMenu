package com.lgc.mysliding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.AMapFragmentActivity;
import com.lgc.mysliding.activity.MyMainActivity;
import com.lgc.mysliding.adapter.DeviceListAdapter;
import com.lgc.mysliding.adapter.MyDeviceAdapter;
import com.lgc.mysliding.bean.DetectorInfoBean;
import com.lgc.mysliding.bean.DetectorLists;
import com.lgc.mysliding.presenter.DevicePresenter;
import com.lgc.mysliding.view_interface.ViewInterface;

import java.util.ArrayList;
import java.util.List;

public class NeedleFragment extends Fragment implements ViewInterface, AdapterView.OnItemClickListener, TextWatcher, View.OnClickListener {

    private static final String TAG="NeedleFragment";
    private static final int ListChange=2017;
//    private String url="http://192.168.1.184:8080/json/detectorInfo1.json";
    protected String url="http://218.15.154.6:8080/detector_list?";
    private View mView;
    private ListView lv_detector;
    private EditText et_search;
    private ImageView iv_search;
    private RelativeLayout rl_search;
    private ImageView iv_clear_search;
    private MyDeviceAdapter adapter;

    private List<DetectorInfoBean.DeviceListBean> mDeviceList= new ArrayList<>();
    private List<DetectorInfoBean.DeviceListBean> searchDeviceList= new ArrayList<>();

    private DeviceListAdapter deviceListAdapter;
    private List<DetectorLists.DetectorListBean> mDeviceListBeen=new ArrayList<>();
    private List<DetectorLists.DetectorListBean> searchDeviceListBeen=new ArrayList<>();

    private List<MarkerOptions> markerOptions;
    private MyApp myApp;
    private MyDeviceAdapter myAppAdapter;
    private MyMainActivity myMainActivity;

    //更新列表页面
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ListChange:
                    refreshListView(msg.getData().getString("text"));
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_needle, container, false);
            initView();
            myApp= (MyApp) getActivity().getApplicationContext();

            //通过presenter层去模型层获取json数据
            new DevicePresenter(this).load(String.format(url,2));
        }

        Log.d(TAG,"onCreateView");
        return mView;
    }

    //初始化控件
    private void initView(){
        myMainActivity = (MyMainActivity) getActivity();
        iv_search=myMainActivity.iv_search_mac;
        lv_detector = (ListView) mView.findViewById(R.id.lv_detector);
        et_search = (EditText) mView.findViewById(R.id.et_search);
        rl_search = (RelativeLayout) mView.findViewById(R.id.rl_search);
        iv_clear_search = (ImageView) mView.findViewById(R.id.iv_clear_search);
        lv_detector.setOnItemClickListener(this);
        et_search.addTextChangedListener(this);
        iv_search.setOnClickListener(this);
        iv_clear_search.setOnClickListener(this);
    }

    //控件的点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //显示或隐藏搜索栏
            case R.id.iv_search_mac:
                if(rl_search.getVisibility()==View.VISIBLE){
                    rl_search.setVisibility(View.GONE);
                }else {
                    rl_search.setVisibility(View.VISIBLE);
                }
                break;
            //清空EditText的内容
            case R.id.iv_clear_search:
                if (et_search.getText().toString().trim().length()!=0){
                    et_search.setText("");
                }
                break;
        }
    }

    //lv_detector 列表条目点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        TextView tvMac= (TextView) view.findViewById(R.id.tv_mac);
//        String mac= tvMac.getText().toString();
//        Log.d(TAG,"mac--"+mac);
        TextView tv_mac=(TextView)view.findViewById(R.id.tv_mac);
        String select_mac=tv_mac.getText().toString();
        Intent start=new Intent(getContext(), AMapFragmentActivity.class);
//        Intent start=new Intent(getContext(), MAmapActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("select_mac",select_mac);
        start.putExtras(bundle);
        startActivity(start);
    }

    //EditText输入监听
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //EditText输入过程实时发送Message更新界面
        Message msg=handler.obtainMessage();
        Bundle data=new Bundle();
        data.putString("text",charSequence.toString());
        msg.setData(data);
        msg.what=ListChange;
        handler.sendMessage(msg);

        Log.d(TAG,"sendMessage--"+charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //解析json数据获取列表数据
    @Override
    public void showDevice(List<DetectorLists.DetectorListBean> deviceListBeen) {

        /*mDeviceList=deviceListBeen;
        adapter = new MyDeviceAdapter(getContext(),mDeviceList);
        myAppAdapter = myApp.myDeviceAdapter;
        myAppAdapter =adapter;*/

        mDeviceListBeen=deviceListBeen;
        deviceListAdapter=new DeviceListAdapter(getContext(),mDeviceListBeen);
        //一开始先显示所有列表
        lv_detector.setAdapter(deviceListAdapter);
        Log.d(TAG,"showDevice--"+ deviceListAdapter);

        myApp.setDeviceListBeen(deviceListBeen);
        Log.d(TAG,"myApp--"+myApp.getDeviceListBeen().size());
    }

    //更新输入MAC搜索界面
    private void refreshListView(String searchStr){

        Log.d(TAG,"refreshListView--"+searchStr);

        Log.d(TAG,"mDeviceList--"+mDeviceListBeen.size());

        //每次输入都先清除匹配的列表
        searchDeviceListBeen.clear();

        //搜索为空时显示所有数据
        if (searchStr==null || searchStr.trim().length()==0){
            /*adapter = new MyDeviceAdapter(getContext(),mDeviceList);
            lv_detector.setAdapter(adapter);*/

            deviceListAdapter=new DeviceListAdapter(getContext(),mDeviceListBeen);
            lv_detector.setAdapter(deviceListAdapter);
            lv_detector.invalidateViews();

            Log.d(TAG, "显示所有");
        }

        //搜索不为空时显示匹配的数据
        if (/*searchStr!=null && */searchStr.trim().length()!=0){
            /*for (DetectorInfoBean.DeviceListBean d:mDeviceList) {
                if (d.getMac().toLowerCase().contains(searchStr.toLowerCase())){
                    searchDeviceList.add(d);
                }
            }*/
            for (DetectorLists.DetectorListBean d:mDeviceListBeen) {
                if (d.getMac().toLowerCase().contains(searchStr.toLowerCase())){
                    searchDeviceListBeen.add(d);
                }
            }
            Log.d(TAG,"searchDeviceList--"+searchDeviceListBeen.size());
            /*adapter=new MyDeviceAdapter(getContext(),searchDeviceList);
            lv_detector.setAdapter(adapter);*/
            deviceListAdapter=new DeviceListAdapter(getContext(),searchDeviceListBeen);
            lv_detector.setAdapter(deviceListAdapter);
            lv_detector.invalidateViews();

            Log.d(TAG,"显示搜索匹配");
        }
    }

}
