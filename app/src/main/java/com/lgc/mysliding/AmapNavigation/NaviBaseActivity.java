package com.lgc.mysliding.AmapNavigation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.lgc.mysliding.utils.NaviErrorInfo;
import com.lgc.mysliding.utils.TTSController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 * 高德地图导航基类
 */
public class NaviBaseActivity extends Activity implements AMapNaviListener,AMapNaviViewListener {

    protected AMapNaviView mAmapNaviView;
    protected AMapNavi mAmapNavi;
    protected TTSController mTtsManager;//讯飞语音
    protected NaviLatLng mStartLatlng=new NaviLatLng(23.109233,114.414505);//起点投资大厦位置
    protected NaviLatLng mEndLatlng=new NaviLatLng(23.10255,114.41233);//终点江畔花园位置
    protected List<NaviLatLng> startList=new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> endList=new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> mWayPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();

        //设置导航监听
        mAmapNavi=AMapNavi.getInstance(getApplicationContext());
        mAmapNavi.addAMapNaviListener(this);
        mAmapNavi.addAMapNaviListener(mTtsManager);

        //设置模拟导航行车速度
        mAmapNavi.setEmulatorNaviSpeed(60);
        startList.add(mStartLatlng);
        endList.add(mEndLatlng);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAmapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAmapNaviView.onPause();
        //仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
        mTtsManager.stopSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAmapNaviView.onDestroy();

        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAmapNavi.stopNavi();
        mAmapNavi.destroy();
        mTtsManager.destroy();
    }

    /**
     * 以下是AMapNaviListener监听的回调方法
     */

    @Override
    public void onInitNaviFailure() {
        //定位失败
        Toast.makeText(getApplicationContext(),"定位失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        //定位成功
        Toast.makeText(getApplicationContext(),"定位成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartNavi(int i) {
        //开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        //当前定位回调
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        //播报类型和播报文字回调
    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
    }

    @Override
    public void onArriveDestination() {
        //到达目的地
    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {
        //到达目的地，有统计信息回调
    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

    }

    @Override
    public void onCalculateRouteSuccess() {
        //路线计算成功
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        //路线计算失败
        Log.e("dm", "--------------------------------------------");
        Log.i("dm", "路线计算失败：错误码=" + i + ",Error Message= " + NaviErrorInfo.getError(i));
        Log.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        Log.e("dm", "--------------------------------------------");
        Toast.makeText(this, "errorInfo：" + i + ",Message：" + NaviErrorInfo.getError(i), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //道路拥塞后重新计算回调
    }

    @Override
    public void onArrivedWayPoint(int i) {
        //到达途经点回调
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        //Gps开关状态回调
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {
        //过时
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        //导航过程中信息更新回调，请看NaviInfo的具体说明
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        //已过时
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        //已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        //显示转弯回调
    }

    @Override
    public void hideCross() {
        //隐藏转弯回调
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        //显示车道回调
    }

    @Override
    public void hideLaneInfo() {
        //隐藏车道回调
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        //多路线计算成功回调
    }

    @Override
    public void notifyParallelRoad(int i) {
        if (i == 0) {
            Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主辅路过渡");
            return;
        }
        if (i == 1) {
            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在主路");
            return;
        }
        if (i == 2) {
            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();

            Log.d("wlx", "当前在辅路");
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式统计信息
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式拥堵信息
    }

    /**
     * 以下是AMapNaviViewListener监听的回调方法
     */

    @Override
    public void onNaviSetting() {
        //底部导航设置点击回调
    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {
        //地图模式，锁屏或者锁车
    }

    @Override
    public void onNaviTurnClick() {
        //转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        //下一个道路view的点击回调
    }

    @Override
    public void onScanViewButtonClick() {
        //全览按钮点击回调
    }

    @Override
    public void onLockMap(boolean b) {
        //锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        Log.d("wlx", "导航页面加载成功");
        Log.d("wlx", "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }
}
