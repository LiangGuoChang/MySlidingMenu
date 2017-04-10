package com.lgc.mysliding.AmapNavigation.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.lgc.mysliding.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lgc on 2017/3/13.
 * 路线规划地图显示层基类
 */
public class BaseRouteOverlay {

    protected List<Marker> stationMarkers=new ArrayList<Marker>();
    protected List<Polyline> allPolylines=new ArrayList<Polyline>();
    protected Marker startMarker;
    protected Marker endMarker;
    protected LatLng startLatlng;
    protected LatLng endLatlng;
    protected AMap mAMap;
    private Bitmap startBit,endBit,walkBit,driveBit;
    private Context mContext;
    protected boolean nodeIconVisible=true;

    public BaseRouteOverlay(Context context){
        mContext=context;
    }

    /**
     * 去掉BusRouteOverlay上所有的Marker。
     * @since V2.1.0
     */
    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        for (Marker marker : stationMarkers) {
            marker.remove();
        }
        for (Polyline line : allPolylines) {
            line.remove();
        }
        destroyBit();
    }
    private void destroyBit() {
        if (startBit != null) {
            startBit.recycle();
            startBit = null;
        }
        if (endBit != null) {
            endBit.recycle();
            endBit = null;
        }
        if (walkBit != null) {
            walkBit.recycle();
            walkBit = null;
        }
        if (driveBit != null) {
            driveBit.recycle();
            driveBit = null;
        }
    }
    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_start);
    }
    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_end);
    }
    /**
     * 给步行Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getWalkBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_man);
    }
    /**
     * 给驾车Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_car);
    }

    protected void addStartAndEndMarker() {
        startMarker = mAMap.addMarker((new MarkerOptions())
                .position(startLatlng).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9"));
        // startMarker.showInfoWindow();

        endMarker = mAMap.addMarker((new MarkerOptions()).position(endLatlng)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9"));
        // mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    /**
     * 移动镜头到当前的视角。
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (startLatlng != null) {
            if (mAMap == null)
                return;
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, 50));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startLatlng.latitude, startLatlng.longitude));
        b.include(new LatLng(endLatlng.latitude, endLatlng.longitude));
        return b.build();
    }

    /**
     * 路段节点图标控制显示接口。
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    public void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
                for (int i = 0; i < this.stationMarkers.size(); i++) {
                    this.stationMarkers.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void addStationMarker(MarkerOptions options) {
        if(options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if(marker != null) {
            stationMarkers.add(marker);
        }

    }

    protected void addPolyLine(PolylineOptions options) {
        if(options == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(options);
        if(polyline != null) {
            allPolylines.add(polyline);
        }
    }

    protected float getRouteWidth() {
        return 18f;
    }

    protected int getWalkColor() {
        return Color.parseColor("#6db74d");
    }

    /**
     * 自定义路线颜色。
     * return 自定义路线颜色。
     * @since V2.2.1
     */
    protected int getBusColor() {
        return Color.parseColor("#537edc");
    }

    protected int getDriveColor() {
        return Color.parseColor("#537edc");
    }

}
