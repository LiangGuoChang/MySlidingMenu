package com.lgc.mysliding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.TextureMapView;
import com.amap.api.services.help.Tip;
import com.lgc.mysliding.AmapNavigation.GPSNaviActivity;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.EnterActivity;


public class NavigateFragment extends Fragment implements View.OnClickListener {

    private static final String TAG="NavigateFragment";
    private static final int POI_RESULT=300;
    private static final int POI_REQUEST_START=301;
    private static final int POI_REQUEST_END=302;
    private static final int POI_START_TYPE=303;
    private static final int POI_END_TYPE=308;

    private View mView;
    private Button btn_navi;
    private TextureMapView mMapView;
    private com.amap.api.maps.AMap mAmap;
    private TextView start_point;//起点
    private TextView end_point;//终点
    private TextView drive;//驾车
    private TextView ride;//骑行
    private TextView walk;//步行
    private Tip startTip;//起点
    private Tip endTip;//终点

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_navigate, container, false);
        }
        initView(savedInstanceState);
        return mView;
    }

    //初始化控件
    private void initView(Bundle bundle){
        mMapView = (TextureMapView)mView.findViewById(R.id.nv_mapview);
        mMapView.onCreate(bundle);
        if (mAmap==null){
            mAmap = mMapView.getMap();
        }
        start_point = (TextView)mView.findViewById(R.id.tv_start_poi);
        start_point.setOnClickListener(this);
        end_point = (TextView)mView.findViewById(R.id.tv_end_poi);
        end_point.setOnClickListener(this);
        drive = (TextView)mView.findViewById(R.id.tv_drive);
        drive.setOnClickListener(this);
        ride = (TextView)mView.findViewById(R.id.tv_ride);
        ride.setOnClickListener(this);
        walk = (TextView)mView.findViewById(R.id.tv_walk);
        walk.setOnClickListener(this);
        btn_navi = (Button)mView.findViewById(R.id.btn_navi);
        btn_navi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_navi://开始导航
                Intent start=new Intent(getContext(), GPSNaviActivity.class);
                startActivity(start);
                break;
            case R.id.tv_start_poi://获取起点位置
                Intent startPoi=new Intent(getContext(), EnterActivity.class);
                Bundle startBundle=new Bundle();
                startBundle.putInt("item",3);
                startBundle.putInt("poiType",POI_START_TYPE);
                startPoi.putExtras(startBundle);
                startActivityForResult(startPoi,POI_REQUEST_START);
                break;
            case R.id.tv_end_poi://获取终点位置
                Intent endPoi=new Intent(getContext(), EnterActivity.class);
                Bundle endBundle=new Bundle();
                endBundle.putInt("item",3);
                endBundle.putInt("poiType",POI_END_TYPE);
                endPoi.putExtras(endBundle);
                startActivityForResult(endPoi,POI_REQUEST_END);
                break;
            case R.id.tv_drive://驾车
                break;
            case R.id.tv_ride://骑行
                break;
            case R.id.tv_walk://步行
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==POI_RESULT){
            Bundle tipBundle=data.getExtras();
            switch (requestCode){
                case POI_REQUEST_START://获得起点
                    startTip=tipBundle.getParcelable("select_tip");
                    if (startTip != null) {
                        Log.d(TAG,"startTip--"+startTip.getName());
                        start_point.setText(startTip.getName());
                    }
                    break;
                case POI_REQUEST_END://获得终点
                    endTip=tipBundle.getParcelable("select_tip");
                    if (endTip != null) {
                        Log.d(TAG,"endTip--"+endTip.getName());
                        end_point.setText(endTip.getName());
                    }
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        mView=null;
        mAmap=null;
    }
}
