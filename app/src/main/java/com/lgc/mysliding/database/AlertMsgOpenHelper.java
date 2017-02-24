package com.lgc.mysliding.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/2/24.
 * 报警信息本地数据库
 */
public class AlertMsgOpenHelper extends SQLiteOpenHelper{

    public AlertMsgOpenHelper(Context context) {
        super(context, "XGAlertMsg.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE alertmessage (id integer primary key autoincrement,msg_id varchar(64),title varchar(128),activity varchar(256),msgActionType varchar(512),content text,update_time varchar(16))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
