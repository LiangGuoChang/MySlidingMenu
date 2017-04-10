package com.lgc.mysliding.AmapNavigation.overlay;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.lgc.mysliding.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 * 驾车导航路线图层类
 */
public class DrivingRouteOverlay extends BaseRouteOverlay{

    private Context mContext;
    private DrivePath drivePath;
    private List<LatLonPoint> throughPoints;
    private List<Marker> throughPointMarkerList = new ArrayList<Marker>();
    private boolean throughPointMarkerVisible = true;
    private float mWidth=25;
    private PolylineOptions mPolylineOptions;
    private PolylineOptions mPolylineOptionscolor;
    private List<LatLng> mLatLngsOfPath;
    private List<TMC> tmcs;
    private boolean isColorfulline = true;

    /**
     * 根据给定参数，构造一个导航路线图层类对象
     * @param context 上下文
     * @param aMap 地图对象
     * @param path 导航路线方案
     * @param start 起点
     * @param end 终点
     * @param throughPoints 途径点
     */
    public DrivingRouteOverlay(Context context, AMap aMap, DrivePath path,
           LatLonPoint start, LatLonPoint end, List<LatLonPoint> throughPoints) {
        super(context);
        mContext=context;
        mAMap=aMap;
        this.drivePath=path;
        startLatlng=new LatLng(start.getLatitude(),start.getLongitude());
        endLatlng=new LatLng(end.getLatitude(),end.getLongitude());
        this.throughPoints=throughPoints;
    }

    public float getRouteWidth() {
        return mWidth;
    }

    /**
     * 设置路线宽度
     *
     * @param mWidth 路线宽度，取值范围：大于0
     */
    public void setRouteWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    public void addToMap() {
        initPolylineOptions();
        try {
            if (mAMap == null) {
                return;
            }
            if (mWidth == 0 || drivePath == null) {
                return;
            }
            mLatLngsOfPath = new ArrayList<LatLng>();
            tmcs = new ArrayList<TMC>();
            List<DriveStep> drivePaths = drivePath.getSteps();
            //添加起点
            mPolylineOptions.add(startLatlng);
            for (DriveStep step : drivePaths) {
                List<LatLonPoint> latlonPoints = step.getPolyline();
                List<TMC> tmclist = step.getTMCs();
                tmcs.addAll(tmclist);
                addDrivingStationMarkers(step,
                        new LatLng(latlonPoints.get(0).getLatitude(),latlonPoints.get(0).getLongitude()));
                for (LatLonPoint latlonpoint : latlonPoints) {
                    LatLng latLng=new LatLng(latlonpoint.getLatitude(),latlonpoint.getLongitude());
                    mPolylineOptions.add(latLng);
                    mLatLngsOfPath.add(latLng);
                }
            }
            //添加终点
            mPolylineOptions.add(endLatlng);

            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();
            addThroughPointMarker();
            if (isColorfulline && tmcs.size()>0 ) {
                colorWayUpdate(tmcs);
                showcolorPolyline();
            }else {
                showPolyline();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }

    private void showcolorPolyline() {
        addPolyLine(mPolylineOptionscolor);

    }

    /**
     * @param driveStep
     * @param latLng
     */
    private void addDrivingStationMarkers(DriveStep driveStep, LatLng latLng) {
        addStationMarker(new MarkerOptions()
                .position(latLng)
                .title("\u65B9\u5411:" + driveStep.getAction()
                        + "\n\u9053\u8DEF:" + driveStep.getRoad())
                .snippet(driveStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor()));
    }

    private void addThroughPointMarker() {
        if (this.throughPoints != null && this.throughPoints.size() > 0) {
            LatLonPoint latLonPoint = null;
            for (int i = 0; i < this.throughPoints.size(); i++) {
                latLonPoint = this.throughPoints.get(i);
                if (latLonPoint != null) {
                    throughPointMarkerList.add(mAMap
                            .addMarker((new MarkerOptions())
                                    .position(
                                            new LatLng(latLonPoint
                                                    .getLatitude(), latLonPoint
                                                    .getLongitude()))
                                    .visible(throughPointMarkerVisible)
                                    .icon(getThroughPointBitDes())
                                    .title("\u9014\u7ECF\u70B9")));
                }
            }
        }
    }
    private BitmapDescriptor getThroughPointBitDes() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_through);

    }

