package com.lgc.mysliding.AmapNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.lgc.mysliding.R;

/**
 * Created by Administrator on 2017/3/6.
 * GPS导航
 */
public class GPSNaviActivity extends NaviBaseActivity {

    protected final String TAG="GPSNaviActivity";
    private final static int ROUTE_TYPE_DRIVE = 1;
    private final static int ROUTE_TYPE_RIDE = 2;
    private final static int ROUTE_TYPE_WALK = 3;
    private int mRouteType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_navigate);

        Intent naviIntent=getIntent();
        Bundle naviBundle=naviIntent.getExtras();
        LatLonPoint start=naviBundle.getParcelable("startPoint");
        LatLonPoint end=naviBundle.getParcelable("endPoint");
        mRouteType=naviBundle.getInt("route_type",1);
        if (start != null) {
            mStartLatlng=new NaviLatLng(start.getLatitude(),start.getLongitude());
        }
        if (end != null) {
            mEndLatlng=new NaviLatLng(end.getLatitude(),end.getLongitude());
        }
        if (startList!=null && startList.size()>0){
            startList.clear();
        }
        if (endList!=null && endList.size()>0){
            endList.clear();
        }
        if (startList != null) {
            startList.add(mStartLatlng);
        }
        if (endList != null) {
            endList.add(mEndLatlng);
        }
        mAmapNaviView= (AMapNaviView) findViewById(R.id.navi_view);
        mAmapNaviView.onCreate(savedInstanceState);
        mAmapNaviView.setAMapNaviViewListener(this);
    }

    /**
     * 初始化导航成功回调
     */
    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy=0;
        try {
            strategy=mAmapNavi.strategyConvert(true,false,false,false,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mRouteType==ROUTE_TYPE_DRIVE){
            mAmapNavi.calculateDriveRoute(startList,endList,mWayPointList,strategy);
            Log.d(TAG,"驾车");
        }else if (mRouteType==ROUTE_TYPE_WALK){
            mAmapNavi.calculateWalkRoute(mStartLatlng,mEndLatlng);
            Log.d(TAG,"步行");
        }else if (mRouteType==ROUTE_TYPE_RIDE){
            mAmapNavi.calculateRideRoute(mStartLatlng,mEndLatlng);
            Log.d(TAG,"骑行");
        }
    }

    /**
     * 路线计算成功回调
     */
    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAmapNavi.startNavi(NaviType.EMULATOR);
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        super.onCalculateMultipleRoutesSuccess(ints);
        Log.d(TAG,"multiple-"+ints.length);
    }


}
