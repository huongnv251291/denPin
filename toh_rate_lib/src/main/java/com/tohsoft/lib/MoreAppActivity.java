package com.tohsoft.lib;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


//import com.photo.best.pip.adapter.AppAdapter;
//import com.photo.best.pip.adapter.FolderAdapter;
//import com.photo.best.pip.adapter.PhotoAdapter;
//import com.photo.best.pip.data.App;
//import com.photo.best.pip.data.Folder;
//import com.photo.best.pip.utils.CommonUtils;
//import com.photo.best.pip.utils.DownloadUtils;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.GridView;


public class MoreAppActivity extends Activity {

	private GridView listPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_apps);
		listPhoto = (GridView) findViewById(R.id.list_photo);

		if (CoreService.showlApps != null && CoreService.showlApps.size() > 0) {
			AppAdapter adapter = new AppAdapter(MoreAppActivity.this,
					CoreService.showlApps);
			listPhoto.setAdapter(adapter);
		}

		initRequestForCount(this); // count on google cloud statistic
		// new AsynLoader().execute();
	}

	private void initRequestForCount(Context context) {
		
		String url = "http://adservice.tohsoft.com/morecount.php";

		GetAdPackageTask runner = new GetAdPackageTask(context);
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
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		// protected void onProgressUpdate(Integer... progress) {
		// //setProgressPercent(progress[0]);
		// }
		//
		// protected void onPostExecute(String result) {
		// //showDialog("Downloaded " + result + " bytes");
		// }
	}

}