    public void setThroughPointIconVisibility(boolean visible) {
        try {
            throughPointMarkerVisible = visible;
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //设置路线显示不同的颜色
    public void setIsColorfulline(boolean iscolorfulline) {
        this.isColorfulline = iscolorfulline;
    }

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     *
     * @param tmcSection
     */
    private void colorWayUpdate(List<TMC> tmcSection) {
        if (mAMap == null) {
            return;
        }
        if (tmcSection == null || tmcSection.size() <= 0) {
            return;
        }
        TMC segmentTrafficStatus;
        mPolylineOptionscolor = null;
        mPolylineOptionscolor = new PolylineOptions();
        mPolylineOptionscolor.width(getRouteWidth());
        List<Integer> colorList = new ArrayList<Integer>();
        mPolylineOptionscolor.add(startLatlng);
        mPolylineOptionscolor.add(new LatLng(tmcSection.get(0).getPolyline().get(0).getLatitude(),
                tmcSection.get(0).getPolyline().get(0).getLongitude()));
        colorList.add(getDriveColor());
        for (int i = 0; i < tmcSection.size(); i++) {
            segmentTrafficStatus = tmcSection.get(i);
            int color = getcolor(segmentTrafficStatus.getStatus());
            List<LatLonPoint> mployline = segmentTrafficStatus.getPolyline();
            for (int j = 1; j < mployline.size(); j++) {
                mPolylineOptionscolor.add(new LatLng(mployline.get(j).getLatitude(),
                        mployline.get(j).getLongitude()));
                colorList.add(color);
            }
        }
        mPolylineOptionscolor.add(endLatlng);
        colorList.add(getDriveColor());
        mPolylineOptionscolor.colorValues(colorList);
    }

    private int getcolor(String status) {

        if (status.equals("畅通")) {
            return Color.GREEN;
        } else if (status.equals("缓行")) {
            return Color.YELLOW;
        } else if (status.equals("拥堵")) {
            return Color.RED;
        } else if (status.equals("严重拥堵")) {
            return Color.parseColor("#990033");
        } else {
            return Color.parseColor("#537edc");
        }
    }

    @Override
    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startLatlng.latitude, startLatlng.longitude));
        b.include(new LatLng(endLatlng.latitude, endLatlng.longitude));
        if (this.throughPoints != null && this.throughPoints.size() > 0) {
            for (int i = 0; i < this.throughPoints.size(); i++) {
                b.include(new LatLng(
                        this.throughPoints.get(i).getLatitude(),
                        this.throughPoints.get(i).getLongitude()));
            }
        }
        return b.build();
    }

    @Override
    public void removeFromMap() {
        try {
            super.removeFromMap();
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).remove();
                }
                this.throughPointMarkerList.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取两点间距离
     *
     * @param start
     * @param end
     * @return
     */
    public static int calculateDistance(LatLng start, LatLng end) {
        double x1 = start.longitude;
        double y1 = start.latitude;
        double x2 = end.longitude;
        double y2 = end.latitude;
        return calculateDistance(x1, y1, x2, y2);
    }

    public static int calculateDistance(double x1, double y1, double x2, double y2) {
        final double NF_pi = 0.01745329251994329; // 弧度 PI/180
        x1 *= NF_pi;
        y1 *= NF_pi;
        x2 *= NF_pi;
        y2 *= NF_pi;
        double sinx1 = Math.sin(x1);
        double siny1 = Math.sin(y1);
        double cosx1 = Math.cos(x1);
        double cosy1 = Math.cos(y1);
        double sinx2 = Math.sin(x2);
        double siny2 = Math.sin(y2);
        double cosx2 = Math.cos(x2);
        double cosy2 = Math.cos(y2);
        double[] v1 = new double[3];
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2;
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2;
        v1[2] = siny1 - siny2;
        double dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);

        return (int) (Math.asin(dist / 2) * 12742001.5798544);
    }
    //获取指定两点之间固定距离点
    public static LatLng getPointForDis(LatLng sPt, LatLng ePt, double dis) {
        double lSegLength = calculateDistance(sPt, ePt);
        double preResult = dis / lSegLength;
        return new LatLng((ePt.latitude - sPt.latitude) * preResult + sPt.latitude, (ePt.longitude - sPt.longitude) * preResult + sPt.longitude);
    }

}
