package com.tohsoft.app.data.local.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.utility.SharedPreference;


/**
 * Created by Phong on 11/9/2016.
 */

public class PreferencesHelper {
    private SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Exit dialog
     * */
    public void setShowExitDialog(boolean isShow) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, isShow);
            editor.apply();
        }
    }

    public boolean canShowExitDialog() {
        return sharedPreferences != null && sharedPreferences.getBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, true);
    }

    /**
    * Start in background permission on Xiaomi devices (new)
    * */
    public static void setStartInBackgroundShowed(Context context, boolean isShowed) {
        SharedPreference.setBoolean(context, PreferenceKeys.FREF_START_IN_BACKGROUND_SHOWED, isShowed);
    }

    public static boolean isStartInBackgroundShowed(Context context) {
        return SharedPreference.getBoolean(context, PreferenceKeys.FREF_START_IN_BACKGROUND_SHOWED, false);
    }
}
