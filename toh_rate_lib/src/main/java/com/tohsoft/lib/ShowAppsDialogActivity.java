package com.tohsoft.lib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import android.view.*;


public class ShowAppsDialogActivity extends Activity {
	RelativeLayout btnRateApps;
	String developId = null;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_apps_activity);
		developId = CoreService.getPackageName(this);

		btnRateApps = (RelativeLayout) findViewById(R.id.bgr_get_now);
		btnRateApps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (developId != null) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + developId)));
				}
				finish();
			}
		});
	}
}