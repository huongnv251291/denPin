package com.tohsoft.app.data.network

import com.tohsoft.app.BuildConfig
import com.tohsoft.app.data.local.preference.PreferencesHelper
import com.tohsoft.app.data.models.MoreApps
import io.reactivex.Single
import java.util.*

/**
 * Created by Phong on 11/9/2016.
 */
class DataManager(private val mRemoteApiService: NetworkService, private val mPreferencesHelper: PreferencesHelper) {

    val moreApps: Single<MoreApps>
        get() {
            val params: MutableMap<String, String> = HashMap()
            params["app_id"] = BuildConfig.APPLICATION_ID
            return mRemoteApiService.moreApps(params)
        }
}