package com.lgc.mysliding.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 * 围栏列表的项
 */
public class FenceBean {

    /**
     * ret_code : 0
     * ret_msg :
     * fence_list : 围栏信息表
     * [{
     * "id":"5796f15d46018a6389d2e478", 用户id
     * "username":"cosine",             用户名
     * "name":"电子围栏",                围栏名称
     * "longitude":113.33885982942675,  围栏中心经度
     * "latitude":23.187941436207694,   围栏中心纬度
     * "radius":150,                    围栏半径
     * "phone_list":["bc7574d26432"],   监控号码表
     * "abnormal_phone_list":[],
     * "type":2,                         报警类型
     * "last_alert_time":1485221134,
     * "monitoring_objects":[{"value":"bc7574d26432","longitude":113.343056,"latitude":23.18588,"alert":false}]},{"id":"57f3e0e590b10432d1e36469","username":"cosine","name":"监控任务梅州围栏","longitude":116.07265496082991,"latitude":24.303177447239477,"radius":1000,"phone_list":["1C6758D9E0CD","12312","1c6758d9e0cd"],"abnormal_phone_list":["1C6758D9E0CD","1c6758d9e0cd"],"type":1,"last_alert_time":1487330627,"monitoring_objects":[{"value":"1C6758D9E0CD","longitude":114.40789,"latitude":23.106134,"alert":true},{"value":"12312","longitude":0,"latitude":0,"alert":false},{"value":"1c6758d9e0cd","longitude":114.40789,"latitude":23.106134,"alert":true}]},{"id":"580811a190b104640d7e9d2b","username":"cosine","name":"围栏","longitude":114.40736456773487,"latitude":23.110603364002287,"radius":300,"phone_list":["1C6758D9E0CD","1c9e464f0295"],"abnormal_phone_list":[],"type":2,"last_alert_time":1487324372,"monitoring_objects":[{"value":"1C6758D9E0CD","longitude":114.40789,"latitude":23.106134,"alert":false},{"value":"1c9e464f0295","longitude":114.412796,"latitude":23.114865,"alert":false}]},{"id":"5812c33690b104244d836441","username":"cosine","name":"监控任务惠州出围栏报警","longitude":114.41009612740513,"latitude":23.112767873549558,"radius":400,"phone_list":["1C6758D9E0CD"],"abnormal_phone_list":["1C6758D9E0CD"],"type":1,"last_alert_time":1487330356,"monitoring_objects":[{"value":"1C6758D9E0CD","longitude":114.40789,"latitude":23.106134,"alert":true}]},{"id":"58a264fc90b10437ee6921e5","username":"cosine","name":"test_fence","longitude":0,"latitude":0,"radius":1000,"phone_list":["15870002121"],"abnormal_phone_list":[],"type":1,"last_alert_time":0,"monitoring_objects":[{"value":"15870002121","longitude":0,"latitude":0,"alert":false}]},{"id":"58a2652046018a4f5389e307","username":"cosine","name":"test_fence","longitude":0,"latitude":0,"radius":1000,"phone_list":["15870002121"],"abnormal_phone_list":[],"type":1,"last_alert_time":0,"monitoring_objects":[{"value":"15870002121","longitude":0,"latitude":0,"alert":false}]}]
     */

    private int ret_code;
    private String ret_msg;
    private List<FenceListBean> fence_list; //围栏信息表

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public List<FenceListBean> getFence_list() {
        return fence_list;
    }

    public void setFence_list(List<FenceListBean> fence_list) {
        this.fence_list = fence_list;
    }

    public static class FenceListBean {
        /**
         * id : 5796f15d46018a6389d2e478
         * username : cosine
         * name : 电子围栏名称
         * longitude : 113.33885982942675
         * latitude : 23.187941436207694
         * radius : 150
         * phone_list : ["bc7574d26432"]
         * abnormal_phone_list : []
         * type : 2
         * last_alert_time : 1485221134
         * monitoring_objects : [{"value":"bc7574d26432","longitude":113.343056,"latitude":23.18588,"alert":false}]
         */

        private String id;
        private String username;
        private String name;
        private double longitude;
        private double latitude;
        private int radius;
        private int type;
        private int last_alert_time;
        private List<String> phone_list;
        private List<String> abnormal_phone_list;// TODO: 2017/2/17
        private List<MonitoringObjectsBean> monitoring_objects;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getLast_alert_time() {
            return last_alert_time;
        }

        public void setLast_alert_time(int last_alert_time) {
            this.last_alert_time = last_alert_time;
        }

        public List<String> getPhone_list() {
            return phone_list;
        }

        public void setPhone_list(List<String> phone_list) {
            this.phone_list = phone_list;
        }

        public List<String> getAbnormal_phone_list() {
            return abnormal_phone_list;
        }

        public void setAbnormal_phone_list(List<String> abnormal_phone_list) {
            this.abnormal_phone_list = abnormal_phone_list;
        }

        public List<MonitoringObjectsBean> getMonitoring_objects() {
            return monitoring_objects;
        }

        public void setMonitoring_objects(List<MonitoringObjectsBean> monitoring_objects) {
            this.monitoring_objects = monitoring_objects;
        }

        public static class MonitoringObjectsBean {
            /**
             * value : bc7574d26432
             * longitude : 113.343056
             * latitude : 23.18588
             * alert : false
             */

            private String value;
            private double longitude;
            private double latitude;
            private boolean alert;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
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

            public boolean isAlert() {
                return alert;
            }

            public void setAlert(boolean alert) {
                this.alert = alert;
            }
        }
    }
}
