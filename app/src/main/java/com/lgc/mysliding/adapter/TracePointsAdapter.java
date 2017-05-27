package com.lgc.mysliding.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.lgc.mysliding.R;
import com.lgc.mysliding.bean.TracePoints;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lgc on 2017/5/12.
 * 轨迹点列表适配器
 */
public class TracePointsAdapter extends BaseAdapter{
    private static final String TAG="TracePointsAdapter";
    private Context mContext;
    private List<TracePoints.TraceBean> mPoints;
    private TracePoints.TraceBean point;

    public TracePointsAdapter(Context context,List<TracePoints.TraceBean> list){
        this.mContext=context;
        this.mPoints=list;
    }

    @Override
    public int getCount() {
        return mPoints==null ? 0 : mPoints.size();
    }

    @Override
    public Object getItem(int i) {
        point=mPoints.get(i);
        return point;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final PointViewHolder viewHolder;
        if (view==null){
            viewHolder=new PointViewHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.trace_points_item,null);
            viewHolder.tv_address= (TextView) view.findViewById(R.id.tv_point_address);
            viewHolder.tv_enter_time= (TextView) view.findViewById(R.id.tv_enter_time);
            viewHolder.tv_leave_time= (TextView) view.findViewById(R.id.tv_leave_time);
            view.setTag(viewHolder);
        }else {
            viewHolder= (PointViewHolder) view.getTag();
        }
        TracePoints.TraceBean traceBean= (TracePoints.TraceBean) getItem(i);
        double lat=traceBean.getLatitude();
        double lon=traceBean.getLongitude();
        LatLonPoint point=new LatLonPoint(lat,lon);
        int enter_time=traceBean.getEnter_time();
        int leave_time=traceBean.getLeave_time();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String enterStr=sdf.format(new Date(enter_time*1000L));
        String leaveStr=sdf.format(new Date(leave_time*1000L));
        viewHolder.tv_enter_time.setText(enterStr);
        viewHolder.tv_leave_time.setText(leaveStr);
        GeocodeSearch geocodeSearch=new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                String formatAddress = regeocodeAddress.getFormatAddress();
                Log.i(TAG,"formatAddress::"+formatAddress);
                String mAddress = formatAddress.substring(6);
                viewHolder.tv_address.setText(mAddress);
                Log.i(TAG,"address::"+mAddress);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//                String address=geocodeResult.getGeocodeAddressList().get(0).getDistrict();
//                viewHolder.tv_address.setText(address);
//                Log.d(TAG,"address::"+address);
//                Log.i(TAG,"address::"+address);
            }
        });
        RegeocodeQuery regeocodeQuery=new RegeocodeQuery(point,200, GeocodeSearch.GPS);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
        return view;
    }

    static class PointViewHolder{
        TextView tv_address;
        TextView tv_enter_time;
        TextView tv_leave_time;
    }

}
