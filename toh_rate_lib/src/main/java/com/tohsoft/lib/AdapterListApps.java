package com.tohsoft.lib;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterListApps extends ArrayAdapter<AppAdsObject> {
	  private final Context context;
	  private final List<AppAdsObject> listAppObject;

	  public AdapterListApps(Context context, List<AppAdsObject> listAppObject) {
	    super(context, R.layout.item_listview_layout, listAppObject);
	    this.context = context;
	    this.listAppObject = listAppObject;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.item_listview_layout, parent, false);
	    TextView textAppName = (TextView) rowView.findViewById(R.id.tv_app_name);
	    ImageView imgAppIcon = (ImageView) rowView.findViewById(R.id.img_app_icon);
	    TextView textDes = (TextView) rowView.findViewById(R.id.tv_des);
	    AppAdsObject appAdsObject = listAppObject.get(position);
	    String appName = appAdsObject.getTitle();
	    String appDes = appAdsObject.getDes();
	    final String appPkg = appAdsObject.getPkg();
	    byte[] appIcon = appAdsObject.getIconSize64();
	    textAppName.setText(appName);
	    textDes.setText(appDes);
//	    byte[] decodedString = Base64.decode(appIcon, Base64.URL_SAFE);
	    Bitmap bitmap = BitmapFactory.decodeByteArray(appIcon , 0, appIcon.length);
//	    Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
	    imgAppIcon.setImageBitmap(bitmap);
//	    imgAppIcon.setImageDrawable(drawable);
		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPkg)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	    return rowView;
	  }
	} 