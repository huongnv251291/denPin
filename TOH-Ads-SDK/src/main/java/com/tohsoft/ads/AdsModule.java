package com.tohsoft.ads;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.tohsoft.ads.models.AdsId;
import com.tohsoft.ads.models.AdsType;
import com.tohsoft.ads.utils.AdDebugLog;
import com.tohsoft.ads.utils.AdsUtils;
import com.tohsoft.ads.wrapper.AdViewWrapper;
import com.tohsoft.ads.wrapper.InterstitialAdWrapper;
import com.tohsoft.ads.wrapper.InterstitialOPAHelper;
import com.tohsoft.ads.wrapper.NativeAdViewWrapper;
import com.utility.SharedPreference;
import com.utility.UtilsLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by PhongNX on 6/8/2020.
 */
@SuppressWarnings("WeakerAccess")
@SuppressLint("StaticFieldLeak")
public class AdsModule {
    private static final String GENERAL_CONFIG_ADS_ID_LIST = "ads_id_list";

    private static AdsModule sAdsModule;
    public static AdViewWrapper sBannerBottom;
    public static AdViewWrapper sBannerEmptyScreen;
    public static NativeAdViewWrapper sNativeAdViewWrapper;
    public static InterstitialAdWrapper sPromotionAds;
    public AdViewWrapper mBannerExitDialog;
    public InterstitialOPAHelper mInterstitialOPAHelper;

    private Application mApplication;
    private List<String> mAdsIdConfigList = new ArrayList<>();
    private Map<AdsType, List<String>> mCustomAdsIdConfig = new HashMap<>();
    private AdsId mAdmobAdsId;
    private AdsId mFanAdsId;
    private AdsId mAdsId;

    private boolean mIgnoreDestroyStaticAd;

    public static AdsModule getInstance() {
        if (sAdsModule == null) {
            sAdsModule = new AdsModule();
        }
        return sAdsModule;
    }

    public AdsId getAdsId() {
        return mAdsId;
    }

    public Context getContext() {
        return mApplication;
    }

    private AdsModule() {
    }

    /*
     * Set Application Context & initialize modules
     * */
    public AdsModule init(@NonNull Application application) {
        mApplication = application;
        AudienceNetworkAds.initialize(application);
        MobileAds.initialize(application);
        Utils.init(application);
        mAdsIdConfigList = generateAdsIdListConfig(SharedPreference.getString(mApplication, GENERAL_CONFIG_ADS_ID_LIST, ""));
        return sAdsModule;
    }

    /*
     * Set and generate Ads id from assets file
     * */
    public AdsModule setResourceAdsId(@Nullable String admobAssetsFileName, @Nullable String fanAssetsFileName) {
        checkCondition();
        mAdmobAdsId = AdsUtils.readIdsFromAssetsFile(mApplication, admobAssetsFileName);
        mFanAdsId = AdsUtils.readIdsFromAssetsFile(mApplication, fanAssetsFileName);
        if (!UtilsLib.isEmptyList(mAdsIdConfigList) && (mAdmobAdsId != null || mFanAdsId != null)) {
            mAdsId = AdsUtils.mixAdsIdWithConfig(mAdmobAdsId, mFanAdsId, mAdsIdConfigList);

            refreshAdsIdIfNeeded();
        }
        return sAdsModule;
    }

    /*
     * This method must be called before setCustomAdsIdListConfig() is called
     * */
    public AdsModule setAdsIdListConfig(String adsIdListConfig) {
        checkCondition();
        if (!TextUtils.isEmpty(adsIdListConfig)) {
            AdDebugLog.logd("\n---------------\nadsIdListConfig: " + adsIdListConfig + "\n---------------");
            SharedPreference.setString(mApplication, GENERAL_CONFIG_ADS_ID_LIST, adsIdListConfig);
            mAdsIdConfigList = generateAdsIdListConfig(adsIdListConfig);
            if (mAdmobAdsId != null || mFanAdsId != null) {
                mAdsId = AdsUtils.mixAdsIdWithConfig(mAdmobAdsId, mFanAdsId, mAdsIdConfigList);

                refreshAdsIdIfNeeded();
            }
        }
        return sAdsModule;
    }

