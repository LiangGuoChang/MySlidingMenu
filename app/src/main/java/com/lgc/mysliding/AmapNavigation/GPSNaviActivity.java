package com.lgc.mysliding.AmapNavigation;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.lgc.mysliding.R;

/**
 * Created by Administrator on 2017/3/6.
 * GPS导航
 */
public class GPSNaviActivity extends NaviBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gps_navigate);
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
        mAmapNavi.calculateDriveRoute(startList,endList,mWayPointList,strategy);
    }

    /**
     * 路线计算成功回调
     */
    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAmapNavi.startNavi(NaviType.GPS);
    }
}
