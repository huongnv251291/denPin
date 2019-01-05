package com.tohsoft.lib.listener;

import com.tohsoft.lib.AdsProcess;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverCloseNotification extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    manager.cancel(AdsProcess.Notification_ADS_ID);
	}
}