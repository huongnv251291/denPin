package com.tohsoft.app.utils.commons;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tohsoft.app.BaseApplication;
import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.utils.Utils;
import com.utility.DebugLog;
import com.utility.SharedPreference;
import com.utility.UtilsLib;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Phong on 8/29/2017.
 */

public class Communicate {
    public static final String EMAIL_COMPANY = "app@tohsoft.com";
    private static final String DEFAULT_MORE_APPS = "developer?id=TOHsoft+Co.,+Ltd";
    private static final String MORE_APPS_INFO = "MORE_APPS_INFO";
    private static final String PACKAGE_NAME_PRO = BuildConfig.APPLICATION_ID + ".pro";
    private static final String SHARE_CONTENT = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    private static String sMoreApp = "";

    public static void saveMoreAppsDetails(Context context, String details) {
        SharedPreference.setString(context, MORE_APPS_INFO, details);
    }

    public static void rateApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.setData(Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            try {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                context.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    private static void moreApps(Context context) {
        String detailMoreApps = sMoreApp;
        if (detailMoreApps.isEmpty()) {
            detailMoreApps = DEFAULT_MORE_APPS;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.setData(Uri.parse("market://" + detailMoreApps));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            try {
                intent.setData(Uri.parse("https://play.google.com/store/apps/" + detailMoreApps));
                context.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    public static void getFullVersion(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.setData(Uri.parse("market://details?id=" + PACKAGE_NAME_PRO));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            try {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_PRO));
                context.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    public static void onFeedback(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/email");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_COMPANY});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.lbl_report_problem) + " " + context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, "\n\n---- Device Info ----\n" + UtilsLib.getInfoDevices(context));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.lbl_report_problem_with)));
    }

    public static void shareApps(Context context) {
        if (context == null) {
            return;
        }
        final String SHARE_SUBJECT = context.getString(R.string.app_name);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
        intent.putExtra(Intent.EXTRA_TEXT, SHARE_CONTENT);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.lbl_share_app)));
    }

    public static void onMoreApp(Context context) {
        if (!TextUtils.isEmpty(sMoreApp)) {
            Communicate.moreApps(context);
            return;
        }
        Utils.showProgress(context, context.getString(R.string.msg_please_wait));
        Disposable disposable = ApplicationModules.getInstant().getDataManager()
                .getMoreApps()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(moreAppsResult -> {
                    Utils.dismissCurrentDialog();
                    if (moreAppsResult != null) {
                        sMoreApp = moreAppsResult.moreApps;
                    }
                    moreApps(context);
                }, throwable -> {
                    DebugLog.loge(throwable.getMessage());
                    Utils.dismissCurrentDialog();
                    moreApps(context);
                });
        BaseApplication.getInstance().addRequest(disposable);
    }
}
