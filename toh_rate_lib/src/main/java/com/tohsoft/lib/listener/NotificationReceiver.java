package com.tohsoft.lib.listener;

//import com.tohsoft.lib.analytics.GoogleAnalyticsApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
//		try {
//			SharedPreferences mPrefs = context.getSharedPreferences(
//					AdsProcess.PREF_ADS_OBJECT, Context.MODE_PRIVATE);
//			int delay = mPrefs.getInt(AdsProcess.DELAY_REPEED_VALUE, 1);
//			int delayAdd = mPrefs.getInt(AdsProcess.DELAY_REPEED_ADD, 0);
//			Time today = new Time(Time.getCurrentTimezone());
//			if((delay != 0) && (((today.monthDay + delayAdd)%delay) == 0)){
//				try {
//					AppAdsObject appAdsObject = CoreService.getObjectApps(context);
//					String appPkg = appAdsObject.getPkg();
//					if(!CoreService.isPackageInstalled(appPkg, context)){
//						AdsProcess.customNotification(context);
//					}
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				
////				try {
////					String packageName = context.getPackageName();
////					GoogleAnalyticsApplication.getInstance().trackScreenView(packageName);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}