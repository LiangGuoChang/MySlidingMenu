package com.lgc.mysliding.bean;

/**
 * Created by Administrator on 2017/2/21.
 * 创建新围栏，删除围栏，更改围栏，返回的json数据项
 */
public class FenceResultBean {
    /**
     * ret_code : 0 返回码-0-成功
     * ret_msg : 返回的信息
     * id : 58abfe4490b10437ee6921f0 返回操作的id
     */

    private int ret_code;
    private String ret_msg;
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
