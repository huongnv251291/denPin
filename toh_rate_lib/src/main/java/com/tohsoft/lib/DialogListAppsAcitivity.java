package com.tohsoft.lib;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;


public class DialogListAppsAcitivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//				R.layout.bar_recorder_activity);
		setContentView(R.layout.layput_list_apps_ads);
		List<AppAdsObject> listAppObject = CoreService.getListObjectApps(this);
		AdapterListApps adapterListApps = new AdapterListApps(this,
				listAppObject);
		ListView lv = (ListView) findViewById(R.id.listview);
		lv.setAdapter(adapterListApps);
	}
}
