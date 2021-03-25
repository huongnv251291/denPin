package com.tohsoft.app.data

import android.annotation.SuppressLint
import android.content.Context
import com.tohsoft.app.data.local.preference.PreferencesHelper
import com.tohsoft.app.data.network.DataManager
import com.tohsoft.app.data.network.getNetworkService

/**
 * Created by Phong on 3/1/2017.
 */
class ApplicationModules {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var sApplicationModules: ApplicationModules? = null

        val instant: ApplicationModules
            get() {
                if (sApplicationModules == null) {
                    sApplicationModules = ApplicationModules()
                }
                return sApplicationModules!!
            }
    }

    private var mContext: Context? = null
    private lateinit var mDataManager: DataManager
    private lateinit var mPreferencesHelper: PreferencesHelper

    val preferencesHelper: PreferencesHelper
        get() = mPreferencesHelper

    val dataManager: DataManager
        get() = mDataManager

    /*
     *Initialize modules for app
     */
    fun initModules(context: Context?) {
        mContext = context
        mPreferencesHelper = PreferencesHelper(context!!)
        mDataManager = DataManager(getNetworkService(), mPreferencesHelper)
    }

    fun onDestroy() {
        sApplicationModules = null
    }
}