package com.lgc.mysliding.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;

import java.io.IOException;
import java.util.List;

/**
 *轨迹查询的
 * 高德地图自定义 infoWindow 的 adapter
 */
public class TraceAmapInfoWin implements AMap.InfoWindowAdapter{
    private Context context= MyApp.getMyApp().getBaseContext();
    private String enterTime;
    private String leaveTime;
    private String enter_address;
    private LatLng latLng;
    private double lat;
    private double lng;
    private TextView tv_enter_time;
    private TextView tv_leave_time;
    private TextView tv_address;

    @Override
    public View getInfoWindow(Marker marker) {
        initTraceMarker(marker);
        View mView=initTraceInfoView();
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initTraceMarker(Marker marker){
        enterTime=marker.getSnippet();
        leaveTime=marker.getTitle();
        latLng=marker.getPosition();
        lat=latLng.latitude;
        lng=latLng.longitude;
        //换算经纬度为地址
        Geocoder geocoder=new Geocoder(context);
        try {
            List<Address> addressList=geocoder.getFromLocation(lat,lng,1);
            if(addressList.size()>0){
                Address addr=addressList.get(0);
                enter_address =addr.getAddressLine(0).substring(3).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View initTraceInfoView(){
        View view= LayoutInflater.from(context).inflate(R.layout.trace_amap_infowin,null);
        tv_enter_time = (TextView) view.findViewById(R.id.tv_snippet_enter);
        tv_leave_time = (TextView) view.findViewById(R.id.tv_tittle_leave);
        tv_address = (TextView) view.findViewById(R.id.tv_enter_address);

        tv_enter_time.setText(enterTime);
        tv_leave_time.setText(leaveTime);
        tv_address.setText(enter_address);
        return view;
    }
}
