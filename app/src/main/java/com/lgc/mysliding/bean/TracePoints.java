package com.lgc.mysliding.bean;

import java.util.List;

/**
 * 轨迹点
 */
public class TracePoints {

    /**
     * ap_mac : 00193b047ed8
     * longitude : 115.765341
     * latitude : 23.91668
     * enter_time : 1484166605
     * leave_time : 1484166605
     * org_code : 0
     */

    private List<TraceBean> trace;

    public List<TraceBean> getTrace() {
        return trace;
    }

    public void setTrace(List<TraceBean> trace) {
        this.trace = trace;
    }

    public static class TraceBean {
        private String ap_mac;
        private double longitude;
        private double latitude;
        private int enter_time;
        private int leave_time;
        private String org_code;

        public String getAp_mac() {
            return ap_mac;
        }

        public void setAp_mac(String ap_mac) {
            this.ap_mac = ap_mac;
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

        public int getEnter_time() {
            return enter_time;
        }

        public void setEnter_time(int enter_time) {
            this.enter_time = enter_time;
        }

        public int getLeave_time() {
            return leave_time;
        }

        public void setLeave_time(int leave_time) {
            this.leave_time = leave_time;
        }

        public String getOrg_code() {
            return org_code;
        }

        public void setOrg_code(String org_code) {
            this.org_code = org_code;
        }
    }
}
