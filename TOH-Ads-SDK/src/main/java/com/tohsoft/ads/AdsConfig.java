package com.tohsoft.ads;

import android.app.Application;

import com.utility.DebugLog;
import com.utility.SharedPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by PhongNX on 6/5/2020.
 */
public class AdsConfig {
    private static final String LAST_TIME_INTER_OPA_SHOWED = "last_time_interstitial_opa_showed";
    private static final String FREQ_INTER_OPA_IN_MILLISECONDS = "freq_interstitial_opa_in_ms";
    private static final String SPLASH_DELAY_IN_MS = "splash_delay_in_ms";
    private static final String INTER_OPA_PROGRESS_DELAY_IN_MS = "inter_opa_progress_delay_in_ms";

    private static final long DEFAULT_FREQ_CAP_INTER_OPA_IN_MS = 15 * 60 * 1000; // 15 minutes
    private static final long DEFAULT_SPLASH_DELAY_IN_MS = 3000; // 3 seconds
    private static final long DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS = 2000; // 2 seconds

    private static AdsConfig sAdsConfig;
    private Application mApplication;
    private List<String> mTestDevices = new ArrayList<>();
    private boolean isFullVersion;
    private boolean isTestMode;

    public static AdsConfig getInstance() {
        if (sAdsConfig == null) {
            sAdsConfig = new AdsConfig();
        }
        return sAdsConfig;
    }

    public AdsConfig init(Application application) {
        mApplication = application;
        return sAdsConfig;
    }

    /*
     * Add device hashed ID for Test mode (FAN)
     * */
    public AdsConfig addTestDevices(Collection<String> collection) {
        if (collection != null) {
            mTestDevices.addAll(collection);
        }
        return sAdsConfig;
    }

    /*
     * Add device hashed ID for Test mode (FAN)
     * */
    public AdsConfig addTestDevices(String... devicesHash) {
        if (devicesHash != null) {
            mTestDevices.addAll(Arrays.asList(devicesHash));
        }
        return sAdsConfig;
    }

    public AdsConfig setTestMode(boolean testMode) {
        isTestMode = testMode;
        if (isTestMode) {
            DebugLog.DEBUG = true;
        }
        return sAdsConfig;
    }

    public AdsConfig setFullVersion(boolean fullVersion) {
        isFullVersion = fullVersion;
        return sAdsConfig;
    }

    /*
     * Last time OPA showed
     * */
    public AdsConfig setLastTimeOPAShow() {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, LAST_TIME_INTER_OPA_SHOWED, System.currentTimeMillis());
        }
        return sAdsConfig;
    }

    /*
     * Splash delay time
     * */
    public AdsConfig setSplashDelayInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, SPLASH_DELAY_IN_MS, time);
        }
        return sAdsConfig;
    }

    /*
     * Fake progress delay time
     * */
    public AdsConfig setInterOPAProgressDelayInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, INTER_OPA_PROGRESS_DELAY_IN_MS, time);
        }
        return sAdsConfig;
    }

    /*
     *
     * */
    public List<String> getTestDevices() {
        return mTestDevices;
    }

    public boolean isFullVersion() {
        return isFullVersion;
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public boolean canShowOPA() {
        long freqInterOPAInMilliseconds = SharedPreference.getLong(mApplication, FREQ_INTER_OPA_IN_MILLISECONDS, DEFAULT_FREQ_CAP_INTER_OPA_IN_MS);
        if (freqInterOPAInMilliseconds == 0) {
            return true;
        }
        long lastTimeOPAShow = SharedPreference.getLong(mApplication, LAST_TIME_INTER_OPA_SHOWED, DEFAULT_FREQ_CAP_INTER_OPA_IN_MS);
        return System.currentTimeMillis() - lastTimeOPAShow >= freqInterOPAInMilliseconds;
    }

    /*
     * Frequency time limited for OPA
     * */
    public AdsConfig setFreqInterOPAInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, FREQ_INTER_OPA_IN_MILLISECONDS, time);
        }
        return sAdsConfig;
    }

    /*
     * Splash delay time
     * */
    public long getSplashDelayInMs() {
        if (mApplication != null) {
            return SharedPreference.getLong(mApplication, SPLASH_DELAY_IN_MS, DEFAULT_SPLASH_DELAY_IN_MS);
        }
        return DEFAULT_SPLASH_DELAY_IN_MS;
    }

    /*
     * Fake progress delay time
     * */
    public long getInterOPAProgressDelayInMs() {
        if (mApplication != null) {
            return SharedPreference.getLong(mApplication, INTER_OPA_PROGRESS_DELAY_IN_MS, DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS);
        }
        return DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS;
    }

}
