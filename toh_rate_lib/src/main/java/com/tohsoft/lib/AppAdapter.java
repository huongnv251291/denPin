package com.tohsoft.lib;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import com.photo.best.pip.adapter.AppAdapter.ViewHolder;
//import com.photo.best.pip.adapter.AppAdapter.ViewHolder;
//import com.photo.best.pip.R;
//import com.photo.best.pip.adapter.MagazinAdapter.ViewHolder;
//import com.photo.best.pip.data.App;
//import com.photo.best.pip.data.Folder;
//import com.photo.best.pip.data.Pip;
//import com.photoedit.best.photoframe.loader.PhotoView;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<App> lApps;
    private Picasso picasso;
    private int size;
    Context context;
	public AppAdapter(Activity context,ArrayList<App> lApps){
		this.inflater = LayoutInflater.from(context);
		this.lApps = lApps;
		this.picasso = Picasso.with(context);
		size = (int) context.getResources().getDimension(R.dimen.item_size_list_app);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		
		return lApps.size();
	}
	
	

	@Override
	public Object getItem(int position) {
		return lApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
        if(convertView == null){
//		ViewHolder viewHolder = null;

		final int pos = position;
		convertView = inflater.inflate(R.layout.item_list_app, null);
		viewHolder = new ViewHolder();
		viewHolder.wImageView = (ImageView) convertView.findViewById(R.id.view_photo_1);
		viewHolder.wTextView = (TextView)convertView.findViewById(R.id.title_app);
		//viewHolder.wTextView.setText(lApps.get(position).getName());
		convertView.findViewById(R.id.view_photo_1).setOnClickListener(new OnClickListener() {
			
					@Override
					public void onClick(View v) {
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id=" + lApps.get(pos).getMpackage())));
						
					}
		});


		convertView.setTag(viewHolder);
        }else{
        	viewHolder = (ViewHolder) convertView.getTag();
        }
		App app = (App) getItem(position);
		String pFile = app.getIcon();
		picasso.load(pFile).resize(size, size).placeholder( R.drawable.progress_animation ).into(viewHolder.wImageView);
		try {
			String name = new String(app.getName().getBytes("ISO-8859-1"), "UTF-8");
			viewHolder.wTextView.setText(name);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return convertView;
	}
	
	public class ViewHolder{
		public ImageView wImageView;
		public TextView wTextView;
	}

}
