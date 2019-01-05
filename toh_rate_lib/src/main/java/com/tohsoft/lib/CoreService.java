package com.tohsoft.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.photo.best.pip.data.App;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

public class CoreService {

	public static String defaulPackage = "com.photoedit.photocollage";
	public static String newPackage = null;
	public static String PREF_ADS_OBJECT = "PREF_ADS_OBJECT";
	public static String PREF_PUT_ADS_OBJECT = "PREF_PUT_ADS_OBJECT";
	public static String PREF_PUT_LIST_ADS_OBJECT = "PREF_PUT_LIST_ADS_OBJECT";
	public static String PREF_PUT_OLDEST_PACKAGE = "PREF_PUT_OLDEST_PACKAGE";
	public static String PREF_PUT_CALLRECORDER_PACKAGE = "PREF_PUT_CALLRECORDER_PACKAGE";

	public static ArrayList<App> newlApps = null;
	public static ArrayList<App> newlAppsForOneAds = null;
	public static ArrayList<App> showlApps = new ArrayList<App>();

	public static String defaultCallRecorderPackage = "com.toh.callrecord";
	public static String callRecorderPackage = null;
	
	public static String getPackageName(Context context) {
		if (newPackage == null
				|| (newPackage != null && newPackage.equalsIgnoreCase(""))) {
			newPackage = defaulPackage;
			try {
				SharedPreferences mPrefs = context.getSharedPreferences(
						PREF_ADS_OBJECT, Context.MODE_PRIVATE);
				newPackage = mPrefs.getString(PREF_PUT_OLDEST_PACKAGE,
						defaulPackage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newPackage;
	}

	public static String getCallRecorderPackageName(Context context) {
		if (callRecorderPackage == null) {
			callRecorderPackage = defaultCallRecorderPackage;
			try {
				SharedPreferences mPrefs = context.getSharedPreferences(
						PREF_ADS_OBJECT, Context.MODE_PRIVATE);
				callRecorderPackage = mPrefs.getString(PREF_PUT_CALLRECORDER_PACKAGE,
						defaultCallRecorderPackage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return callRecorderPackage;
	}

	
	
	// public static void initPackageName(final Context context){
	// String pakage = context.getApplicationContext().getPackageName();
	// String url = "http://209.54.48.226/adt.php?type=1&package=" + pakage;
	// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	// .permitAll().build();
	// StrictMode.setThreadPolicy(policy);
	//
	// HttpClient client = new DefaultHttpClient();
	// HttpGet request = new HttpGet(url);
	//
	// HttpResponse response;
	// try {
	// response = client.execute(request);
	// newPackage = EntityUtils.toString(response.getEntity());
	// String pk = response.toString();
	// System.out.print("Response of GET request" + pk);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // Timer t = new Timer();
	// // t.schedule(new TimerTask() {
	// // @Override
	// // public void run() {
	// // getListAppInfo(context);
	// // }
	// // }, 0000);
	// }

	public static void initPackageName(Context context) {
		String pakage = context.getApplicationContext().getPackageName();
		String url = "http://adservice.tohsoft.com/adt.php?type=1&package="
				+ pakage;

		GetAdPackageTask runner = new GetAdPackageTask(context);
		// String sleepTime = time.getText().toString();
		runner.execute(url);
		/*
		 * StrictMode.ThreadPolicy policy = new
		 * StrictMode.ThreadPolicy.Builder() .permitAll().build();
		 * StrictMode.setThreadPolicy(policy);
		 * 
		 * HttpClient client = new DefaultHttpClient(); HttpGet request = new
		 * HttpGet(url);
		 * 
		 * HttpResponse response; try { response = client.execute(request);
		 * newPackage = EntityUtils.toString(response.getEntity()); String pk =
		 * response.toString(); Log.d("CoreService","Response of GET request" +
		 * pk); Log.d("CoreService","newPackage: " + newPackage); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}
	
	
	public static void initCallRecorderPackageName(Context context) {
		String pakage = context.getApplicationContext().getPackageName();
		String url = "http://adservice.tohsoft.com/adtcr.php?type=1&package="
				+ pakage;

		GetAdPackageTask runner = new GetAdPackageTask(context);
		runner.execute(url);
	}
	
	public final static String LAST_TIME_REQUEST_MORE2_KEY = "LAST_TIME_REQUEST_MORE2_KEY";
	public final static String LAST_RESPONSE_OF_REQUEST_MORE2_KEY = "LAST_RESPONSE_OF_REQUEST_MORE2_KEY";
	public final static long durationThreadhold = 6*60*60*1000; // ms
	
	/**
	 * ket noi lay list apps
	 * 
	 * @param context
	 * Hong add 25/05/2016
	 */
	
	public static void initPackageName2(Context context, LibActivityInterface libActivity) {
		// check neu mang newapps chua co phan tu ==> thuc hien request server
		// new co roi thi check so voi lan gan nhat neu >6 tieng thi thuc hien request server
		// de cap nhat
		
		
		String pakage = context.getApplicationContext().getPackageName();
		
		boolean isNeedSendReqToServer = true;
		SharedPreferences pref = context.getApplicationContext()
				.getSharedPreferences(
						pakage,
						Context.MODE_PRIVATE);
		
		if (newlApps==null || newlApps.size()==0){
			String lastResponse = pref.getString(LAST_RESPONSE_OF_REQUEST_MORE2_KEY, null);
			if (lastResponse!=null){
				newlApps = parseMoreApp(lastResponse);
			}
		}
		
		
		if (newlApps==null || newlApps.size()==0){
			
			isNeedSendReqToServer = true;
		}else{
			// check so voi lan request gan nhat >12 tieng chua
			//todo
			
			
			long lastTimeRequest = pref.getLong(LAST_TIME_REQUEST_MORE2_KEY, -1);
			if (lastTimeRequest<0){
				isNeedSendReqToServer = true;
			}else{
				long now = System.currentTimeMillis(); 
				if (now - lastTimeRequest>=durationThreadhold){
					isNeedSendReqToServer = true;
				}else{
					isNeedSendReqToServer = false;
				}
				
			}
			
			
		}
		
		if (isNeedSendReqToServer){
			
			// cap nhat sharepreference last time request
			long now = System.currentTimeMillis(); 
			SharedPreferences.Editor editor = pref.edit();
			editor.putLong(LAST_TIME_REQUEST_MORE2_KEY,
					now);
			editor.commit();
			
			
			String langCode = Locale.getDefault().getLanguage();
			String url = "http://adservice.tohsoft.com/more2.php?package=" + pakage
					+ "&hl=" + langCode;
	
			GetAdPackageTask2 runner = new GetAdPackageTask2(context, libActivity);
			// String sleepTime = time.getText().toString();
			runner.execute(url);
		}
		
	}
	
	
	/**
	 * ket noi lay list apps
	 * 
	 * @param context
	 * Hong add 25/05/2016
	 */
	public static void initPackageNameForOneAppAds(Context context, LibActivityInterface libActivity) {
		String pakage = context.getApplicationContext().getPackageName();
		String langCode = Locale.getDefault().getLanguage();
		String url = "http://adservice.tohsoft.com/moreoneapp.php?package=" + pakage
				+ "&hl=" + langCode;

		GetAdPackageTask2 runner = new GetAdPackageTask2(context, libActivity);
		// String sleepTime = time.getText().toString();
		runner.execute(url);
		
	}
	
	/**
	 * ket noi lay list apps
	 * 
	 * @param context
	 * Hong add 25/05/2016
	 */
	public static void submitCount(Context context) {
		String pakage = context.getApplicationContext().getPackageName();
		String langCode = Locale.getDefault().getLanguage();
		String url = "http://adservice.tohsoft.com/count1.php";

		GetAdPackageTaskCount runner = new GetAdPackageTaskCount(context);
		// String sleepTime = time.getText().toString();
		runner.execute(url);
		
	}
	
	/**
	 * ket noi lay list apps
	 * 
	 * @param context
	 * Hong add 25/05/2016
	 */
	public static void submitCount2(Context context) {
		String pakage = context.getApplicationContext().getPackageName();
		String langCode = Locale.getDefault().getLanguage();
		String url = "http://adservice.tohsoft.com/count2.php";

		GetAdPackageTaskCount runner = new GetAdPackageTaskCount(context);
		// String sleepTime = time.getText().toString();
		runner.execute(url);
		
	}

	public static class GetAdPackageTask extends
			AsyncTask<String, String, String> {
		private String resp;

		Context context;

		public GetAdPackageTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);

			HttpResponse response;
			String adPackage = null;
			try {
				response = client.execute(request);
				adPackage = EntityUtils.toString(response.getEntity());
				String pk = response.toString();
				Log.d("CoreService", "Response of GET request" + pk);
				Log.d("CoreService", "newPackage: " + newPackage);
				if (pk != null && !pk.equalsIgnoreCase("")) {
					SharedPreferences mPrefs = context.getSharedPreferences(
							PREF_ADS_OBJECT, Context.MODE_PRIVATE);
					Editor prefsEditor = mPrefs.edit();
					prefsEditor.putString(PREF_PUT_OLDEST_PACKAGE, pk);
					prefsEditor.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			newPackage = adPackage;
			return resp;
		}

		// protected void onProgressUpdate(Integer... progress) {
		// //setProgressPercent(progress[0]);
		// }
		//
		// protected void onPostExecute(String result) {
		// //showDialog("Downloaded " + result + " bytes");
		// }
	}

	
	/**
	 * Ket noi len service lay ve danh sach cac app bao gom [package, icon link,
	 * name tuong ung ngon ngu dang dung]
	 * 
	 * @param context
	 *            Hong add 25/05/2016
	 */
	public static class GetAdPackageTask2 extends
			AsyncTask<String, String, String> {
		private String resp;

		Context context;
		LibActivityInterface libActivity;

		public GetAdPackageTask2(Context context, LibActivityInterface libActivity) {
			this.context = context;
			this.libActivity = libActivity;
		}

		@Override
		protected String doInBackground(String... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);

			HttpResponse response;
//			String adPackage = null;
			ArrayList<App> lApps = null;
			try {
				response = client.execute(request);
//				adPackage = EntityUtils.toString(response.getEntity());
				String data = EntityUtils.toString(response.getEntity());
				
				lApps = parseMoreApp(data);
				
				// save last response
				String pakage = context.getApplicationContext().getPackageName();
				SharedPreferences pref = context.getApplicationContext()
						.getSharedPreferences(
								pakage,
								Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(LAST_RESPONSE_OF_REQUEST_MORE2_KEY,
						data);
				editor.commit();
				//return lApps;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (lApps!=null && lApps.size()>0){
//				if (newlApps!=null){
//					newlApps.clear();
//				}
				//
				if (newlApps==null||newlApps.size()==0){
					newlApps = lApps;
				}else{
					if (lApps.size()!= newlApps.size()){
						newlApps = lApps;
					}else{
					// check chi tiet tung phan tu 2 mang
						boolean isEqual = true;
						for (int i = 0; i<lApps.size(); i++){
							App a = lApps.get(i);
							App b = newlApps.get(i);
							if (!App.isEqual(a, b)){
								isEqual = false;
								break;
							}
						}
						
						if (!isEqual){
							newlApps = lApps;
						}
					}
					
				}
			}
			return resp;
		}

		
		
		
		@Override
	    protected void onPostExecute(String result) {
			libActivity.doUpdateUI();
	    }
		
		
		// protected void onProgressUpdate(Integer... progress) {
		// //setProgressPercent(progress[0]);
		// }
		//
		// protected void onPostExecute(String result) {
		// //showDialog("Downloaded " + result + " bytes");
		// }
	}

	
	/**
	 * Ket noi len service lay ve danh sach cac app bao gom [package, icon link,
	 * name tuong ung ngon ngu dang dung]
	 * 
	 * @param context
	 *            Hong add 25/05/2016
	 */
	public static class GetAdPackageTask3 extends
			AsyncTask<String, String, String> {
		private String resp;

		Context context;

		public GetAdPackageTask3(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);

			HttpResponse response;
//			String adPackage = null;
			ArrayList<App> lApps = null;
			try {
				response = client.execute(request);
//				adPackage = EntityUtils.toString(response.getEntity());
				String data = EntityUtils.toString(response.getEntity());
				
				lApps = parseMoreApp(data);
				//return lApps;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (lApps!=null && lApps.size()>0){
				if (newlAppsForOneAds!=null){
					newlAppsForOneAds.clear();
				}
				newlAppsForOneAds = lApps;
			}
			return resp;
		}

		
		
		
		
		
		
		// protected void onProgressUpdate(Integer... progress) {
		// //setProgressPercent(progress[0]);
		// }
		//
		// protected void onPostExecute(String result) {
		// //showDialog("Downloaded " + result + " bytes");
		// }
	}
	
	
	/**
	 * Ket noi len service ko xu ly response
	 * 
	 * @param context
	 *            Hong add 25/05/2016
	 */
	public static class GetAdPackageTaskCount extends
			AsyncTask<String, String, String> {
		private String resp;

		Context context;

		public GetAdPackageTaskCount(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);

			HttpResponse response;
//			String adPackage = null;
			ArrayList<App> lApps = null;
			try {
				response = client.execute(request);
//				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return resp;
		}

		
		
		
		
		
		
		// protected void onProgressUpdate(Integer... progress) {
		// //setProgressPercent(progress[0]);
		// }
		//
		// protected void onPostExecute(String result) {
		// //showDialog("Downloaded " + result + " bytes");
		// }
	}
	public static  ArrayList<App> parseMoreApp(String data){
		ArrayList<App> lApps = new ArrayList<App>();
		try {
			JSONArray jArray = new JSONArray(data);
			for(int i=0; i<jArray.length();i++){
				JSONArray jiArray = jArray.getJSONArray(i);
				String name = new String(jiArray.getString(2).getBytes("ISO-8859-1"), "UTF-8");
				lApps.add(new App(jiArray.getString(0), jiArray.getString(1), name));
			}
			return lApps;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	// public static void getListAppInfo(Context context) {
	// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	// .permitAll().build();
	// StrictMode.setThreadPolicy(policy);
	// String pakage = context.getApplicationContext().getPackageName();
	// String url = "http://209.54.48.226/adcross.php?type=1&package="
	// + pakage;
	// String url = "http://adservice.tohsoft.com/adcross.php?type=1&package="
	// + pakage;
	// HttpClient client = new DefaultHttpClient();
	// HttpGet request = new HttpGet(url);
	// HttpResponse response;
	// try {
	// response = client.execute(request);
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// response.getEntity().getContent(), "UTF-8"));
	// String json = reader.readLine();
	// JSONArray jsonArray = new JSONArray(json);
	// List<AppAdsObject> listAppAdsObject = new ArrayList<AppAdsObject>();
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject jsonObject = jsonArray.getJSONObject(i);
	// String title = jsonObject.optString("title").toString();
	// String des = jsonObject.optString("des").toString();
	// String pkg = jsonObject.optString("pkg").toString();
	// String icon = jsonObject.optString("icon").toString();
	// byte[] data = Base64.decode(icon.getBytes(), Base64.DEFAULT);
	// AppAdsObject appAdsObject = new AppAdsObject(title, des, pkg,
	// data, null, null, 0, null, null, null);
	// listAppAdsObject.add(appAdsObject);
	// }
	// SharedPreferences mPrefs = context.getSharedPreferences(
	// PREF_ADS_OBJECT, Context.MODE_PRIVATE);
	// Editor prefsEditor = mPrefs.edit();
	// Gson gson = new Gson();
	// String gsonObject = gson.toJson(listAppAdsObject);
	// prefsEditor.putString(PREF_PUT_LIST_ADS_OBJECT, gsonObject);
	// prefsEditor.commit();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static List<AppAdsObject> getListObjectApps(Context context) {
		List<AppAdsObject> listAppAdsObject = new ArrayList<AppAdsObject>();
		SharedPreferences mPrefs = context.getSharedPreferences(
				PREF_ADS_OBJECT, Context.MODE_PRIVATE);
		Gson gson = new Gson();

		String gsonObject = mPrefs.getString(PREF_PUT_LIST_ADS_OBJECT, null);
		if (gsonObject != null) {
			Type type = new TypeToken<List<AppAdsObject>>() {
			}.getType();
			listAppAdsObject = gson.fromJson(gsonObject, type);
		}
		return listAppAdsObject;
	}

	public static AppAdsObject getFirstObjectApps(Context context) {
		List<AppAdsObject> listAppAdsObject = getListObjectApps(context);
		AppAdsObject appAdsObject = null;
		if (listAppAdsObject != null) {
			for (int i = 0; i < listAppAdsObject.size(); i++) {
				appAdsObject = listAppAdsObject.get(i);
				boolean isInstalled = isPackageInstalled(appAdsObject.getPkg(),
						context);
				if (!isInstalled) {
					break;
				}
			}
		}
		return appAdsObject;
	}

	public static void getAppInfo(Context context) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		String pakage = context.getApplicationContext().getPackageName();
		// String url = "http://209.54.48.226/adcross.php?type=2&package="
		// + pakage;
		String url = "http://adservice.tohsoft.com/adcross.php?type=2&package="
				+ pakage;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONArray jsonArray = new JSONArray(json);
			AppAdsObject appAdsObject = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String title = jsonObject.optString("title").toString();
				String des = jsonObject.optString("des").toString();
				String pkg = jsonObject.optString("pkg").toString();
				String icon = jsonObject.optString("icon").toString();
				byte[] data = Base64.decode(icon.getBytes(), Base64.DEFAULT);
				appAdsObject = new AppAdsObject(title, des, pkg, data, null,
						null, 0, null, null, null);
			}
			if (appAdsObject != null) {
				SharedPreferences mPrefs = context.getSharedPreferences(
						PREF_ADS_OBJECT, Context.MODE_PRIVATE);
				Editor prefsEditor = mPrefs.edit();
				Gson gson = new Gson();
				String gsonObject = gson.toJson(appAdsObject);
				prefsEditor.putString(PREF_PUT_ADS_OBJECT, gsonObject);
				prefsEditor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ket noi len service lay ve danh sach cac app bao gom [package, icon link,
	 * name tuong ung ngon ngu dang dung]
	 * 
	 * @param context
	 *            Hong add 25/05/2016
	 */
	public static void getAppInfoList(Context context) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		String pakage = context.getApplicationContext().getPackageName();
		// String url = "http://209.54.48.226/adcross.php?type=2&package="
		// + pakage;
		String langCode = Locale.getDefault().getLanguage();
		String url = "http://adservice.tohsoft.com/more2.php?package=" + pakage
				+ "&hl=" + langCode;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONArray jsonArray = new JSONArray(json);
			AppAdsObject appAdsObject = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String title = jsonObject.optString("title").toString();
				String des = jsonObject.optString("des").toString();
				String pkg = jsonObject.optString("pkg").toString();
				String icon = jsonObject.optString("icon").toString();
				byte[] data = Base64.decode(icon.getBytes(), Base64.DEFAULT);
				appAdsObject = new AppAdsObject(title, des, pkg, data, null,
						null, 0, null, null, null);
			}
			if (appAdsObject != null) {
				SharedPreferences mPrefs = context.getSharedPreferences(
						PREF_ADS_OBJECT, Context.MODE_PRIVATE);
				Editor prefsEditor = mPrefs.edit();
				Gson gson = new Gson();
				String gsonObject = gson.toJson(appAdsObject);
				prefsEditor.putString(PREF_PUT_ADS_OBJECT, gsonObject);
				prefsEditor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static AppAdsObject getObjectApps(Context context) {
		AppAdsObject appAdsObject = null;
		SharedPreferences mPrefs = context.getSharedPreferences(
				PREF_ADS_OBJECT, Context.MODE_PRIVATE);
		Gson gson = new Gson();

		String gsonObject = mPrefs.getString(PREF_PUT_ADS_OBJECT, null);
		if (gsonObject != null) {
			appAdsObject = gson.fromJson(gsonObject, AppAdsObject.class);
		}
		return appAdsObject;
	}

	public static boolean isPackageInstalled(String packagename, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	

	public static class GetAdCallRecorderPackageTask extends
			AsyncTask<String, String, String> {
		private String resp;

		Context context;

		public GetAdCallRecorderPackageTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);

			HttpResponse response;
			String adPackage = null;
			try {
				response = client.execute(request);
				adPackage = EntityUtils.toString(response.getEntity());
				String pk = response.toString();
				Log.d("CoreService", "Response of GET request" + pk);
				Log.d("CoreService", "Call recorder Package: " + adPackage);
				if (pk != null && !pk.equalsIgnoreCase("")) {
					SharedPreferences mPrefs = context.getSharedPreferences(
							PREF_ADS_OBJECT, Context.MODE_PRIVATE);
					Editor prefsEditor = mPrefs.edit();
					prefsEditor.putString(PREF_PUT_CALLRECORDER_PACKAGE, pk);
					prefsEditor.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			callRecorderPackage = adPackage;
			return resp;
		}
	}
}
