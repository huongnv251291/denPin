package com.tohsoft.lib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

public class AppSelfLib {

    private static int stCntOpened = 0;
    public static String language = "en";

    public static boolean showRateActivityOnBack(final Context context,
                                                 int cntOpened, int requestCodeShowRate) {
        boolean isShow = false;
        stCntOpened = cntOpened;
        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int countRecord = pref.getInt(
                RateDialogActivity.PRE_SHARING_COUNT_OPENED, 0);
        if (countRecord > cntOpened) {
            // return false;
        } else if (countRecord <= cntOpened) {
            editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED,
                    cntOpened + 1);
            editor.commit();
            Intent intent = new Intent(context, RateDialogActivity.class);
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, requestCodeShowRate);
            isShow = true;
        }
//		else {
//			editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED,
//					countRecord + 1);
//			editor.commit();
//		}
        return isShow;
    }

    public static boolean showRateActivity(final Context context, int cntOpened) {
        boolean isShow = false;
        stCntOpened = cntOpened;
        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int countRecord = pref.getInt(
                RateDialogActivity.PRE_SHARING_COUNT_OPENED, 0);
        if (countRecord > cntOpened) {
            // return false;
        } else if (countRecord == cntOpened) {
            editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED,
                    cntOpened + 1);
            editor.commit();
            Intent intent = new Intent(context, RateDialogActivity.class);
            context.startActivity(intent);
            // return true;
            isStopped = false;
            isCloseWithButton = false;
            isShow = true;
        } else {
            editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED,
                    countRecord + 1);
            editor.commit();
            // return false;
        }
        return isShow;
    }

    public static boolean showRateAfterDays(final Context context, int days) {
        if (isEnableShowRate(context, days)) {
            showRateActivity(context, 0);
            return true;
        }
        return false;
    }

    public static String KEY_NUMBER_UNLOCK_APPEAR = "KEY_SHOW_RATEDIALOG_APPEAR";
    public static String PRE_SHARING_ENABLE_SHOW_RATE = "PRE_SHARING_ENABLE_SHOW_RATE";
    public static String PRE_SHARING_IS_SHOW_RATE = "PRE_SHARING_IS_SHOW_RATE";

    public static void setShowRate(Context context) {
        String PRE_SHARING_ENABLE_SHOW_RATE = "PRE_SHARING_ENABLE_SHOW_RATE";
        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                PRE_SHARING_ENABLE_SHOW_RATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PRE_SHARING_IS_SHOW_RATE, true);
        editor.commit();
    }

    public static boolean isEnableShowRate(Context context, int days) {
        boolean isEanble = false;

        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                PRE_SHARING_ENABLE_SHOW_RATE, Context.MODE_PRIVATE);
        boolean isShowRate = mSharedPreferences.getBoolean(PRE_SHARING_IS_SHOW_RATE, false);
        if (isShowRate) {
            return isEanble;
        }
        long time = System.currentTimeMillis();
        long openCount = mSharedPreferences.getLong(KEY_NUMBER_UNLOCK_APPEAR, 0l);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (openCount == 0l) {
            editor.putLong(KEY_NUMBER_UNLOCK_APPEAR, time);
            editor.commit();
            isEanble = false;
        } else {
            long showRate = time - (openCount + days * 86400000);
            if (showRate > 0) {
                isEanble = true;
            }
        }
        return isEanble;
    }

    public static void showRateResumeActivity(Context context) {
        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                        Context.MODE_PRIVATE);
        boolean ableShowRateActivity = pref.getBoolean(
                RateDialogActivity.IS_ABLE_SHOW_RATE_ACTIVITY, false);
        int countRecord = pref.getInt(
                RateDialogActivity.PRE_SHARING_COUNT_OPENED, 0);
        if (countRecord > stCntOpened) {
            return;
        }
        if (ableShowRateActivity) {
            SharedPreferences.Editor editor = pref.edit();
            Intent intent = new Intent(context, RateDialogActivity.class);
            context.startActivity(intent);
            editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED,
                    stCntOpened + 1);
            editor.commit();
        }
    }

    public static void setShowActivity(Context context) {
        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(RateDialogActivity.IS_ABLE_SHOW_RATE_ACTIVITY, true);
        editor.commit();
    }

    public static void showRateActivity(Context context) {
        Intent intent = new Intent(context, RateDialogActivity.class);
        context.startActivity(intent);
    }