    /*
     * This method must call after setAdsIdListConfig() is called
     * */
    public AdsModule setCustomAdsIdListConfig(String customAdsIdJsonValue) {
        checkCondition();
        if (!TextUtils.isEmpty(customAdsIdJsonValue)) {
            generateCustomAdsIdList(customAdsIdJsonValue);
            mixCustomAdsIdList();
        }
        return sAdsModule;
    }

    /*
     * Check if Application not set, it will throw an Exception
     * */
    private void checkCondition() {
        if (mApplication == null) {
            throw new NullPointerException("Application is NULL, you must to call init() first at onCreate() of Application class");
        }
    }

    /*
     * Parse custom Ads id from JSONObject
     * (customAdsIdJsonValue will look like: { 	"std_banner_list" : "ADMOB-1,ADMOB-0,ADMOB-2"  })
     * */
    private void generateCustomAdsIdList(String customAdsIdJsonValue) {
        try {
            JSONObject jsonObject = new JSONObject(customAdsIdJsonValue);
            Iterator<String> iterator = jsonObject.keys();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    AdsType adsType = null;
                    if (key.toLowerCase().startsWith("std_banner")) {
                        adsType = AdsType.STD_BANNER;
                    } else if (key.toLowerCase().startsWith("banner_exit_dialog")) {
                        adsType = AdsType.BANNER_EXIT_DIALOG;
                    } else if (key.toLowerCase().startsWith("banner_empty_screen")) {
                        adsType = AdsType.BANNER_EMPTY_SCREEN;
                    } else if (key.toLowerCase().startsWith("interstitial_opa")) {
                        adsType = AdsType.INTERSTITIAL_OPA;
                    } else if (key.toLowerCase().startsWith("interstitial_gift")) {
                        adsType = AdsType.INTERSTITIAL_GIFT;
                    }

                    // Put custom Ads id to map
                    if (adsType != null && !UtilsLib.isEmptyList(generateAdsIdListConfig(jsonObject.getString(key)))) {
                        mCustomAdsIdConfig.put(adsType, generateAdsIdListConfig(jsonObject.getString(key)));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * Mix Ads id with custom Ads id
     * */
    private void mixCustomAdsIdList() {
        if (mCustomAdsIdConfig != null && !mCustomAdsIdConfig.isEmpty()) {
            for (AdsType adsType : mCustomAdsIdConfig.keySet()) {
                if (mCustomAdsIdConfig.get(adsType) != null) {
                    AdsUtils.mixCustomAdsIdConfig(mAdsId, mAdmobAdsId, mFanAdsId, adsType, mCustomAdsIdConfig.get(adsType));
                }
            }

            refreshAdsIdIfNeeded();
        }
    }

    /*
     * Check and set Ads id for existed wrapper
     * */
    private void refreshAdsIdIfNeeded() {
        if (mAdsId != null) {
            if (sBannerBottom != null) {
                sBannerBottom.setAdsId(mAdsId.std_banner);
            }
            if (sBannerEmptyScreen != null) {
                sBannerEmptyScreen.setAdsId(mAdsId.banner_empty_screen);
            }
            if (sPromotionAds != null) {
                sPromotionAds.setAdsId(mAdsId.interstitial_gift);
            }
            if (mBannerExitDialog != null) {
                mBannerExitDialog.setAdsId(mAdsId.banner_exit_dialog);
            }
            if (mInterstitialOPAHelper != null) {
                mInterstitialOPAHelper.setAdsId(mAdsId.interstitial_opa);
            }
        }
    }

    /*
     * Generate Ads id list with a Firebase value
     * (adsIdList will look like: FAN-0, ADMOB-0, ADMOB-1)
     * */
    @NonNull
    private List<String> generateAdsIdListConfig(String adsIdList) {
        if (adsIdList.contains(",")) {
            String[] config = adsIdList.split(",");
            return Arrays.asList(config);
        }
        return new ArrayList<>();
    }

    /*
     * Generate Banner Exit Dialog - AdViewWrapper instance (Medium Banner)
     * Auto get Ads id from config
     * */
    public AdViewWrapper getBannerExitDialog() {
        if (mAdsId != null && mAdsId.banner_exit_dialog != null) {
            mBannerExitDialog = new AdViewWrapper(mApplication, mAdsId.banner_exit_dialog);
            return mBannerExitDialog;
        }
        return null;
    }

    /*
     * Generate InterstitialOPAHelper instance (Interstitial OPA flow)
     * Auto get Ads id from config
     *
     * @param:
     * View progressLoading: fake progress loading view
     * InterstitialOPAListener listener: Call back for each state of the OPA flow
     * */
    public InterstitialOPAHelper getInterstitialOPAHelper(View progressLoading, InterstitialOPAHelper.InterstitialOPAListener listener) {
        if (mAdsId != null && mAdsId.interstitial_opa != null) {
            mInterstitialOPAHelper = new InterstitialOPAHelper(mApplication, mAdsId.interstitial_opa, progressLoading, listener);
            return mInterstitialOPAHelper;
        }
        return null;
    }

    /*
     * Generate standard bottom Banner - sBannerBottom static instance (Adaptive Banner)
     * Auto get Ads id from config
     *
     * @param:
     * ViewGroup container: The container that AdView will be added to
     * */
    public void showBannerBottom(ViewGroup container) {
        if (!AdsConfig.getInstance().isFullVersion() && container != null && mAdsId != null && mAdsId.std_banner != null) {
            if (sBannerBottom == null) {
                sBannerBottom = new AdViewWrapper(mApplication, mAdsId.std_banner);
            }
            sBannerBottom.initBottomBanner(container);
        } else if (container != null) {
            container.removeAllViews();
        }
    }

    /*
     * Generate Banner Empty Screen - sBannerEmptyScreen static instance (Medium Banner)
     * Auto get Ads id from config
     *
     * @param:
     * ViewGroup container: The container that AdsView will be added to
     * */
    public void showBannerEmptyScreen(ViewGroup container) {
        if (!AdsConfig.getInstance().isFullVersion() && container != null && mAdsId != null && mAdsId.banner_empty_screen != null) {
            if (sBannerEmptyScreen == null) {
                sBannerEmptyScreen = new AdViewWrapper(mApplication, mAdsId.banner_empty_screen);
            }
            sBannerEmptyScreen.initMediumBanner(container);
        } else if (container != null) {
            container.removeAllViews();
        }
    }

    /*
     * Generate Native AdView - sNativeAdViewWrapper static instance
     *
     * @param:
     * ViewGroup container: The container that AdsView will be added to
     * String[] adsId: array ids for Native AdView
     * */
    public void showNativeAdView(ViewGroup container, String[] adsId) {
        if (!AdsConfig.getInstance().isFullVersion() && container != null && adsId != null) {
            if (sNativeAdViewWrapper == null) {
                sNativeAdViewWrapper = new NativeAdViewWrapper(adsId);
            }
            sNativeAdViewWrapper.refreshAd(mApplication, container);
        } else if (container != null) {
            container.removeAllViews();
        }
    }

    /*
     * Generate Promotion Ads - sPromotionAds static instance (Interstitial Ads)
     * Auto get Ads id from config
     *
     * @param:
     * View viewPromotionAds: Gift icon to show or hide when Interstitial is loaded or failed
     * */
    public void showPromotionAdsView(View viewPromotionAds) {
        if (!AdsConfig.getInstance().isFullVersion() && mAdsId != null && mAdsId.interstitial_gift != null) {
            if (sPromotionAds == null) {
                sPromotionAds = new InterstitialAdWrapper(mApplication, mAdsId.interstitial_gift);
            }
            sPromotionAds.initAds(viewPromotionAds);
        } else if (viewPromotionAds != null) {
            viewPromotionAds.setVisibility(View.GONE);
        }
    }

    /*
     * Call this method when the user click on the Gift icon (View viewPromotionAds in method showPromotionAdsView()),
     * it will display Interstitial Ads if it has been successfully loaded before
     *
     * After that, InterstitialAdWrapper (sPromotionAds instance) will auto reload Ads and show Gift icon when Ads is loaded
     * */
    public void showPromotionAds() {
        if (sPromotionAds != null) {
            sPromotionAds.show();
        }
    }

    public void setIgnoreDestroyStaticAd(boolean ignoreDestroyStaticAd) {
        this.mIgnoreDestroyStaticAd = ignoreDestroyStaticAd;
    }

    /*
     *
     * */
    public void destroyStaticAds() {
        if (mIgnoreDestroyStaticAd) {
            mIgnoreDestroyStaticAd = false;
            return;
        }
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
