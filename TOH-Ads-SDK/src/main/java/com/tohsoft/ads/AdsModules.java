package com.tohsoft.ads;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.Utils;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.tohsoft.ads.models.AdsConfig;
import com.tohsoft.ads.wrapper.AdViewWrapper;
import com.tohsoft.ads.wrapper.InterstitialAdWrapper;
import com.utility.SharedPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public
/**
 * Created by PhongNX on 6/8/2020.
 */
@SuppressLint("StaticFieldLeak")
class AdsModules {
    private static final String LAST_TIME_INTER_OPA_SHOWED = "last_time_interstitial_opa_showed";
    private static final String FREQ_INTER_OPA_IN_MILLISECONDS = "freq_interstitial_opa_in_ms";
    private static final String SPLASH_DELAY_IN_MS = "splash_delay_in_ms";
    private static final String INTER_OPA_PROGRESS_DELAY_IN_MS = "inter_opa_progress_delay_in_ms";

    public static final long DEFAULT_FREQ_CAP_INTER_OPA_IN_MS = 15 * 60 * 1000; // 15 minutes
    public static final long DEFAULT_SPLASH_DELAY_IN_MS = 3000; // 3 seconds
    public static final long DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS = 2000; // 2 seconds

    public static AdViewWrapper sBannerBottom;
    public static AdViewWrapper sBannerEmptyScreen;
    public static InterstitialAdWrapper sPromotionAds;

    private static AdsModules sAdsModules;
    private Application mApplication;
    private boolean isFullVersion;

    private List<String> mTestDevices = new ArrayList<>();

    public static AdsModules getInstance() {
        if (sAdsModules == null) {
            sAdsModules = new AdsModules();
        }
        return sAdsModules;
    }

    AdsModules() {
    }

    public AdsModules init(Application application) {
        mApplication = application;
        AudienceNetworkAds.initialize(application);
        MobileAds.initialize(application);
        Utils.init(application);
        return sAdsModules;
    }

    public AdsModules addTestDevices(Collection<String> collection) {
        if (collection != null) {
            mTestDevices.addAll(collection);
        }
        return sAdsModules;
    }

    public AdsModules addTestDevices(String... devicesHash) {
        if (devicesHash != null) {
            mTestDevices.addAll(Arrays.asList(devicesHash));
        }
        return sAdsModules;
    }

    public AdsModules setFullVersion(boolean fullVersion) {
        isFullVersion = fullVersion;
        return sAdsModules;
    }

    public List<String> getTestDevices() {
        return mTestDevices;
    }

    public boolean isFullVersion() {
        return isFullVersion;
    }

    public Context getContext() {
        return mApplication;
    }

    public void showBannerBottom(AdsConfig adsConfig, ViewGroup container) {
        if (!isFullVersion && container != null) {
            if (sBannerBottom == null) {
                sBannerBottom = new AdViewWrapper(adsConfig);
            }
            sBannerBottom.initBanner(container);
        } else if (container != null) {
            container.removeAllViews();
        }
    }

    public void showBannerEmptyScreen(AdsConfig adsConfig, ViewGroup container) {
        if (!isFullVersion && container != null) {
            if (sBannerEmptyScreen == null) {
                sBannerEmptyScreen = new AdViewWrapper(adsConfig);
            }
            sBannerEmptyScreen.initMediumBanner(container);
        } else if (container != null) {
            container.removeAllViews();
        }
    }

    public void showPromotionAdsView(AdsConfig adsConfig, View viewPromotionAds) {
        if (!isFullVersion) {
            if (sPromotionAds == null) {
                sPromotionAds = new InterstitialAdWrapper(adsConfig);
            }
            sPromotionAds.initAds(viewPromotionAds);
        }
    }

    public void showPromotionAds() {
        if (sPromotionAds != null) {
            sPromotionAds.show();
        }
    }

    public AdsConfig getAdsConfig(String mode, String[] admobIds, String[] fanIds, boolean testMode) {
        return new AdsConfig.Builder(mApplication)
                .setTestMode(testMode)
                .setMode(mode)
                .setAdmobIds(admobIds)
                .setFanIds(fanIds)
                .build();
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
     *
     * */
    public void setLastTimeOPAShow() {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, LAST_TIME_INTER_OPA_SHOWED, System.currentTimeMillis());
        }
    }

    public void setFreqInterOPAInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, FREQ_INTER_OPA_IN_MILLISECONDS, time);
        }
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

    public void setSplashDelayInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, SPLASH_DELAY_IN_MS, time);
        }
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

    public void setInterOPAProgressDelayInMs(long time) {
        if (mApplication != null) {
            SharedPreference.setLong(mApplication, INTER_OPA_PROGRESS_DELAY_IN_MS, time);
        }
    }

    /*
     *
     * */
    public void destroyStaticAds() {
        if (sBannerBottom != null) {
            sBannerBottom.destroy();
            sBannerBottom = null;
        }
        if (sBannerEmptyScreen != null) {
            sBannerEmptyScreen.destroy();
            sBannerEmptyScreen = null;
        }
        if (sPromotionAds != null) {
            sPromotionAds.destroy();
            sPromotionAds = null;
        }
    }
}
