package com.tohsoft.app.data.local.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Phong on 11/9/2016.
 */

public class PreferencesHelper {
    private SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*
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

}
