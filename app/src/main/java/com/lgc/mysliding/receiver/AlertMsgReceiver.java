package com.lgc.mysliding.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lgc.mysliding.bean.AlertMsgBean;
import com.lgc.mysliding.service.AlertMsgService;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/2/24.
 * 信鸽推送消息广播接收者
 */
public class AlertMsgReceiver extends XGPushBaseReceiver{
    private static final String TAG="AlertMsgReceiver";
    private Intent intent = new Intent("com.lgc.mysliding.reciver.UPDATE_LISTVIEW");

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    //展示通知结果
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if (context==null || xgPushShowedResult==null){
            return;
        }
        AlertMsgBean alertMsgBean=new AlertMsgBean();
        alertMsgBean.setMsg_id(xgPushShowedResult.getMsgId());
        alertMsgBean.setTitle(xgPushShowedResult.getTitle());
        alertMsgBean.setContent(xgPushShowedResult.getContent());
        // notificationActionType==1为Activity，2为url，3为intent
        alertMsgBean.setMsgActionType(xgPushShowedResult.getNotificationActionType());
        // Activity,url,intent都可以通过getActivity()获得
        alertMsgBean.setActivity(xgPushShowedResult.getActivity());
        alertMsgBean.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        AlertMsgService.getMsgServiceInstance(context).saveToDatabase(alertMsgBean);
        context.sendBroadcast(intent);
        Log.d(TAG,"您有1条新消息, " + "通知被展示 ， " + xgPushShowedResult.toString());
    }
}
