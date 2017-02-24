package com.lgc.mysliding.bean;

/**
 * Created by Administrator on 2017/2/23.
 * 报警信息单元
 */
public class AlertMsgBean {
    private Integer id;
    private long msg_id;
    private String title;
    private String content;
    private String activity;
    private int msgActionType;
    private String update_time;

    public AlertMsgBean(){
    }

    public AlertMsgBean(Integer id,long msg_id,String title,String content,
                        String activity,int type,String update_time){
        super();
        this.id=id;
        this.msg_id=msg_id;
        this.title=title;
        this.content=content;
        this.activity=activity;
        this.msgActionType=type;
        this.update_time=update_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getMsgActionType() {
        return msgActionType;
    }

    public void setMsgActionType(int msgActionType) {
        this.msgActionType = msgActionType;
    }

}
