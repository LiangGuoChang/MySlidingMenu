package com.lgc.mysliding.adapter;

import android.content.Context;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.lgc.mysliding.MyApp;
import com.lgc.mysliding.R;

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
        LatLonPoint point=new LatLonPoint(lat,lng);
        //换算经纬度为地址
        GeocodeSearch geocodeSearch=new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                RegeocodeAddress address=regeocodeResult.getRegeocodeAddress();
                String addr=address.getFormatAddress();
                enter_address=addr.substring(3);
                Log.d("aaa","addr::"+addr);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        RegeocodeQuery query=new RegeocodeQuery(point,50,GeocodeSearch.GPS);
        /*try {
            geocodeSearch.getFromLocation(query);
        } catch (AMapException e) {
            e.printStackTrace();
            Log.d("aaa","initTraceMarker::"+e.getMessage());
        }*/
        geocodeSearch.getFromLocationAsyn(query);


        Geocoder geocoder=new Geocoder(context);
        /*Geocoder geocoder=new Geocoder(context);
        try {
            List<Address> addressList=geocoder.getFromLocation(lat,lng,1);
            if(addressList.size()>0){
                Address addr=addressList.get(0);
                enter_address =addr.getAddressLine(0).substring(3).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
