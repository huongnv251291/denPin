package com.tohsoft.app;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.multidex.MultiDexApplication;

import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.main.MainActivity;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.Utils;
import com.utility.DebugLog;
import com.utility.SharedPreference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * Created by Phong on 11/9/2016.
 */

public class BaseApplication extends MultiDexApplication {
    private static BaseApplication sBaseApplication;
    private CompositeDisposable mCompositeDisposable;

    public static BaseApplication getInstance() {
        return sBaseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sBaseApplication = this;
        DebugLog.DEBUG = BuildConfig.DEBUG; // Đặt flag cho DebugLog (Chỉ hiển thị log trong bản build Debug)
        ApplicationModules.getInstant().initModules(getApplicationContext());

        Utils.init(this);
        if (!BuildConfig.DEBUG) {
            initCrash();
        }
    }

    public void addRequest(Disposable disposable){
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /*
    * Tự động restart app khi bị crash
    * */
    @SuppressLint("MissingPermission")
    private void initCrash() {
        CrashUtils.init((crashInfo, e) -> {
            DebugLog.loge(e);
            restartApp();
        });
    }

    private void restartApp() {
        if (!shouldAutoRestartApp()) {
            SharedPreference.setInt(this, AUTO_RESTART, 0);
            killApp();
            return;
        }
        setFlagAutoRestartApp();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (manager == null) return;
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1, restartIntent);
        killApp();
    }

    private void killApp() {
        ActivityUtils.finishAllActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private static final String AUTO_RESTART = "AUTO_RESTART";

    /*
    * Tự restart app tối đa 3 lần liên tục
    * */
    private boolean shouldAutoRestartApp() {
        return SharedPreference.getInt(this, AUTO_RESTART, 0) < 3;
    }

    private void setFlagAutoRestartApp() {
        int currentCount = SharedPreference.getInt(this, AUTO_RESTART, 0);
        SharedPreference.setInt(this, AUTO_RESTART, currentCount + 1);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ApplicationModules.getInstant().onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
