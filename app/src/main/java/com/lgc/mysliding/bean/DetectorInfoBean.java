package com.lgc.mysliding.bean;

import java.util.List;

/**
 * 已过时
 */

public class DetectorInfoBean {

    private List<DeviceListBean> device_list;

    public List<DeviceListBean> getDevice_list() {
        return device_list;
    }

    public void setDevice_list(List<DeviceListBean> device_list) {
        this.device_list = device_list;
    }

    public static class DeviceListBean {
        /**
         * 探针信息
         * mac : 8cab8e54c884 mac地址
         * time : 1463095281  unix 时间戳
         * rssi : 80 信号强度
         * longitude : 116.099656 经度
         * latitude : 24.291004  纬度
         * 经纬度用来换算成相对应的地址
         */

        private String mac;
        private int time;
        private int rssi;
        private double longitude;
        private double latitude;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
