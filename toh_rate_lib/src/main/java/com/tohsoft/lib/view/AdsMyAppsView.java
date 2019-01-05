//package com.tohsoft.lib.view;
//
//import com.tohsoft.lib.AppAdsObject;
//import com.tohsoft.lib.CoreService;
//import com.banana.lib.R;
//import com.tohsoft.lib.anim.PauseRotateAnimation;
//import com.tohsoft.lib.analytics.GoogleAnalyticsApplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.Animation;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class AdsMyAppsView extends LinearLayout{
//	private String leftLabel = "";
//	private String rightLabel = "";
//	private TextView leftTextView;
//	private TextView rightTextView;
//	public static String INTENT_FILTER_UPDATE_CLOSE_ADS = "INTENT_FILTER_UPDATE_CLOSE_ADS";
//			
//	ImageView imgAppIcon;
//	ImageView imgDownloadIcon;
//	ImageView imgCloseIcon;
//	TextView tvTitle;
//	TextView tvDes;
//	Context context;
//	RelativeLayout mainLayout;
//	AdsMyAppsView adsMyAppsView;
//	AppAdsObject appAdsObject;
//	public AdsMyAppsView(Context context) {
//		super(context);
//		appAdsObject = CoreService.getFirstObjectApps(context);
//		LayoutInflater.from(context).inflate(R.layout.custom_layout_show_ads_app, this);
//		adsMyAppsView = this;
//		if(appAdsObject == null){
//			adsMyAppsView.setVisibility(View.GONE);
//		}
//		try {
//			String packageName = context.getPackageName();
//			GoogleAnalyticsApplication.getInstance().trackScreenView(packageName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public AdsMyAppsView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		appAdsObject = CoreService.getFirstObjectApps(context);
//		initViews(context, attrs);
//		adsMyAppsView = this;
//		if(appAdsObject == null){
//			adsMyAppsView.setVisibility(View.GONE);
//		}
//		try {
//			String packageName = context.getPackageName();
//			GoogleAnalyticsApplication.getInstance().trackScreenView(packageName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public AdsMyAppsView(Context context, AttributeSet attrs, int defStyle) {
//		this(context, attrs);
//		appAdsObject = CoreService.getFirstObjectApps(context);
//		initViews(context, attrs);
//		adsMyAppsView = this;
//		if(appAdsObject == null){
//			adsMyAppsView.setVisibility(View.GONE);
//		}
//		try {
//			String packageName = context.getPackageName();
//			GoogleAnalyticsApplication.getInstance().trackScreenView(packageName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//    public boolean onTouchEvent(final MotionEvent event) {
//        if(event.getAction() == MotionEvent.ACTION_UP){
//        	if(appAdsObject == null){
//        		return true;
//        	}
//        	String appPkg = appAdsObject.getPkg();
//        	Intent intent = new Intent(Intent.ACTION_VIEW,
//    				Uri.parse("market://details?id=" + appPkg));
//        	context.startActivity(intent);
//        	try {
//    			String packageName = context.getPackageName();
//    			GoogleAnalyticsApplication.getInstance().trackEvent(packageName, "Music track play", "Click from touch");
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//            return performClick();
//        }
//        return true;
//    }
//	
//	private void initViews(final Context context, AttributeSet attrs) {
//    	if(appAdsObject == null){
//    		return;
//    	}
//		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
//				R.styleable.AdsMyAppsView, 0, 0);
//		adsMyAppsView = this;
//		try {
//			// get the text and colors specified using the names in attrs.xml
//			leftLabel = a.getString(R.styleable.AdsMyAppsView_leftLabel);
//			rightLabel = a.getString(R.styleable.AdsMyAppsView_rightLabel);
////			leftStyle = a.getResourceId(R.styleable.AdsMyAppsView_leftLabelStyle, android.R.style.TextAppearance_DeviceDefault);
////			rightStyle = a.getResourceId(R.styleable.AdsMyAppsView_rightLabelStyle, android.R.style.TextAppearance_DeviceDefault);
//		} finally {
//			a.recycle();
//		}
//		
//		LayoutInflater.from(context).inflate(R.layout.custom_layout_show_ads_app, this);
//		
//		// Using RemoteViews to bind custom layouts into Notification
//				
//				String appName = appAdsObject.getTitle();
//				String appDes = appAdsObject.getDes();
//				final String appPkg = appAdsObject.getPkg();
//				byte[] appIcon = appAdsObject.getIconSize64();
//				Bitmap bitmap = BitmapFactory.decodeByteArray(appIcon, 0,
//						appIcon.length);
//				
//				imgAppIcon = (ImageView) this.findViewById(R.id.avatar_icon);
//				imgDownloadIcon = (ImageView) this.findViewById(R.id.img_download);
//				imgCloseIcon = (ImageView) this.findViewById(R.id.img_app_download);
//				tvTitle = (TextView) this.findViewById(R.id.tv_app_name);
//				tvDes = (TextView) this.findViewById(R.id.tv_des);
//				mainLayout  = (RelativeLayout) this.findViewById(R.id.ln_custom_layout);
//				
//				
//				com.tohsoft.lib.view.RoundedImageView iconAvatar = (com.tohsoft.lib.view.RoundedImageView) this.findViewById(R.id.img_app_icon);
//				PauseRotateAnimation rotate = new PauseRotateAnimation(0, 360,
//				        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				        0.5f);
//
//				rotate.setDuration(15000);
//				rotate.setRepeatCount(Animation.INFINITE);
//				iconAvatar.setAnimation(rotate);
//				
//				tvTitle.setText(appName);
////				tvTitle.setTextAppearance(context, leftStyle);
//				tvTitle.setTypeface(null, Typeface.BOLD);
//				tvDes.setText(appDes);
////				tvDes.setTextAppearance(context, leftStyle);
//				imgAppIcon.setImageBitmap(bitmap);
//				
//				 mainLayout.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							Intent intent = new Intent(Intent.ACTION_VIEW,
//				    				Uri.parse("market://details?id=" + appPkg));
//				        	context.startActivity(intent);
//				        	try {
//				    			String packageName = context.getPackageName();
//				    			GoogleAnalyticsApplication.getInstance().trackEvent(packageName, "Music track play", "Click listener");
//				    		} catch (Exception e) {
//				    			e.printStackTrace();
//				    		}
//						}
//					});
//				 imgCloseIcon.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							adsMyAppsView.setVisibility(View.GONE);
//							Intent intent = new Intent(INTENT_FILTER_UPDATE_CLOSE_ADS);
//							LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//							try {
//				    			String packageName = context.getPackageName();
//				    			GoogleAnalyticsApplication.getInstance().trackEvent(packageName, "Close Music track play", "Close event");
//				    		} catch (Exception e) {
//				    			e.printStackTrace();
//				    		}
//						}
//					});
//	        	
//	}
//
//	public String getLeftLabel() {
//		return leftLabel;
//	}
//
//	public void setLeftLabel(String leftLabel) {
//		this.leftLabel = leftLabel;
//		if(leftTextView!=null){
//			leftTextView.setText(leftLabel);
//		}
//	}
//
//	public String getRightLabel() {
//		return rightLabel;
//	}
//
//	public void setRightLabel(String rightLabel) {
//		this.rightLabel = rightLabel;
//		if(rightTextView!=null){
//			rightTextView.setText(rightLabel);
//		}
//	}
//	
//	public boolean isInstalled(Context context){
//		if(appAdsObject != null){
//			try {
//				return CoreService.isPackageInstalled(appAdsObject.getPkg(), context);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return true;
//			}
//		}else{
//			return true;
//		}
//	}
//	
//}
