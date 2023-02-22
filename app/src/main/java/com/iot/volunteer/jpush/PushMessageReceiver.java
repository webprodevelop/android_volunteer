package com.iot.volunteer.jpush;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.volunteer.App;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.activity.ActivityLogin;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.activity.ActivityNotification;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.helper.RoomActivity;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.util.AppConst;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG, "[onMessage] " + customMessage);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("msg", customMessage.message);
        context.sendBroadcast(intent);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageOpened] " + message);

        Bundle bundle = new Bundle();
        bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
        bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
        bundle.putString(JPushInterface.EXTRA_EXTRA, message.notificationExtras);
        processMessage(context, bundle, true);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageArrived] " + message);

        Bundle bundle = new Bundle();
        bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
        bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
        bundle.putString(JPushInterface.EXTRA_EXTRA, message.notificationExtras);
        processMessage(context, bundle, false);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageDismiss] " + message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG, "[onRegister] " + registrationId);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("rid", registrationId);
        context.sendBroadcast(intent);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG, "[onConnected] " + isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG, "[onCommandResult] " + cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:" + isOn + ",source:" + source);
    }

    private void processMessage(Context context, Bundle bundle, boolean isOpened) {
        String sTitle = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String sAlert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String sExtras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        try {
            JSONObject extrasJson = new JSONObject(sExtras);
            String strIotData = extrasJson.optString("iot_data");
            processNotification(context, strIotData, sTitle, sAlert, isOpened);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void processNotification(Context context, String jPushData, String sTitle, String sAlert, boolean isOpened) {
        JSONObject iotDataObject = null;
        try {
            iotDataObject = new JSONObject(jPushData);

            boolean isNew = true;

            String receiver = iotDataObject.optString("receiver_phone", "");
            if (!receiver.isEmpty() && !receiver.equals(Prefs.Instance().getUserPhone())){
                return;
            }

            int notificationId = iotDataObject.optInt("db_id");
            ItemNotification itemNotification;
            IOTDBHelper iotdbHelper = new IOTDBHelper(context);
            if (iotDataObject.optString("type").equals("task")) {
                itemNotification = iotdbHelper.findNotificationEntry(notificationId);
            }
            else {
                itemNotification = Util.findNotificationEntry(notificationId);
            }
            if (itemNotification != null) {
                isNew = false;
            } else {
                itemNotification = new ItemNotification(
                        notificationId,
                        sTitle,
                        sAlert,
                        iotDataObject.optString("task_id"),
                        iotDataObject.optLong("time"),
                        iotDataObject.optString("type"),
                        iotDataObject.optString("task_status"),
                        iotDataObject.optString("notice_type"));
            }

            String alarmType = iotDataObject.getString("type");
            if (alarmType != null && alarmType.equals("task")) {
                String strTaskStatus = iotDataObject.getString("task_status");
                if (strTaskStatus != null && !strTaskStatus.isEmpty()) {
                    iotdbHelper.addNotificationEntry(itemNotification);
                    if (isOpened) {
                        Log.e(TAG,"==========" + App.Instance().getCurrentActivity());
                        if (App.Instance().getCurrentActivity() == null) {
                            Intent intentLogin = new Intent(context, ActivityLogin.class);
                            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intentLogin);
                        }
                        else {
                            Intent intentNew = new Intent(context, App.Instance().getCurrentActivity().getClass());
                            intentNew.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            App.Instance().getCurrentActivity().startActivity(intentNew);
                        }
                    }
                    else {
                        Intent intent = new Intent(AppConst.ACTION_PUSH_RECEIVED);
                        intent.putExtra("notification_data", itemNotification);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
//                    if (ActivityMain.IS_FOREGROUND) {
//                        Intent intent = new Intent(AppConst.ACTION_PUSH_RECEIVED);
//                        intent.putExtra("notification_data", itemNotification);
//                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                    } else if (ActivityNotification.IS_FOREGROUND) {
//                        Intent intent = new Intent(AppConst.ACTION_PUSH_RECEIVED);
//                        intent.putExtra("notification_data", itemNotification);
//                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                    } else {
//                        Intent intentLogin = new Intent(context, ActivityLogin.class);
////                        intentLogin.putExtras(bundle);
//                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(intentLogin);
//                    }
//					}
                }
            } else if (isNew && alarmType != null && alarmType.equals("chat")) {
                RoomActivity.setChatId(iotDataObject.getString("roomId"));
                RoomActivity.setChatPass(iotDataObject.getString("password"));
                Intent intent = new Intent(context, RoomActivity.class);
//                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (isNew && alarmType!= null && alarmType.equals("pay")) {
                String sPayStatus = iotDataObject.getString("pay_status");
                Intent intent = new Intent(AppConst.ACTION_PAY_RECEIVED);
                intent.putExtra("pay_status", sPayStatus);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else if (isNew && alarmType!= null && alarmType.equals("notice")) {
                iotdbHelper.addNotificationEntry(itemNotification);
                Intent intent = new Intent(AppConst.ACTION_NOTICE_RECEIVED);
                intent.putExtra("notice_status", (Serializable) itemNotification);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
