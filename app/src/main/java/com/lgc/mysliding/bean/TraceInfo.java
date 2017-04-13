package com.lgc.mysliding.bean;

import java.util.List;

/**
 * Created by lgc on 2017/1/11.
 * 已过时
 */
public class TraceInfo {
    private List<TraceBean> trace;

    public List<TraceBean> getTrace() {
        return trace;
    }

    public void setTrace(List<TraceBean> trace) {
        this.trace = trace;
    }

    public static class TraceBean {
        /**
         * longitude : 116.109695
         * latitude : 24.263339
         * enter_time : 1462198037
         * leave_time : 1462198037
         */

        private double longitude;
        private double latitude;
        private int enter_time;
        private int leave_time;

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
    }
}
