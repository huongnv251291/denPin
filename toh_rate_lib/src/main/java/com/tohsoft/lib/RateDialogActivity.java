package com.tohsoft.lib;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.widget.RatingBar.OnRatingBarChangeListener;


public class RateDialogActivity extends Activity {
    RatingBar ratingBar;
    Button btnNever;
    Button btnRate;
    Button btnLater;

    String linkGooglePlay;
    String pakage = "";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean isVoted = false;
    boolean isShowListApps = false;
    int countRecord = 0;

    public static String PRE_SHARING_CLICKED_MORE_APP = "PRE_SHARING_CLICKED_MORE_APP";
    public static String PRE_SHARING_CLICKED_VOTE_APP_VALUE = "PRE_SHARING_CLICKED_VOTE_APP_VALUE";
    public static String PRE_SHARING_COUNT_OPENED = "PRE_SHARING_COUNT_RECORD";
    public static String IS_ABLE_SHOW_RATE_ACTIVITY = "IS_ABLE_SHOW_RATE_ACTIVITY";

    public static String PRE_SHARING_COUNT_NEWAPPS_OPENED = "PRE_SHARING_COUNT_NEWAPPS_OPENED";

    public static String PRE_SHARING_CLICKED_MORE_APP_VALUE = "PRE_SHARING_CLICKED_MORE_APP_VALUE";


    public static String IS_NEW_DIALOG_HIGH_SCORE = "IS_NEW_DIALOG_HIGH_SCORE";
    public static String NEW_DIALOG_HIGH_SCORE_FBMAILTO = "IS_NEW_DIALOG_HIGH_SCORE_FBMAILTO";
    public static String NEW_DIALOG_HIGH_SCORE_APPNAME = "IS_NEW_DIALOG_HIGH_SCORE_APPNAME";
    private boolean isNewDialogHighScore = false;

    Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(RateLibLocaleManager.setLocale(newBase, AppSelfLib.language));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        super.onCreate(bundle);
//		setContentView(R.layout.rate_dialog_activity);

        context = this;

        SharedPreferences pref2 = getSharedPreferences(RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE, MODE_PRIVATE);
        isNewDialogHighScore = pref2.getBoolean(RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE, true);


        if (isNewDialogHighScore) {
            setContentView(R.layout.rate_dialog_activity_high_score);
        } else {
            //setContentView(R.layout.rate_dialog_activity);
        }

        pakage = getApplicationContext().getPackageName();
        linkGooglePlay = "https://play.google.com/store/apps/details?id=" + pakage;

        pref = getApplicationContext().getSharedPreferences(PRE_SHARING_CLICKED_MORE_APP, MODE_PRIVATE);
        isVoted = pref.getBoolean(PRE_SHARING_CLICKED_VOTE_APP_VALUE, false);
        isShowListApps = pref.getBoolean(PRE_SHARING_CLICKED_MORE_APP_VALUE, false);
        editor = pref.edit();
        countRecord = pref.getInt(PRE_SHARING_COUNT_OPENED, 0);

        /*
        * Luôn reset lại count state mỗi khi hiển thị lên Dialog rate
        * -> Fix lỗi không hiển thị lại RateLib khi đang show RateLib thì kill app bằng recent
        * */
        resetState();
        AppSelfLib.setCloseWithNoThanks(false);
        AppSelfLib.setCloseWithButton(false);
        AppSelfLib.setStopped(false);

        ratingBar = findViewById(R.id.rating_5_stars);
        btnRate = findViewById(R.id.btn_rate);
        btnLater = findViewById(R.id.btn_later);
        btnNever = findViewById(R.id.btn_cancel);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT >= 11) {
            setFinishOnTouchOutside(false);
        }

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if (rating > 4) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pakage)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pakage)));
                    }
                    finish();
                } else {
                    finish();
                }
                AppSelfLib.setCloseWithButton(true);
                AppSelfLib.setStopped(true);
            }
        });

        btnNever.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setStateNeverShowAgain();

                ///get editor
                SharedPreferences pref = getSharedPreferences(
                        RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE,
                        RateDialogActivity.MODE_PRIVATE);
                String fbMailTo = pref.getString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_FBMAILTO, null);
                String appname = pref.getString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_APPNAME, null);
                String subject = "";

                if (fbMailTo != null) {
                    if (appname == null) {
                        subject = getResources().getString(R.string.title_fb_mail3);
                    } else {
                        subject = getResources().getString(R.string.title_fb_mail3) + ": " + appname;
                    }
                    sendMail(fbMailTo, subject);
                }
                AppSelfLib.setCloseWithButton(true);
                AppSelfLib.setStopped(true);
                finish();
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setStateNeverShowAgain();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pakage)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pakage)));
                }
                try {
                    AppSelfLib.setShowRate(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppSelfLib.setCloseWithButton(true);
                AppSelfLib.setStopped(true);
                finish();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetState();
                AppSelfLib.setCloseWithButton(true);
                AppSelfLib.setCloseWithNoThanks(true);
                AppSelfLib.setStopped(true);
                finish();
            }
        });
    }

    private void setStateNeverShowAgain() {
        if (editor != null) {
            editor.putInt(PRE_SHARING_COUNT_OPENED, 6);
            editor.apply();
        }
    }

    private void resetState() {
        if (editor != null) {
            editor.putBoolean(RateDialogActivity.IS_ABLE_SHOW_RATE_ACTIVITY, false);
            editor.putInt(PRE_SHARING_COUNT_OPENED, -5);
            editor.apply();
        }
    }

    /*
    * Comment mới của a Hiếu
    * - Khi đang show rate dialog, không cho phép thoát dialog bằng việc click back
    * */
    @Override
    public void onBackPressed() {
        /*AppSelfLib.setStopped(true);
        super.onBackPressed();*/
    }

    @Override
    protected void onDestroy() {
        AppSelfLib.setStopped(true);
        super.onDestroy();
    }

    public void sendMail(String fbMailTo, String subject) {
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setData(Uri.parse("mailto:" + fbMailTo));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.rate_dislike3)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_email_client_toast3), Toast.LENGTH_SHORT).show();
        }
    }
}