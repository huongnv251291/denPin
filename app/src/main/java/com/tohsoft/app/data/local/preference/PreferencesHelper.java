package com.tohsoft.app.data.local.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;
import com.utility.SharedPreference;


/**
 * Created by Phong on 11/9/2016.
 */

public class PreferencesHelper {
    private SharedPreferences mSharedPreferences;

    public PreferencesHelper(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Exit dialog
     */
    public void setShowExitDialog(boolean isShow) {
        setBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, isShow);
    }

    public boolean canShowExitDialog() {
        return mSharedPreferences != null && mSharedPreferences.getBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, true);
    }

    /**
     * Get PRO version dialog
     */
    public void setGetProVersionEnable(boolean isEnable) {
        setBoolean(PreferenceKeys.FREF_GET_PRO_VERSION_ENABLE, isEnable);
    }

    public boolean canShowGetProVersion() {
        if (BuildConfig.FULL_VERSION || !getBoolean(PreferenceKeys.FREF_GET_PRO_VERSION_ENABLE, true)) {
            return false;
        }
        int currentCount = getCountShowGetProApp();
        currentCount++;
        if (currentCount == 3) {
            currentCount = 5;
        }
        setCountGetProApp(currentCount);

        return currentCount > 0 && currentCount % 5 == 0;
    }

    private int getCountShowGetProApp() {
        return getInt(PreferenceKeys.FREF_GET_PRO_VERSION_COUNT, 0);
    }

    public void setCountGetProApp(int value) {
        setInt(PreferenceKeys.FREF_GET_PRO_VERSION_COUNT, value);
    }

    /**
     * Start in background permission on Xiaomi devices (new)
     */
    public static void setStartInBackgroundShowed(Context context, boolean isShowed) {
        SharedPreference.setBoolean(context, PreferenceKeys.FREF_START_IN_BACKGROUND_SHOWED, isShowed);
    }

    public static boolean isStartInBackgroundShowed(Context context) {
        return SharedPreference.getBoolean(context, PreferenceKeys.FREF_START_IN_BACKGROUND_SHOWED, false);
    }

    /*=============================================================================================*/

    public String getString(String key, String defValue) {
        if (mSharedPreferences == null) {
            return "";
        }
        return mSharedPreferences.getString(key, defValue);
    }

    public void setString(String key, String value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    private int getInt(String key, int defValue) {
        if (mSharedPreferences == null) {
            return 0;
        }
        return mSharedPreferences.getInt(key, defValue);
    }

    private void setInt(String key, int value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    private long getLong(String key, long defValue) {
        if (mSharedPreferences == null) {
            return 0L;
        }
        return mSharedPreferences.getLong(key, defValue);
    }

    private void setLong(String key, long value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    private boolean getBoolean(String key, boolean defValue) {
        if (mSharedPreferences == null) {
            return false;
        }
        return mSharedPreferences.getBoolean(key, defValue);
    }

    private void setBoolean(String key, boolean value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }
}
