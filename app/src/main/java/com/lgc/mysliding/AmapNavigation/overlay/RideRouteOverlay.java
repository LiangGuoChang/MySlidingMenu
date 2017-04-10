package com.lgc.mysliding.AmapNavigation.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideStep;
import com.lgc.mysliding.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 * 骑行路线图层类
 */
public class RideRouteOverlay extends BaseRouteOverlay{

    private PolylineOptions mPolylineOptions;
    private BitmapDescriptor walkStationDescriptor= null;
    private Context mContext;
    private RidePath ridePath;

    /**
     * 根据参数构造骑行线路图层类对象
     * @param context 上下文
     * @param aMap 地图对象
     * @param path 骑行线路方案
     * @param start 起点
     * @param end 终点
     */
    public RideRouteOverlay(Context context,
           AMap aMap, RidePath path, LatLonPoint start,LatLonPoint end) {
        super(context);
        mContext=context;
        mAMap=aMap;
        this.ridePath=path;
        startLatlng=new LatLng(start.getLatitude(),start.getLongitude());
        endLatlng=new LatLng(end.getLatitude(), end.getLongitude());
    }

    /**
     * 添加骑行路线到地图中。
     * @since V3.5.0
     */
    public void addToMap() {

        initPolylineOptions();
        try {
            List<RideStep> ridePaths = ridePath.getSteps();
            mPolylineOptions.add(startLatlng);
            for (int i = 0; i < ridePaths.size(); i++) {
                RideStep rideStep = ridePaths.get(i);
                LatLng latLng = new LatLng(
                        rideStep.getPolyline().get(0).getLatitude(),
                        rideStep.getPolyline().get(0).getLongitude());

                addRideStationMarkers(rideStep, latLng);
                addRidePolyLines(rideStep);
            }
            mPolylineOptions.add(endLatlng);
            addStartAndEndMarker();

            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        if(walkStationDescriptor == null) {
            walkStationDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.amap_ride);
        }
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    /**
     * @param rideStep
     * @param position
     */
    private void addRideStationMarkers(RideStep rideStep, LatLng position) {
        addStationMarker(new MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + rideStep.getAction()
                        + "\n\u9053\u8DEF:" + rideStep.getRoad())
                .snippet(rideStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor));
    }

    /**
     * @param rideStep
     */
    private void addRidePolyLines(RideStep rideStep) {
        List<LatLonPoint> shapes=rideStep.getPolyline();
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = new LatLng(point.getLatitude(),point.getLongitude());
            lineShapes.add(latLngTemp);
        }
        mPolylineOptions.addAll(lineShapes);
    }
    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
}
