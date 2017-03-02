package com.lgc.mysliding.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lgc.mysliding.bean.AlertMsgBean;
import com.lgc.mysliding.database.AlertMsgOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 * 信鸽消息接收服务
 */
public class AlertMsgService {
    private final static String TAG="AlertMsgService";
    private AlertMsgOpenHelper alertMsgOpenHelper;
    private static AlertMsgService instance=null;

    public AlertMsgService(Context context){
        this.alertMsgOpenHelper=new AlertMsgOpenHelper(context);
    }

    //获取AlertMsgService实例
    public synchronized static AlertMsgService getMsgServiceInstance(Context context){
        if (instance == null){
            instance=new AlertMsgService(context);
        }
        return instance;
    }

    //保存报警信息到数据库
    public long saveToDatabase(AlertMsgBean alertMsgBean){
        SQLiteDatabase db=alertMsgOpenHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("msg_id",alertMsgBean.getMsg_id());
        values.put("title",alertMsgBean.getTitle());
        values.put("activity",alertMsgBean.getActivity());
        values.put("msgActionType",alertMsgBean.getMsgActionType());
        values.put("content",alertMsgBean.getContent());
        values.put("update_time",alertMsgBean.getUpdate_time());
        long insertId=db.insert("alertmessage",null,values);
        Log.d(TAG, "insertId: "+insertId);
        return insertId;
    }

    //获取数据库中的消息总数(行数)
    public int getMsgCount(){
        SQLiteDatabase db=alertMsgOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from alertmessage",null);
        try{
            cursor.moveToFirst();

            Log.d(TAG, "getMsgCount: "+cursor.getInt(0));
            return cursor.getInt(0);
//            Log.d(TAG, "getMsgCount: "+cursor.getCount());
//            return cursor.getCount();
        }finally {
            cursor.close();
        }
    }

    //删除指定msg_id的报警信息
    public int deletOne(String msg_id){
        SQLiteDatabase db=alertMsgOpenHelper.getWritableDatabase();
        int delLine=db.delete("alertmessage","msg_id=?",new String[]{msg_id});
        Log.d(TAG, "deletOne-"+delLine);
        return delLine;
    }

    //从数据库获取可以显示到报警信息
    public List<AlertMsgBean> getScrollData(/*int currentPage,int lineSize,String msg_id*/){
//        String firstResult=String.valueOf((currentPage-1)*lineSize);
        SQLiteDatabase db=alertMsgOpenHelper.getReadableDatabase();
        Cursor cursor=null;
        try{
//            if (msg_id == null || "".equals(msg_id)){
                cursor = db
                        .query("alertmessage",
                                new String[] { "id,msg_id,title,content,activity,msgActionType,update_time" },
                                null, null, null, null, "update_time DESC");
//                                firstResult + "," + lineSize);
//            }else {
                /*cursor = db
                        .query("alertmessage",
                                new String[] { "id,msg_id,title,content,activity,msgActionType,update_time" },
                                "msg_id like ?", new String[] { msg_id + "%" },
                                null, null, "update_time DESC");
//                                firstResult + "," + lineSize);*/
//            }
            List<AlertMsgBean> alertMsgBeanList=new ArrayList<AlertMsgBean>();
            while (cursor.moveToNext()){
                alertMsgBeanList.add(new AlertMsgBean(cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getLong(cursor.getColumnIndex("msg_id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getString(cursor.getColumnIndex("activity")),
                        cursor.getInt(cursor.getColumnIndex("msgActionType")),
                        cursor.getString(cursor.getColumnIndex("update_time"))));
            }
            Log.d(TAG, "getScrollData-"+cursor.getCount());
            return alertMsgBeanList;
        }finally {
            cursor.close();
        }
    }

}
