package com.lgc.mysliding.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.lgc.mysliding.AmapNavigation.GPSNaviActivity;
import com.lgc.mysliding.AmapNavigation.overlay.DrivingRouteOverlay;
import com.lgc.mysliding.AmapNavigation.overlay.RideRouteOverlay;
import com.lgc.mysliding.AmapNavigation.overlay.WalkRouteOverlay;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.EnterActivity;


public class NavigateFragment extends Fragment implements View.OnClickListener, RouteSearch.OnRouteSearchListener {

    private static final String TAG="NavigateFragment";
    private static final int POI_RESULT=300;
    private static final int POI_REQUEST_START=301;
    protected static final int POI_REQUEST_END=302;
    private static final int POI_START_TYPE=303;
    private static final int POI_END_TYPE=308;

    private final int ROUTE_TYPE_DRIVE = 1;
    private final int ROUTE_TYPE_RIDE = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private int mRouteType;

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
    private LatLonPoint mStartPoint;//路线起点
    private LatLonPoint mEndPoint;//路线终点
    private Tip endTip;//终点
    private Marker startMarker;//路线起点标注
    private Marker endMarker;//路线终点标注
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveResult;//驾车线路结果
    private WalkRouteResult mWalkResult;//步行线路结果
    private RideRouteResult mRideResult;//骑行线路结果
    private ProgressDialog progDialog;//进度框
    private LocationSource.OnLocationChangedListener locationChangedListener;//地图定位回调
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private LatLonPoint mLocationPoint;//定位我的位置
    private Marker mLocationMarker;//定位标志

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
        mRouteSearch = new RouteSearch(getContext());//线路搜索实例
        //线路搜索监听
        mRouteSearch.setRouteSearchListener(this);
        //地图定位监听
        mAmap.setLocationSource(mLocationSource);
        mAmap.setMyLocationEnabled(true);
    }

    //设置选择出行方式是控件变化
    private void setSelectView(int index){
        //先还原所有控件
        clearAllSelect();
        switch (index){
            case 0://驾车
                drive.setBackgroundColor(getResources().getColor(R.color.tab_select));
                drive.setTextColor(Color.WHITE);
                break;
            case 1://骑行
                ride.setBackgroundColor(getResources().getColor(R.color.tab_select));
                ride.setTextColor(Color.WHITE);
                break;
            case 2://步行
                walk.setBackgroundColor(getResources().getColor(R.color.tab_select));
                walk.setTextColor(Color.WHITE);
                break;
        }
    }
    //还原所有选项控件
    private void clearAllSelect(){
        drive.setBackgroundColor(getResources().getColor(R.color.tab_unselect));
        drive.setTextColor(getResources().getColor(R.color.tab_text_unselect));
        ride.setBackgroundColor(getResources().getColor(R.color.tab_unselect));
        ride.setTextColor(getResources().getColor(R.color.tab_text_unselect));
        walk.setBackgroundColor(getResources().getColor(R.color.tab_unselect));
        walk.setTextColor(getResources().getColor(R.color.tab_text_unselect));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_navi://开始导航
                if (mStartPoint!=null && mEndPoint!=null){
                    Intent start=new Intent(getContext(), GPSNaviActivity.class);
                    Bundle naviBundle=new Bundle();
                    naviBundle.putParcelable("startPoint",mStartPoint);
                    naviBundle.putParcelable("endPoint",mEndPoint);
                    naviBundle.putInt("route_type",mRouteType);
                    start.putExtras(naviBundle);
                    startActivity(start);
                }else {
                    Toast.makeText(getContext(),"起点或终点不能为空",Toast.LENGTH_SHORT).show();
                }
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
                setSelectView(0);
                searchRouteResult(ROUTE_TYPE_DRIVE,RouteSearch.DrivingDefault);
                break;
            case R.id.tv_ride://骑行
                setSelectView(1);
                searchRouteResult(ROUTE_TYPE_RIDE,RouteSearch.RidingDefault);
                break;
            case R.id.tv_walk://步行
                setSelectView(2);
                searchRouteResult(ROUTE_TYPE_WALK,RouteSearch.WalkDefault);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==POI_RESULT){
            Bundle tipBundle=data.getExtras();
            boolean my_location__type=tipBundle.getBoolean("my_location_type");
            switch (requestCode){
                case POI_REQUEST_START://获得起点,设置起点标志
                    if (my_location__type){//使用当前位置为起点
                        mStartPoint=mLocationPoint;//设置路线起点
                        start_point.setText("我的位置");
                    }else {//使用输入的位置为起点
                        startTip = tipBundle.getParcelable("select_tip");
                        if (startTip != null) {

                            Log.d(TAG, "startTip--" + startTip.getName());
                            Log.d(TAG, "startTip--" + startTip.getPoint().getLatitude() + "\n"
                                    + startTip.getPoint().getLongitude());

                            mStartPoint = startTip.getPoint();//设置路线起点
                            start_point.setText(startTip.getName());
                        }
                    }
                        //设置起点标志
                        /*LatLng startLatLng=new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude());
                            if (startMarker!=null){
                                startMarker.remove();
                            }
                            startMarker=mAmap.addMarker(new MarkerOptions()
                                    .position(startLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_start)));

                            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng,14));*/
                        //终点不为空则搜索驾车路线
                        if (mEndPoint!=null){
                            if (!(mStartPoint.equals(mEndPoint))){
                                LatLng startLatLng=new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude());
                                if (startMarker!=null){
                                    startMarker.remove();
                                }
                                startMarker=mAmap.addMarker(new MarkerOptions()
                                        .position(startLatLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_start)));

                                searchRouteResult(ROUTE_TYPE_DRIVE,RouteSearch.DrivingDefault);
                                setSelectView(0);
                            }else {//起点与终点相同，清除地图上起点标志
                                if (startMarker!=null){
                                    startMarker.remove();
                                }
                                Toast.makeText(getContext(),"起点和终点不能相同",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            //终点为空，只显示起点
                            LatLng startLatLng=new LatLng(mStartPoint.getLatitude(),mStartPoint.getLongitude());
                            if (startMarker!=null){
                                startMarker.remove();
                            }
                            startMarker=mAmap.addMarker(new MarkerOptions()
                                    .position(startLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_start)));

                            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng,14));
                        }
                    break;
                case POI_REQUEST_END://获得终点,设置终点标志
                    if (my_location__type){//使用当前位置为终点
                        mEndPoint=mLocationPoint;//设置路线终点
                        end_point.setText("我的位置");
                    }else {//使用输入的位置为终点
                        endTip=tipBundle.getParcelable("select_tip");
                        if (endTip != null) {

                            Log.d(TAG,"endTip--"+endTip.getName());
                            Log.d(TAG,"endTip--"+endTip.getPoint().getLatitude()+"\n"
                                    +endTip.getPoint().getLongitude());

                            mEndPoint=endTip.getPoint();//设置路线终点
                            end_point.setText(endTip.getName());
                        }
                    }
                   /* LatLng endLatLng=new LatLng(mEndPoint.getLatitude(),mEndPoint.getLongitude());
                    if (endMarker!=null){
                        endMarker.remove();
                    }
                    endMarker=mAmap.addMarker(new MarkerOptions()
                            .position(endLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_end)));

                    mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng,14));*/
                    //起终点不为空则搜索驾车路线
                    if (mStartPoint!=null){
                        if (!(mEndPoint.equals(mStartPoint))){
                            LatLng endLatLng=new LatLng(mEndPoint.getLatitude(),mEndPoint.getLongitude());
                            if (endMarker!=null){
                                endMarker.remove();
                            }
                            endMarker=mAmap.addMarker(new MarkerOptions()
                                    .position(endLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_end)));

                            searchRouteResult(ROUTE_TYPE_DRIVE,RouteSearch.DrivingDefault);
                            setSelectView(0);
                        }else {//终点与起点相同，清除地图上终点标志
                            if (endMarker!=null){
                                endMarker.remove();
                            }
//                            end_point.setText("");
                            Toast.makeText(getContext(),"起点和终点不能相同",Toast.LENGTH_SHORT).show();
                        }
                    }else {//如果起点为空，只显示终点
                        LatLng endLatLng=new LatLng(mEndPoint.getLatitude(),mEndPoint.getLongitude());
                        if (endMarker!=null){
                            endMarker.remove();
                        }
                        endMarker=mAmap.addMarker(new MarkerOptions()
                                .position(endLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_end)));

                        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng,14));
                    }
                    break;
            }
        }
    }

    /**
     * 搜索路径规划方案
     * @param routeType
     * @param mode
     */
    private void searchRouteResult(int routeType,int mode){
        if (mStartPoint==null){
            Toast.makeText(getContext(),"请输入起点",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEndPoint==null){
            Toast.makeText(getContext(),"请输入终点",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mStartPoint.equals(mEndPoint)){
            Toast.makeText(getContext(),"起点与终点不能相同",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG,"路线起点-"+mStartPoint.getLatitude()+"-"+mStartPoint.getLongitude());
        Log.d(TAG,"路线终点-"+mEndPoint.getLatitude()+"-"+mEndPoint.getLongitude());
        showProgressDialog();//显示进度框
        RouteSearch.FromAndTo fromAndTo=new RouteSearch.FromAndTo(mStartPoint,mEndPoint);

        if (routeType==ROUTE_TYPE_DRIVE){//驾车路线规划方案
            Log.d(TAG,"驾车");
            RouteSearch.DriveRouteQuery driveRouteQuery=
                    new RouteSearch.DriveRouteQuery(fromAndTo,mode,null,null,"");
            mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);//异步搜索驾车线路

            //设置路线类型为驾车
            mRouteType=ROUTE_TYPE_DRIVE;

        }else if (routeType==ROUTE_TYPE_RIDE){
            Log.d(TAG,"骑行");
            RouteSearch.RideRouteQuery rideRouteQuery=new RouteSearch.RideRouteQuery(fromAndTo,mode);
            mRouteSearch.calculateRideRouteAsyn(rideRouteQuery);//异步搜索骑行线路
            //设置路线类型为骑行
            mRouteType=ROUTE_TYPE_RIDE;
        }else if (routeType==ROUTE_TYPE_WALK){
            Log.d(TAG,"步行");
            RouteSearch.WalkRouteQuery walkRouteQuery=new RouteSearch.WalkRouteQuery(fromAndTo,mode);
            mRouteSearch.calculateWalkRouteAsyn(walkRouteQuery);//异步搜索步行线路
            //设置路线类型为步行
            mRouteType=ROUTE_TYPE_WALK;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mMapView.onResume();
        /*//开始定位
        mAmap.setLocationSource(mLocationSource);
        mAmap.setMyLocationEnabled(true);
        if (!mapLocationClient.isStarted()){
            mapLocationClient.startLocation();
        }*/

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mMapView.onPause();
        /*//停止定位
        if (mapLocationClient.isStarted()){
            mapLocationClient.stopLocation();
        }*/
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
        outState.putParcelable("mStartTip",startTip);
        outState.putParcelable("mEndTip",endTip);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
        mView=null;
        mAmap=null;
    }

    /**
     * 公交线路搜索回调
     * @param busRouteResult
     * @param i
     */
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /**
     * 驾车线路搜索回调
     * @param driveRouteResult
     * @param i
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        dissmissProgressDialog();//隐藏进度框
        //清除地图上的覆盖物
        mAmap.clear();
        if (i== AMapException.CODE_AMAP_SUCCESS){
            if (driveRouteResult!=null && driveRouteResult.getPaths()!=null){
                if (driveRouteResult.getPaths().size()>0){
                    mDriveResult = driveRouteResult;
                    Log.d(TAG,"getPaths-"+mDriveResult.getPaths().size());
                    //把所有路线全画出来
                    for (int j=0;j<mDriveResult.getPaths().size();j++){
                        DrivePath drivePath=mDriveResult.getPaths().get(j);
                        DrivingRouteOverlay drivingRouteOverlay=new DrivingRouteOverlay(getContext()
                                ,mAmap
                                , drivePath
                                ,mDriveResult.getStartPos()
                                ,mDriveResult.getTargetPos(),null);
                        drivingRouteOverlay.setNodeIconVisibility(true);//设置节点marker显示
                        drivingRouteOverlay.setIsColorfulline(true);//设置颜色表示交通情况、
                        drivingRouteOverlay.removeFromMap();
                        drivingRouteOverlay.addToMap();
                        drivingRouteOverlay.zoomToSpan();
                    }
                    /*DrivePath drivePath=mDriveResult.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay=new DrivingRouteOverlay(getContext()
                            ,mAmap
                            , drivePath
                            ,mDriveResult.getStartPos()
                            ,mDriveResult.getTargetPos(),null);
                    drivingRouteOverlay.setNodeIconVisibility(true);//设置节点marker显示
                    drivingRouteOverlay.setIsColorfulline(true);//设置颜色表示交通情况、
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();*/
                }else if (driveRouteResult!=null && driveRouteResult.getPaths()==null){
                    Toast.makeText(getContext(),"无相关线路",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(),"搜索无结果",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(),"搜索失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 步行线路搜索回调
     * @param walkRouteResult
     * @param i
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        dissmissProgressDialog();//隐藏进度框
        //清除地图上的覆盖物
        mAmap.clear();
        if (i== AMapException.CODE_AMAP_SUCCESS){
            if (walkRouteResult!=null && walkRouteResult.getPaths()!=null){
                if (walkRouteResult.getPaths().size()>0){
                    mWalkResult=walkRouteResult;
                    WalkPath walkPath=mWalkResult.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay=new WalkRouteOverlay(getContext(),
                            mAmap, walkPath, mWalkResult.getStartPos(), mWalkResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                }else if (walkRouteResult!=null && walkRouteResult.getPaths()==null){
                    Toast.makeText(getContext(),"无相关线路",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(),"搜索无结果",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(),"搜索失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 骑行线路搜索回调
     * @param rideRouteResult
     * @param i
     */
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        dissmissProgressDialog();//隐藏进度框
        //清除地图上的覆盖物
        mAmap.clear();
        if (i== AMapException.CODE_AMAP_SUCCESS){
            if (rideRouteResult!=null && rideRouteResult.getPaths()!=null){
                if (rideRouteResult.getPaths().size()>0){
                    mRideResult=rideRouteResult;
                    RidePath ridePath=mRideResult.getPaths().get(0);
                    RideRouteOverlay rideRouteOverlay=new RideRouteOverlay(getContext(),
                            mAmap,ridePath,mRideResult.getStartPos(),mRideResult.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                }else if (rideRouteResult!=null && rideRouteResult.getPaths()==null){
                    Toast.makeText(getContext(),"无相关线路",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(),"搜索无结果",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(),"搜索失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 定位声明
     */
    private com.amap.api.maps.LocationSource mLocationSource=new com.amap.api.maps.LocationSource() {
        //启动定位
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            locationChangedListener = onLocationChangedListener;
            if (mapLocationClient==null){
                mapLocationClient=new AMapLocationClient(getContext());
                mapLocationClientOption=new AMapLocationClientOption();
                //设置客户端定位监听回调
                mapLocationClient.setLocationListener(aMapLocationListener);
                //设置客户端监听参数
                mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                mapLocationClientOption.setNeedAddress(true);//返回地址信息
                mapLocationClientOption.setWifiScan(true);//强行刷新WiFi
                mapLocationClientOption.setMockEnable(false);//不允许模拟位置
                mapLocationClientOption.setOnceLocation(false);//是否定位一次
                mapLocationClientOption.setInterval(300000);//定位时间间隔为5分钟
                mapLocationClient.setLocationOption(mapLocationClientOption);
                //启动监听
                mapLocationClient.startLocation();
                Log.d(TAG,"activate开始定位");
            }
        }

        @Override
        public void deactivate() {
            Log.d(TAG,"deactivate停止定位");
        }
    };

    //客户端定位监听回调
    private AMapLocationListener aMapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (locationChangedListener!=null && aMapLocation!=null){
                if (aMapLocation.getErrorCode()==0){
                    //获取当前位置
                    LatLng latLng=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    mLocationPoint=new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    //设置起点位置为当前定位 // TODO: 2017/3/15
                    mStartPoint=mLocationPoint;
                    //显示当前定位
//                    mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));
                    if (mLocationMarker!=null){
                        mLocationMarker.remove();
                    }
                    MarkerOptions options=new MarkerOptions();
                    options.position(latLng);
                    options.title("您的位置");
                    options.visible(true);
                    BitmapDescriptor bd= BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.my_location));
                    options.icon(bd);
                    //设置当前位置标记
                    mLocationMarker = mAmap.addMarker(options);
//                    mLocationMarker.showInfoWindow();
                    Log.d(TAG,"定位成功");
                }else {
                    Log.d(TAG,"定位失败--"+"error code--"+aMapLocation.getErrorCode()+"\n"+
                            "error info--"+aMapLocation.getErrorInfo());
                }
            }
        }
    };
}
