package com.orsac.android.pccfwildlife.Services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orsac.android.pccfwildlife.Activities.SelectionPage;

public class NotificationCancelReceiver extends BroadcastReceiver {

    public String type="";

    @Override
    public void onReceive(Context context, Intent intent) {

        type = intent.getStringExtra("cancel");

        switch (type){
            case "cancel":
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(SelectionPage.notificationId);
                mNotificationManager.cancelAll();
                break;

        }
    }
}