package com.tohsoft.app.data.local.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.tohsoft.app.BuildConfig

/**
 * Created by Phong on 11/9/2016.
 */
class PreferencesHelper(context: Context) {

    private val mSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Exit dialog
     */
    fun setShowExitDialog(isShow: Boolean) {
        setBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, isShow)
    }

    fun canShowExitDialog(): Boolean {
        return mSharedPreferences.getBoolean(PreferenceKeys.PREF_SHOW_EXIT_DIALOG, true)
    }

    /**
     * Get PRO version dialog
     */
    fun setGetProVersionEnable(isEnable: Boolean) {
        setBoolean(PreferenceKeys.FREF_GET_PRO_VERSION_ENABLE, isEnable)
    }

    fun canShowGetProVersion(): Boolean {
        if (BuildConfig.FULL_VERSION || !getBoolean(PreferenceKeys.FREF_GET_PRO_VERSION_ENABLE, true)) {
            return false
        }
        var currentCount = countShowGetProApp
        currentCount++
        if (currentCount == 3) {
            currentCount = 5
        }
        setCountGetProApp(currentCount)
        return currentCount > 0 && currentCount % 5 == 0
    }

    private val countShowGetProApp: Int
        get() = getInt(PreferenceKeys.FREF_GET_PRO_VERSION_COUNT, 0)

    fun setCountGetProApp(value: Int) {
        setInt(PreferenceKeys.FREF_GET_PRO_VERSION_COUNT, value)
    }

    /*=============================================================================================*/
    fun getString(key: String?, defValue: String?): String? {
        return mSharedPreferences.getString(key, defValue)
    }

    fun setString(key: String?, value: String?) {
        mSharedPreferences.edit().apply {
            putString(key, value)
            apply()
        }
    }

    private fun getInt(key: String, defValue: Int): Int {
        return mSharedPreferences.getInt(key, defValue)
    }

    private fun setInt(key: String, value: Int) {
        mSharedPreferences.edit().apply {
            putInt(key, value)
            apply()
        }
    }

    private fun getLong(key: String, defValue: Long): Long {
        return mSharedPreferences.getLong(key, defValue)
    }

    private fun setLong(key: String, value: Long) {
        mSharedPreferences.edit().apply {
            putLong(key, value)
            apply()
        }
    }

    private fun getFloat(key: String, defValue: Float): Float {
        return mSharedPreferences.getFloat(key, defValue)
    }

    private fun setFloat(key: String, value: Float) {
        mSharedPreferences.edit().apply {
            putFloat(key, value)
            apply()
        }
    }

    private fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defValue)
    }

    private fun setBoolean(key: String, value: Boolean) {
        mSharedPreferences.edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

}