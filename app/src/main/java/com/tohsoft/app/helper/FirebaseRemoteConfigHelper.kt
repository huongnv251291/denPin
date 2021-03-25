package com.tohsoft.app.helper

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.tohsoft.ads.AdsConfig
import com.tohsoft.ads.AdsModule
import com.tohsoft.app.BuildConfig
import com.tohsoft.app.R
import com.utility.DebugLog
import com.utility.SharedPreference

@SuppressLint("StaticFieldLeak")
class FirebaseRemoteConfigHelper {
    companion object {
        private const val REMOTE_PRO_APP_URL = "pro_app_url"
        private const val REMOTE_ADS_ID_LIST = "ads_id_list"
        private const val REMOTE_CUSTOM_ADS_ID_LIST = "custom_ads_id_list"
        private const val REMOTE_FREQ_CAP_INTER_OPA_IN_MINUTE = "freq_cap_inter_opa_in_minute"
        private const val REMOTE_SPLASH_DELAY_IN_MS = "splash_delay_in_ms"
        private const val REMOTE_INTER_OPA_PROGRESS_DELAY_IN_MS = "inter_opa_progress_delay_in_ms"
        private const val DEFAULT_ADS_ID_LIST = "ADMOB-0, ADMOB-1, ADMOB-2"
        private const val DEFAULT_FREQ_CAP_INTER_OPA_IN_MS = (15 * 60 * 1000 ).toLong()
        private const val DEFAULT_SPLASH_DELAY_IN_MS: Long = 3000 // 3 seconds
        private const val DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS: Long = 2000 // 2 seconds
        private var firebaseRemoteConfigHelper: FirebaseRemoteConfigHelper? = null

        val instance: FirebaseRemoteConfigHelper
            get() {
                if (firebaseRemoteConfigHelper == null) {
                    firebaseRemoteConfigHelper = FirebaseRemoteConfigHelper()
                }
                return firebaseRemoteConfigHelper!!
            }
    }

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var mContext: Context? = null
    private var isFetching = false

    private fun initializeApp(context: Context): Boolean {
        return try {
            mContext = context.applicationContext
            FirebaseApp.initializeApp(context)
            var cacheExpiration: Long = 3600
            if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
                cacheExpiration = 0
            }
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(cacheExpiration)
                    .build()
            mFirebaseRemoteConfig?.apply {
                setConfigSettingsAsync(configSettings)
                setDefaultsAsync(R.xml.remote_config_defaults)
            }
            true
        } catch (e: Exception) {
            DebugLog.loge(e)
            false
        }
    }

    fun fetchRemoteData(context: Context) {
        if (mFirebaseRemoteConfig == null) {
            if (!initializeApp(context)) {
                return
            }
        }
        if (isFetching) {
            return
        }
        isFetching = true
        mFirebaseRemoteConfig!!.fetchAndActivate()
                .addOnCompleteListener { task: Task<Boolean?> ->
                    isFetching = false
                    if (task.isSuccessful) {
                        DebugLog.loge("Fetch Successful")
                        setAdsConfigs()
                    } else {
                        DebugLog.loge("Fetch Failed")
                    }
                }
    }

    private fun setAdsConfigs() {
        if (BuildConfig.SHOW_AD) {
            SharedPreference.setString(mContext, REMOTE_ADS_ID_LIST, adsIdList)
            SharedPreference.setString(mContext, REMOTE_CUSTOM_ADS_ID_LIST, customAdsIdList)
            AdsConfig.getInstance()
                    .setFreqInterOPAInMs(freqInterOPAInMs)
                    .setSplashDelayInMs(splashDelayInMs).interOPAProgressDelayInMs = interOPAProgressDelayInMs
            AdsModule.getInstance()
                    .setAdsIdListConfig(adsIdList)
                    .setCustomAdsIdListConfig(customAdsIdList)
            DebugLog.logd("""
                setAdsConfigs:
                AdsIdList: $adsIdList
                CustomAdsIdList: $customAdsIdList
                FreqInterOPAInMs: $freqInterOPAInMs
                SplashDelayInMs: $splashDelayInMs
                InterOPAProgressDelayInMs: $interOPAProgressDelayInMs
                """.trimIndent()
            )
        }
    }

    val proVersionEnable: Boolean
        get() = if (mFirebaseRemoteConfig != null) {
            !TextUtils.isEmpty(mFirebaseRemoteConfig!!.getString(REMOTE_PRO_APP_URL))
        } else false

    val proAppUrl: String
        get() = if (mFirebaseRemoteConfig != null) {
            mFirebaseRemoteConfig!!.getString(REMOTE_PRO_APP_URL)
        } else ""

    val adsIdList: String
        get() = if (mFirebaseRemoteConfig != null) {
            mFirebaseRemoteConfig!!.getString(REMOTE_ADS_ID_LIST)
        } else SharedPreference.getString(mContext, REMOTE_ADS_ID_LIST, DEFAULT_ADS_ID_LIST)

    val customAdsIdList: String
        get() = if (mFirebaseRemoteConfig != null) {
            mFirebaseRemoteConfig!!.getString(REMOTE_CUSTOM_ADS_ID_LIST)
        } else SharedPreference.getString(mContext, REMOTE_CUSTOM_ADS_ID_LIST, "")

    val freqInterOPAInMs: Long
        get() = if (mFirebaseRemoteConfig != null) {
            mFirebaseRemoteConfig!!.getLong(REMOTE_FREQ_CAP_INTER_OPA_IN_MINUTE) * 60 * 1000
        } else DEFAULT_FREQ_CAP_INTER_OPA_IN_MS

    val splashDelayInMs: Long
        get() {
            if (BuildConfig.DEBUG) {
                return 10
            }
            return if (mFirebaseRemoteConfig != null) {
                mFirebaseRemoteConfig!!.getLong(REMOTE_SPLASH_DELAY_IN_MS)
            } else DEFAULT_SPLASH_DELAY_IN_MS
        }

    val interOPAProgressDelayInMs: Long
        get() {
            if (BuildConfig.DEBUG) {
                return 10
            }
            return if (mFirebaseRemoteConfig != null) {
                mFirebaseRemoteConfig!!.getLong(REMOTE_INTER_OPA_PROGRESS_DELAY_IN_MS)
            } else DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS
        }
}