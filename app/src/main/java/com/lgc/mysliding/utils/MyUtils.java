package com.lgc.mysliding.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.lgc.mysliding.MyApp;

import java.io.IOException;
import java.util.List;

/**
 *工具类集合
 */
public class MyUtils {
    private static Context context= MyApp.getMyApp().getBaseContext();

    public MyUtils(Context mContext){
        this.context=mContext;
    }

     public static class GetAddressTask extends AsyncTask<Double,Void,String>{
        private static final String TAG="GetAddressTask";
        private List<String> listAddress;
        private Double[] latlngs;

        public GetAddressTask(List<String> list,Double[] latlng){
            this.listAddress=list;
            this.latlngs=latlng;
        }

        @Override
        protected String doInBackground(Double... doubles) {
            double lat=doubles[0];
            double lon=doubles[1];
            //换算经纬度为地址
            Geocoder geocoder=new Geocoder(context);
            String strAdd = null;
            try {
                List<Address> addressList=geocoder.getFromLocation(lat,lon,1);
                if(addressList.size()>0){
                    Address address=addressList.get(0);
                    strAdd =address.getAddressLine(0).substring(3);
                    Log.d(TAG,"strAdd--"+strAdd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strAdd;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listAddress.add(s);
            if (listAddress.size()>0){
                MyApp.getMyApp().setAddrList(listAddress);
                Log.d(TAG,"listAddress--"+listAddress.size());
            }
        }
    }
}
