package com.lgc.mysliding.bean;

import java.util.List;

/**
 * Created by lgc on 2017/4/11.
 * 搜索轨迹的mac及相关信息
 */
public class TraceBean {

    /**
     * num : 17626
     * feature_list :
     * [{"mac":"8c99e60c9f6a","phone":"13600000145","time":1468506434,"org_code":"555400905","trace_num":0},{"mac":"f823b2b35b8f","phone":"13600001524","time":1474150991,"org_code":"555400905","trace_num":0},{"mac":"581f28ebcc5f","phone":"13600002244","time":1485868861,"org_code":"779852855","trace_num":0},{"mac":"dcd916002c81","phone":"13600003008","time":1478393812,"org_code":"555400905","trace_num":13},{"mac":"88539540f40e","phone":"13600004576","time":1491299140,"org_code":"723005104","trace_num":2774},{"mac":"bc6c21115c95","phone":"13600004722","time":1471679966,"org_code":"555400905","trace_num":0},{"mac":"a0d795145dad","phone":"13600004962","time":1489936308,"org_code":"779852855","trace_num":0},{"mac":"549f13cd04d0","phone":"13600007417","time":1481307478,"org_code":"555400905","trace_num":380},{"mac":"d4f46f548ada","phone":"13600008233","time":1478215243,"org_code":"555400905","trace_num":9},{"mac":"7014a6c00516","phone":"13600009147","time":1476061234,"org_code":"555400905","trace_num":29},{"mac":"8455a5d2f19c","phone":"13600011059","time":1486777853,"org_code":"555400905","trace_num":16},{"mac":"e440e239fc03","phone":"13600012743","time":1469501272,"org_code":"555400905","trace_num":1},{"mac":"fce998eee1bc","phone":"13600019018","time":1485865009,"org_code":"555400905","trace_num":0},{"mac":"bc7574d26a2b","phone":"13600022183","time":1475000936,"org_code":"555400905","trace_num":31},{"mac":"a8667fe2ba2f","phone":"13600023107","time":1487374972,"org_code":"779852855","trace_num":0},{"mac":"2082c0372adf","phone":"13600024318","time":1486248970,"org_code":"555400905","trace_num":0},{"mac":"dc37148b3f94","phone":"13600029799","time":1480167508,"org_code":"555400905","trace_num":51},{"mac":"7c01914af013","phone":"13600030199","time":1475295307,"org_code":"555400905","trace_num":0},{"mac":"a47174328578","phone":"13600036322","time":1475164627,"org_code":"555400905","trace_num":2},{"mac":"f0dbe2a86b70","phone":"13600036370","time":1471238633,"org_code":"555400905","trace_num":1}]
     */

    private int num;
    /**
     * mac : 8c99e60c9f6a
     * phone : 13600000145
     * time : 1468506434
     * org_code : 555400905
     * trace_num : 0
     */

    private List<FeatureListBean> feature_list;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<FeatureListBean> getFeature_list() {
        return feature_list;
    }

    public void setFeature_list(List<FeatureListBean> feature_list) {
        this.feature_list = feature_list;
    }

    public static class FeatureListBean {
        private String mac;
        private String phone;
        private int time;
        private String org_code;
        private int trace_num;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getOrg_code() {
            return org_code;
        }

        public void setOrg_code(String org_code) {
            this.org_code = org_code;
        }

        public int getTrace_num() {
            return trace_num;
        }

        public void setTrace_num(int trace_num) {
            this.trace_num = trace_num;
        }
    }
}
