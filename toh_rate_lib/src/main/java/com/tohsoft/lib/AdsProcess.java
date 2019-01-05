package com.tohsoft.lib;

import java.util.Calendar;

import com.tohsoft.lib.listener.NotificationReceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.support.v4.app.NotificationCompat;


@SuppressLint("NewApi")
public class AdsProcess {
	public static String PREF_ADS_OBJECT = "PREF_ADS_OBJECT";
	public static String PREF_PUT_ADS_OBJECT = "PREF_PUT_ADS_OBJECT";
	public static String PREF_PUT_LIST_ADS_OBJECT = "PREF_PUT_LIST_ADS_OBJECT";
	static Context context;
	public static String DELAY_REPEED_VALUE = "DELAY_REPEED";
	public static String DELAY_REPEED_ADD = "DELAY_REPEED_ADD";
	public static String IS_REGISTER_ALARM = "IS_REGISTER_ALARM";

	public static int Notification_ADS_ID = 6546;
	public AdsProcess(Context context) {
		this.context = context;
	}

	public static void initTimer(Context context, int hour, int minute, int repeat, int delay){
		// Set the alarm to start at approximately 2:00 p.m.
		SharedPreferences mPrefs = context.getSharedPreferences(
				PREF_ADS_OBJECT, Context.MODE_PRIVATE);
		
		boolean isRegisted = mPrefs.getBoolean(AdsProcess.IS_REGISTER_ALARM, false);
		if(isRegisted){
			return;
		}
		Editor prefsEditor = mPrefs.edit();
		prefsEditor.putInt(DELAY_REPEED_VALUE, repeat);
		prefsEditor.putInt(DELAY_REPEED_ADD, delay);
		
		prefsEditor.putBoolean(IS_REGISTER_ALARM, true);
		
		prefsEditor.commit();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(context, NotificationReceiver.class);
		Intent intent = new Intent();
		intent.setAction("com.notification.show.CUSTOM_INTENT");
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

//		// With setInexactRepeating(), you have to use one of the AlarmManager interval
//		// constants--in this case, AlarmManager.INTERVAL_DAY.
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
		        AlarmManager.INTERVAL_DAY, alarmIntent);
//		alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//		        + (3 * 1000), alarmIntent);
	}

//	public static void customNotification(Context context) {
//		NotificationManager notificationmanager1 = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationmanager1.cancel(Notification_ADS_ID);
//
//		// Using RemoteViews to bind custom layouts into Notification
//		AppAdsObject appAdsObject = CoreService.getFirstObjectApps(context);
//		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
//				R.layout.layout_notification_ads);
//		String appName = appAdsObject.getTitle();
//		String appDes = appAdsObject.getDes();
//		final String appPkg = appAdsObject.getPkg();
//		byte[] appIcon = appAdsObject.getIconSize64();
//		Bitmap bitmap = BitmapFactory.decodeByteArray(appIcon, 0,
//				appIcon.length);
//
//		Intent intent = new Intent(Intent.ACTION_VIEW,
//				Uri.parse("market://details?id=" + appPkg));
//		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				context)
//		// Set Icon
//				.setSmallIcon(R.drawable.app_sphere_48)
//				// Set Ticker Message
//				.setTicker(appName)
//				// Dismiss Notification
//				.setAutoCancel(true)
//				// Set PendingIntent into Notification
//				.setContentIntent(pIntent)
//				// Set RemoteViews into Notification
//				.setContent(remoteViews);
//
//		// Locate and set the Image into customnotificationtext.xml ImageViews
//		remoteViews.setImageViewBitmap(R.id.img_app_icon, bitmap);
//		// remoteViews.setImageViewBitmap(R.id.imagenotiright,R.drawable.androidhappy);
//
//		// Locate and set the Text into customnotificationtext.xml TextViews
//		remoteViews.setTextViewText(R.id.tv_app_name, appName);
//		remoteViews.setTextViewText(R.id.tv_des, appDes);
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.US);
//		String time = sdf.format(new Date());
//		
//		remoteViews.setTextViewText(R.id.tv_time, time);
//		
//		// //this is the intent that is supposed to be called when the
//		// //button is clicked
//		Intent switchIntent = new Intent(
//				"com.notification.close.CUSTOM_INTENT");
//		PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context,
//				0, switchIntent, 0);
//
//		remoteViews.setOnClickPendingIntent(R.id.img_app_download,
//				pendingSwitchIntent);
//
//		// Create Notification Manager
//		NotificationManager notificationmanager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		// Build Notification with Notification Manager
//		notificationmanager.notify(Notification_ADS_ID, builder.build());
//
//	}

}
