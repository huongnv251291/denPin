package com.tohsoft.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Process
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tohsoft.ads.AdsConfig
import com.tohsoft.ads.AdsModule
import com.tohsoft.app.data.ApplicationModules
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper
import com.tohsoft.app.ui.main.MainActivity
import com.utility.DebugLog
import com.utility.SharedPreference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.system.exitProcess

/**
 * Created by Phong on 11/9/2016.
 */
class BaseApplication : MultiDexApplication() {
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        DebugLog.DEBUG = BuildConfig.DEBUG || BuildConfig.TEST_AD // Đặt flag cho DebugLog (Chỉ hiển thị log trong bản build Debug | TEST_AD)
        ApplicationModules.instant.initModules(this)
        FirebaseRemoteConfigHelper.instance.fetchRemoteData(this)
        Utils.init(this)

        // Init AdsConfigs
        AdsConfig.getInstance().init(this)
                .setFullVersion(BuildConfig.FULL_VERSION)
                .setTestMode(BuildConfig.TEST_AD)
                .setFreqInterOPAInMs(FirebaseRemoteConfigHelper.instance.freqInterOPAInMs)
                .setSplashDelayInMs(FirebaseRemoteConfigHelper.instance.splashDelayInMs)
                .setInterOPAProgressDelayInMs(FirebaseRemoteConfigHelper.instance.interOPAProgressDelayInMs)
                .addTestDevices("0ca1bc4f-365d-4303-9312-1324d43e329c")
        // Init Ads module
        AdsModule.getInstance().init(this)
                .setResourceAdsId("admob_ids.json", null)
                .setAdsIdListConfig(FirebaseRemoteConfigHelper.instance.adsIdList)
                .setCustomAdsIdListConfig(FirebaseRemoteConfigHelper.instance.customAdsIdList)
        if (!BuildConfig.DEBUG) {
            initCrash()
        }
    }

    fun addRequest(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable!!)
    }

    fun clearAllRequest() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    /*
     * Tự động restart app khi bị crash
     * */
    @SuppressLint("MissingPermission")
    private fun initCrash() {
        CrashUtils.init { _: String?, e: Throwable? ->
            e?.let {
                FirebaseCrashlytics.getInstance().recordException(e)
                DebugLog.loge(e)
            }
            restartApp()
        }
    }

    private fun restartApp() {
        if (!shouldAutoRestartApp()) {
            SharedPreference.setInt(this, AUTO_RESTART, 0)
            killApp()
            return
        }
        setFlagAutoRestartApp()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val restartIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val manager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        manager[AlarmManager.RTC, System.currentTimeMillis() + 1] = restartIntent
        killApp()
    }

    private fun killApp() {
        ActivityUtils.finishAllActivities()
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    /*
     * Tự restart app tối đa 3 lần liên tục
     * */
    private fun shouldAutoRestartApp(): Boolean {
        return SharedPreference.getInt(this, AUTO_RESTART, 0) < 3
    }

    private fun setFlagAutoRestartApp() {
        val currentCount = SharedPreference.getInt(this, AUTO_RESTART, 0)
        SharedPreference.setInt(this, AUTO_RESTART, currentCount + 1)
    }

    override fun onTerminate() {
        super.onTerminate()
        ApplicationModules.instant.onDestroy()
        clearAllRequest()
    }

    companion object {
        var instance: BaseApplication? = null
            private set
        private const val AUTO_RESTART = "AUTO_RESTART"
    }
}