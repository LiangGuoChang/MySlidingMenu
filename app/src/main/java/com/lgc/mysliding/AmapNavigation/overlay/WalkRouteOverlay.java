package com.lgc.mysliding.AmapNavigation.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 * 步行线路图层类
 */
public class WalkRouteOverlay extends BaseRouteOverlay{

    private Context mContext;
    private WalkPath walkPath;
    private PolylineOptions mPolylineOptions;
    private BitmapDescriptor walkStationDescriptor= null;

    /**
     * 根据参数构造图层类对象
     * @param context 上下文
     * @param aMap 地图对象
     * @param path 步行规划的一个方案
     * @param start
     * @param end
     */
    public WalkRouteOverlay(Context context,
           AMap aMap, WalkPath path, LatLonPoint start, LatLonPoint end) {
        super(context);
        mContext=context;
        mAMap=aMap;
        this.walkPath=path;
        startLatlng=new LatLng(start.getLatitude(),start.getLongitude());
        endLatlng=new LatLng(end.getLatitude(),end.getLongitude());
    }

    /**
     * 添加步行路线到地图中。
     * @since V2.1.0
     */
    public void addToMap() {

        initPolylineOptions();
        try {
            List<WalkStep> walkPaths = walkPath.getSteps();
            mPolylineOptions.add(startLatlng);
            for (int i = 0; i < walkPaths.size(); i++) {
                WalkStep walkStep = walkPaths.get(i);
                LatLng latLng = new LatLng(
                        walkStep.getPolyline().get(0).getLatitude(),
                        walkStep.getPolyline().get(0).getLongitude());
                addWalkStationMarkers(walkStep, latLng);
                addWalkPolyLines(walkStep);

            }
            mPolylineOptions.add(endLatlng);
            addStartAndEndMarker();

            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查这一步的最后一点和下一步的起始点之间是否存在空隙
     */
    private void checkDistanceToNextStep(WalkStep walkStep,
                                         WalkStep walkStep1) {
        LatLonPoint lastPoint = getLastWalkPoint(walkStep);
        LatLonPoint nextFirstPoint = getFirstWalkPoint(walkStep1);
        if (!(lastPoint.equals(nextFirstPoint))) {
            addWalkPolyLine(lastPoint, nextFirstPoint);
        }
    }

    /**
     * @param walkStep
     * @return
     */
    private LatLonPoint getLastWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(walkStep.getPolyline().size() - 1);
    }

    /**
     * @param walkStep
     * @return
     */
    private LatLonPoint getFirstWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(0);
    }

    private void addWalkPolyLine(LatLonPoint pointFrom, LatLonPoint pointTo) {
        addWalkPolyLine(new LatLng(pointFrom.getLatitude(),pointFrom.getLongitude()),
                new LatLng(pointTo.getLatitude(),pointTo.getLongitude()));
    }

    private void addWalkPolyLine(LatLng latLngFrom, LatLng latLngTo) {
        mPolylineOptions.add(latLngFrom, latLngTo);
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        if(walkStationDescriptor == null) {
            walkStationDescriptor = getWalkBitmapDescriptor();
        }

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getWalkColor()).width(getRouteWidth());
    }

    /**
     * @param walkStep
     * @param position
     */
    private void addWalkStationMarkers(WalkStep walkStep, LatLng position) {
        addStationMarker(new MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + walkStep.getAction()
                        + "\n\u9053\u8DEF:" + walkStep.getRoad())
                .snippet(walkStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor));
    }

    /**
     * @param walkStep
     */
    private void addWalkPolyLines(WalkStep walkStep) {
        List<LatLonPoint> shapes=walkStep.getPolyline();
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
