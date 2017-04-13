package com.lgc.mysliding.bean;

import java.util.List;

/**
 * 未使用
 */
public class TraceBeanList {

    private List<TraceBean> traceList;

    public List<TraceBean> getTraceList() {
        return traceList;
    }

    public void setTraceList(List<TraceBean> traceList) {
        this.traceList = traceList;
    }

    public static class TraceBean {
        /**
         * longitude : 116.109695 轨迹经度
         * latitude : 24.263339 轨迹纬度
         * entertime : 1462198037  进入时间
         * leavetime : 1462198037  离开时间
         */

        private double longitude;
        private double latitude;
        private long entertime;
        private long leavetime;

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

        public long getEntertime() {
            return entertime;
        }

        public void setEntertime(long entertime) {
            this.entertime = entertime;
        }

        public long getLeavetime() {
            return leavetime;
        }

        public void setLeavetime(long leavetime) {
            this.leavetime = leavetime;
        }
    }
}
