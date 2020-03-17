package com.tohsoft.lib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AppSelfLib {

    public static String language = "en";

    public static boolean showRateActivity(final Context context, int cntOpened) {
        boolean isShow = false;
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(
                RateDialogActivity.PRE_SHARING_CLICKED_MORE_APP,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int countRecord = pref.getInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED, 0);
        if (countRecord > cntOpened) {
            return false;
        } else if (countRecord == cntOpened) {
            Intent intent = new Intent(context, RateDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);

            isStopped = false;
            isCloseWithButton = false;
            isCloseWithNoThanks = false;
            isShow = true;
        }
        editor.putInt(RateDialogActivity.PRE_SHARING_COUNT_OPENED, countRecord + 1);
        editor.apply();
        return isShow;
    }

    public static String PRE_SHARING_IS_SHOW_RATE = "PRE_SHARING_IS_SHOW_RATE";

    public static void setShowRate(Context context) {
        String PRE_SHARING_ENABLE_SHOW_RATE = "PRE_SHARING_ENABLE_SHOW_RATE";
        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                PRE_SHARING_ENABLE_SHOW_RATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PRE_SHARING_IS_SHOW_RATE, true);
        editor.apply();
    }

    /**
     * show dialog with delay, subject can pass null from outside
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

    // Add this variable to check dialog is stopped?
    private static boolean isStopped = false;
    // Add this variable to check dialog has been stopped by buttons on view or back button on device
    private static boolean isCloseWithButton = false;
    // Add this variable to check dialog has been stopped by No, Thanks button
    private static boolean isCloseWithNoThanks = false;

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

    public static boolean isCloseWithNoThanks() {
        return isCloseWithNoThanks;
    }

    public static void setCloseWithNoThanks(boolean isCloseWithNoThanks) {
        AppSelfLib.isCloseWithNoThanks = isCloseWithNoThanks;
    }
}