//	public static boolean showRateActivityNewStyleHighScore(final Context context, int cntOpened) {
//		
//		SharedPreferences pref = context.getApplicationContext()
//				.getSharedPreferences(
//						RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE,
//						Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		
//		
//		editor.putBoolean(RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE, true);
//		
//		editor.commit();
//		
//		return showRateActivity(context, cntOpened);
//	}


    /**
     * show dialog with delay, subject can pass null from outside
     *
     * @param context
     * @param cntOpened
     * @param fbMailto
     * @param appname
     * @return
     */
    public static boolean showRateActivityNewStyleHighScore(final Context context, int cntOpened, String fbMailto, String appname) {

        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        editor.putBoolean(RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE, true);
        editor.putString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_FBMAILTO, fbMailto);
        editor.putString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_APPNAME, appname);
        editor.apply();

        return showRateActivity(context, cntOpened);
    }

    /**
     * show dialog instantly, subject can pass null from outside
     *
     * @param context
     * @param fbMailto
     * @param appname
     */
    public static void showRateActivityNewStyleHighScore(Context context, String fbMailto, String appname) {


        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        editor.putBoolean(RateDialogActivity.IS_NEW_DIALOG_HIGH_SCORE, true);
        editor.putString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_FBMAILTO, fbMailto);
        editor.putString(RateDialogActivity.NEW_DIALOG_HIGH_SCORE_APPNAME, appname);
        editor.commit();

        Intent intent = new Intent(context, RateDialogActivity.class);
        context.startActivity(intent);
    }

    public static void openGooglePlayNewApp(Context context) {
        SharedPreferences pref = context.getApplicationContext()
                .getSharedPreferences(
                        RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int countRecord = pref.getInt(
                RateDialogActivity.PRE_SHARING_COUNT_NEWAPPS_OPENED, 0);
        int siwtch;
        if (countRecord < 2) {
            // countRecord = countRecord%5;
            siwtch = 0;
        } else {
            siwtch = countRecord % 5;
        }
        String developId = CoreService.getPackageName(context);
        if (developId != null) {
            developId = developId.replaceAll("\r\n", "");
            developId = developId.replaceAll("\n\r", "");
            developId = developId.replaceAll("\r", "");
            developId = developId.replaceAll("\n", "");
        }
        editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_NEWAPPS_OPENED,
                countRecord + 1);
        editor.commit();
        if (isPackageInstalled(developId, context)) {
            int min = 1;
            int max = 4;
            Random r = new Random();
            siwtch = r.nextInt(max - min + 1) + min;
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + developId)));
            return;
        }
        switch (siwtch) {
            case 0:
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://details?id=" + "com.music.musicplayer.mp3player")));
//				if (developId == null || (developId.equalsIgnoreCase(""))) {
//					developId = "com.securesoltuion.app.blocksmscall";
//				}
                } catch (Exception e) {
                    developId = "com.securesoltuion.app.blocksmscall";
                    e.printStackTrace();
                }

                break;
            case 1:
                developId = "X Application";
                break;
            case 2:
                developId = "am application";
                break;
            case 3:
                developId = "Secure Solution";
            case 4:
                developId = "Green Banana";
                break;
            default:
                developId = "Secure Solution";
                break;
        }

        try {
            // context.startActivity(new Intent(Intent.ACTION_VIEW,
            // Uri.parse("market://details?id=" + developId)));
            // .parse("market://search?q=pub:Khai Nguyen" + developId)));
            if (siwtch != 0) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://search?q=pub:" + developId)));
            }

        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("https://play.google.com/store/apps/developer?id="
                            + developId)));
        }

    }


    public static void openGooglePlayCallRecorder(Context context) {
        String callRecorderPack = CoreService.getCallRecorderPackageName(context);
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                .parse("market://details?id=" + callRecorderPack)));
    }

    /**
     * @param context Hong add 25/05/2016
     */
    public static void openMoreAppActivity(Context context) {
        if (CoreService.newlApps == null || CoreService.newlApps.size() == 0) {
            openGooglePlayNewApp(context);
        } else {
            //
            CoreService.showlApps.clear();
            for (App ap : CoreService.newlApps) {
                if (!isPackageInstalled(ap.getMpackage(), context)) {
                    CoreService.showlApps.add(ap);
                }
            }

            if (CoreService.showlApps.size() == 0) {
                openGooglePlayNewApp(context);
            } else {
                // start more app activity
                Intent intent = new Intent(context, MoreAppActivity.class);
                context.startActivity(intent);


            }

        }
    }


    /**
     * @param context Hong add 25/05/2016
     */
    public static App getOneAppforAds(Context context) {
        if (CoreService.newlAppsForOneAds == null || CoreService.newlAppsForOneAds.size() == 0) {
            return null;
        } else {
            //

            for (App ap : CoreService.newlAppsForOneAds) {
                if (!isPackageInstalled(ap.getMpackage(), context)) {
                    return ap;
                }
            }

            return null;

        }
    }


    /**
     * @param context Hong add 25/05/2016
     */
    public static ArrayList<App> getListAppforAds(Context context) {
        if (CoreService.newlApps == null || CoreService.newlApps.size() == 0) {
            return null;
        } else {
            //

            ArrayList<App> adsApps = new ArrayList<App>();
            for (App ap : CoreService.newlApps) {
                if (!isPackageInstalled(ap.getMpackage(), context)) {
                    adsApps.add(ap);
                }
            }

            return adsApps;

        }
    }

    public static void openStore(Context context, String mPackage) {
        String url = "http://adservice.tohsoft.com/count3.php";
        initRequestForCount(context, url);

        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                .parse("market://details?id=" + mPackage)));
    }


    private static void initRequestForCount(Context context, String url) {


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

    private static boolean isPackageInstalled(String packagename,
                                              Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isEnableBannerAds(Context context,
                                            String KEY_NUMBER_UNLOCK_APPEAR, String dtStart, int showAfterCount) {
        String PRE_SHARING_ENABLE_BANNER_ADS = "PRE_SHARING_ENABLE_BANNER_ADS";
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PRE_SHARING_ENABLE_BANNER_ADS,
                Context.MODE_PRIVATE);
        // increase number unlock appear and save to references
        // if it over 100000 --> gan lai ve 100
        long openCount = mSharedPreferences.getLong(KEY_NUMBER_UNLOCK_APPEAR,
                0l);
        if (openCount >= 1000000) {
            openCount = 100;
        }
        openCount++;
        Editor edit = mSharedPreferences.edit();
        edit.putLong(KEY_NUMBER_UNLOCK_APPEAR, openCount);
        edit.commit();
        boolean result = true;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
            Date date = format.parse(dtStart);

            Date currentDate = new Date();
            if (date.before(currentDate)) {
                long unlockOpenCoutn = mSharedPreferences.getLong(
                        KEY_NUMBER_UNLOCK_APPEAR, 0l);
                if (unlockOpenCoutn >= showAfterCount) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            return false;
        }

        return result;
    }

    /**
     * call mail client, send feedback and suggestion
     *
     * @param context
     * @param fbMailTo: mail receive feedback
     * @param appName:  app name of application
     */
    public static void sendMailFeedback(Context context, String fbMailTo, String appName) {
        String subject = context.getResources().getString(R.string.title_fb_mail3) + ": " + appName;
        Intent i = new Intent("android.intent.action.SENDTO");
        i.setData(Uri.parse("mailto:" + fbMailTo));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            context.startActivity(Intent.createChooser(i, context.getResources().getString(R.string.rate_dislike3)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.no_email_client_toast3), Toast.LENGTH_SHORT).show();
        }
    }

    // Add this variable to check dialog is stopped?
    private static boolean isStopped = false;
    private static boolean isCloseWithButton = false;

    public static boolean canCloseApplication() {
        return isStopped && isCloseWithButton;
    }

    public static boolean isStopped() {
        return isStopped;
    }

    public static void setStopped(boolean isStopped) {
        AppSelfLib.isStopped = isStopped;
    }

    public static void setCloseWithButton(boolean isCloseWithButton) {
        AppSelfLib.isCloseWithButton = isCloseWithButton;
    }
}
