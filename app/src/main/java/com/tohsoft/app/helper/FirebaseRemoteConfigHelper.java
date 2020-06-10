package com.tohsoft.app.helper;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.tohsoft.ads.AdsConfig;
import com.tohsoft.ads.AdsModule;
import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.utility.DebugLog;
import com.utility.SharedPreference;

public class FirebaseRemoteConfigHelper {
    private static final String REMOTE_PRO_APP_URL = "pro_app_url";
    private static final String REMOTE_ADS_ID_LIST = "ads_id_list";
    private static final String REMOTE_CUSTOM_ADS_ID_LIST = "custom_ads_id_list";
    private static final String REMOTE_FREQ_CAP_INTER_OPA_IN_MINUTE = "freq_cap_inter_opa_in_minute";
    private static final String REMOTE_SPLASH_DELAY_IN_MS = "splash_delay_in_ms";
    private static final String REMOTE_INTER_OPA_PROGRESS_DELAY_IN_MS = "inter_opa_progress_delay_in_ms";

    private static final String DEFAULT_ADS_ID_LIST = "ADMOB-0, ADMOB-1, ADMOB-2";
    public static final long DEFAULT_FREQ_CAP_INTER_OPA_IN_MS = 15 * 60 * 1000; // 15 minutes
    public static final long DEFAULT_SPLASH_DELAY_IN_MS = 3000; // 3 seconds
    public static final long DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS = 2000; // 2 seconds

    private static FirebaseRemoteConfigHelper firebaseRemoteConfigHelper;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Context mContext;
    private boolean isFetching = false;

    public static FirebaseRemoteConfigHelper getInstance() {
        if (firebaseRemoteConfigHelper == null) {
            firebaseRemoteConfigHelper = new FirebaseRemoteConfigHelper();
        }
        return firebaseRemoteConfigHelper;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private boolean initializeApp(Context context) {
        try {
            mContext = context.getApplicationContext();
            FirebaseApp.initializeApp(context);
            long cacheExpiration = 3600;
            if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
                cacheExpiration = 0;
            }
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(cacheExpiration)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            return true;
        } catch (Exception e) {
            DebugLog.loge(e);
            return false;
        }
    }

    public void fetchRemoteData(Context context) {
        if (mFirebaseRemoteConfig == null) {
            if (!initializeApp(context)) {
                return;
            }
        }
        if (isFetching) {
            return;
        }
        isFetching = true;
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    isFetching = false;
                    if (task.isSuccessful()) {
                        DebugLog.loge("Fetch Successful");
                        setAdsConfigs();
                    } else {
                        DebugLog.loge("Fetch Failed");
                    }
                });
    }

    private void setAdsConfigs() {
        if (BuildConfig.SHOW_AD) {
            SharedPreference.setString(mContext, REMOTE_ADS_ID_LIST, getAdsIdList());

            AdsConfig.getInstance()
                    .setFreqInterOPAInMs(getFreqInterOPAInMs())
                    .setSplashDelayInMs(getSplashDelayInMs())
                    .setInterOPAProgressDelayInMs(getInterOPAProgressDelayInMs());

            AdsModule.getInstance()
                    .setAdsIdListConfig(getAdsIdList())
                    .setCustomAdsIdListConfig(getCustomAdsIdList());

            DebugLog.logd("setAdsConfigs:" +
                    "\nAdsIdList: " + getAdsIdList() +
                    "\nCustomAdsIdList: " + getCustomAdsIdList() +
                    "\nFreqInterOPAInMs: " + getFreqInterOPAInMs() +
                    "\nSplashDelayInMs: " + getSplashDelayInMs() +
                    "\nInterOPAProgressDelayInMs: " + getInterOPAProgressDelayInMs()
            );
        }
    }

    public boolean getProVersionEnable() {
        if (mFirebaseRemoteConfig != null) {
            return !TextUtils.isEmpty(mFirebaseRemoteConfig.getString(REMOTE_PRO_APP_URL));
        }
        return false;
    }

    public String getProAppUrl() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getString(REMOTE_PRO_APP_URL);
        }
        return "";
    }

    public String getAdsIdList() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getString(REMOTE_ADS_ID_LIST);
        }
        return SharedPreference.getString(mContext, REMOTE_ADS_ID_LIST, DEFAULT_ADS_ID_LIST);
    }

    public String getCustomAdsIdList() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getString(REMOTE_CUSTOM_ADS_ID_LIST);
        }
        return "";
    }

    public long getFreqInterOPAInMs() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getLong(REMOTE_FREQ_CAP_INTER_OPA_IN_MINUTE) * 60 * 1000;
        }
        return DEFAULT_FREQ_CAP_INTER_OPA_IN_MS;
    }

    public long getSplashDelayInMs() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getLong(REMOTE_SPLASH_DELAY_IN_MS);
        }
        return DEFAULT_SPLASH_DELAY_IN_MS;
    }

    public long getInterOPAProgressDelayInMs() {
        if (mFirebaseRemoteConfig != null) {
            return mFirebaseRemoteConfig.getLong(REMOTE_INTER_OPA_PROGRESS_DELAY_IN_MS);
        }
        return DEFAULT_INTER_OPA_PROGRESS_DELAY_IN_MS;
    }
}
