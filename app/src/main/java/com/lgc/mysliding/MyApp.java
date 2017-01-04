package com.lgc.mysliding;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.amap.api.maps2d.model.MarkerOptions;
import com.lgc.mysliding.bean.DetectorInfoBean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class MyApp extends Application{

    private static final String TAG="MyApp";

    private List<DetectorInfoBean.DeviceListBean> deviceListBeen;
    private List<MarkerOptions> markerOptions;

    public List<MarkerOptions> getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(List<MarkerOptions> markerOptions) {
        this.markerOptions = markerOptions;
    }

    public List<DetectorInfoBean.DeviceListBean> getDeviceListBeen() {
        return deviceListBeen;
    }

    public void setDeviceListBeen(List<DetectorInfoBean.DeviceListBean> deviceListBeen) {
        this.deviceListBeen = deviceListBeen;
    }

    //获取应用的 key
    public String getSHA1(Context context) throws NoSuchAlgorithmException {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0XFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
