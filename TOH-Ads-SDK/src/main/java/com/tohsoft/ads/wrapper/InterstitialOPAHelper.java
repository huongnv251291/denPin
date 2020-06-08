package com.tohsoft.ads.wrapper;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.ads.models.AdsConfig;
import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.AdsModules;
import com.tohsoft.ads.admob.AdmobAdvertisements;
import com.tohsoft.ads.fan.FanAdvertisements;
import com.utility.DebugLog;

/**
 * Created by Phong on 01/05/2019.
 */
public class InterstitialOPAHelper {
    private int MAX_TRY_LOAD_ADS = 3;
    private final AdsConfig mAdsConfig;
    private final String[] mAdsIds;
    private final Context mContext;

    private InterstitialOPAListener mListener;
    private InterstitialAd mInterstitialOpenApp;
    private com.facebook.ads.InterstitialAd mFanInterstitialOpenApp;
    private CountDownTimer mCounter;
    private View mProgressLoading;

    private long DELAY_SPLASH; // Splash timeout
    private long DELAY_PROGRESS; // Fake progress timeout
    private volatile boolean mIsInterstitialOpenAppShownOnStartup = false;
    private volatile boolean mIsInterstitialOpenAppShownOnQuit = false;
    private volatile boolean mIsPause = false;
    private volatile boolean mIsCounting = false;

    private String mCurrentAdsId;
    private boolean useFanAdNetwork;
    private int mAdsPosition = 0;
    private int mTryReloadAds = 0;

    public InterstitialOPAHelper(@NonNull AdsConfig adsConfig, View progressLoading, InterstitialOPAListener listener) {
        this.mAdsConfig = adsConfig;
        this.mContext = mAdsConfig.getContext();
        this.mAdsIds = mAdsConfig.getAdsIds();

        this.mProgressLoading = progressLoading;
        this.mListener = listener;

        if (mAdsIds != null && mAdsIds.length > 3) {
            MAX_TRY_LOAD_ADS = mAdsIds.length;
        }

        DELAY_SPLASH = mAdsConfig.getSplashDelayTimeInMs();
        DELAY_PROGRESS = mAdsConfig.getOPAProgressDelayTimeInMs();
        DebugLog.loge("\nDELAY_SPLASH: " + DELAY_SPLASH + " - DELAY_PROGRESS: " + DELAY_PROGRESS);
    }

    public void initInterstitialOpenApp() {
        getAdsId();
        if (useFanAdNetwork) {
            initFanInterstitial();
        } else {
            initAdmobInterstitial();
        }
        startAdOPALoadingCounter();
    }

    private void getAdsId() {
        if (mAdsPosition >= mAdsIds.length) {
            mAdsPosition = 0;
        }
        mCurrentAdsId = mAdsIds[mAdsPosition];
        useFanAdNetwork = mCurrentAdsId.startsWith(AdsConstants.FAN_ID_PREFIX);

        // Destroy previous Ads instance
        destroy();
    }

    public void setListener(InterstitialOPAListener listener) {
        this.mListener = listener;
    }

    private void initAdmobInterstitial() {
        if (!AdsModules.getInstance().canShowOPA()) {
            return;
        }
        if (mInterstitialOpenApp != null && isCounting()) {
            return;
        }

        AdListener adListener = new AdListener() {

            @Override
            public void onAdFailedToLoad(int code) {
                super.onAdFailedToLoad(code);
                DebugLog.logd("\n---\n[Admob - Interstitial OPA] adsId: " + mCurrentAdsId + "\nError Code: " + code + "\n---");
                mInterstitialOpenApp = null;
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initInterstitialOpenApp();
                } else {
                    mTryReloadAds = 0; // Reset flag
                    mAdsPosition = 0; // Reset flag
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (mIsInterstitialOpenAppShownOnQuit) {
                    mIsInterstitialOpenAppShownOnQuit = false; // Reset flag
                    if (mListener != null) {
                        mListener.showExitDialog();
                    }
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (mIsInterstitialOpenAppShownOnStartup) {
                    mIsInterstitialOpenAppShownOnStartup = false; // Reset flag
                    if (mListener != null) {
                        mListener.onAdOPACompleted();
                    }
                    if (mProgressLoading != null) {
                        mProgressLoading.setVisibility(View.GONE);
                    }
                }
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.ADMOB_ID_PREFIX, "");
        if (mAdsConfig.isTestMode()) {
            adsId = AdsConstants.interstitial_test_id;
        }
        mInterstitialOpenApp = AdmobAdvertisements.initInterstitialAd(mContext, adsId, adListener);
    }

    private void initFanInterstitial() {
        if (!AdsModules.getInstance().canShowOPA()) {
            return;
        }
        if (mFanInterstitialOpenApp != null && isCounting()) {
            return;
        }

        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (mIsInterstitialOpenAppShownOnQuit) {
                    mIsInterstitialOpenAppShownOnQuit = false; // Reset flag
                    if (mListener != null) {
                        mListener.showExitDialog();
                    }
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (mIsInterstitialOpenAppShownOnStartup) {
                    mIsInterstitialOpenAppShownOnStartup = false; // Reset flag
                    if (mListener != null) {
                        mListener.onAdOPACompleted();
                    }
                    if (mProgressLoading != null) {
                        mProgressLoading.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                DebugLog.logd("\n[FAN - Interstitial OPA] PlacementId: " + ad.getPlacementId() + "\nErrorCode: "
                        + adError.getErrorCode() + "\nErrorMessage: " + adError.getErrorMessage());
                mFanInterstitialOpenApp = null;
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initInterstitialOpenApp();
                } else {
                    mTryReloadAds = 0; // Reset flag
                    mAdsPosition = 0; // Reset flag
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.FAN_ID_PREFIX, "");
        mFanInterstitialOpenApp = FanAdvertisements.initInterstitialAd(mContext.getApplicationContext(), adsId, listener);
    }

    private void startAdOPALoadingCounter() {
        if (mIsCounting) {
            return;
        }
        if (mProgressLoading != null) {
            mProgressLoading.setVisibility(View.VISIBLE);
        }

        mIsCounting = true;
        final long checkInterval = 100;
        final long counterTimeout = DELAY_SPLASH + (mProgressLoading != null ? DELAY_PROGRESS : 0);
        mCounter = new CountDownTimer(counterTimeout, checkInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Check if ad OPA loaded
                if (useFanAdNetwork ?
                        (mFanInterstitialOpenApp == null || (mFanInterstitialOpenApp.isAdLoaded() && !mIsPause)) :
                        (mInterstitialOpenApp == null || (mInterstitialOpenApp.isLoaded() && !mIsPause))) {
                    mCounter.cancel();// stop mCounter & finish
                    onAdOPALoadingCounterFinish();
                    return;
                }

                long passedTimeMS = counterTimeout - millisUntilFinished;
                if (passedTimeMS >= DELAY_SPLASH) {
                    if (mListener != null) {
                        mListener.hideSplash();
                    }
                }
            }

            @Override
            public void onFinish() {
                onAdOPALoadingCounterFinish();
            }
        };
        mCounter.start();
    }

    private void onAdOPALoadingCounterFinish() {
        mIsCounting = false;
        mIsInterstitialOpenAppShownOnStartup = show();
        if (!mIsInterstitialOpenAppShownOnStartup) { // Ads not showing
            if (mListener != null) {
                mListener.onAdOPACompleted();
            }
            if (mProgressLoading != null) {
                mProgressLoading.setVisibility(View.GONE);
            }
        }

        // Stop splash
        if (mListener != null) {
            mListener.hideSplash();
        }
    }

    public void checkAndShowFullScreenQuitApp() {
        mIsInterstitialOpenAppShownOnQuit = show();
        if (!mIsInterstitialOpenAppShownOnQuit && mListener != null) {
            mListener.showExitDialog();
        }
    }

    public boolean isCounting() {
        return mIsCounting;
    }

    public boolean isLoaded() {
        if (useFanAdNetwork) {
            return mFanInterstitialOpenApp != null && mFanInterstitialOpenApp.isAdLoaded();
        } else {
            return mInterstitialOpenApp != null && mInterstitialOpenApp.isLoaded();
        }
    }

    public boolean show() {
        try {
            if (isLoaded() && AdsModules.getInstance().canShowOPA() && !mIsPause) {
                if (useFanAdNetwork) {
                    mFanInterstitialOpenApp.show();
                } else {
                    mInterstitialOpenApp.show();
                }
                AdsModules.getInstance().setLastTimeOPAShow();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onResume() {
        mIsPause = false;
    }

    public void onPause() {
        mIsPause = true;
    }

    /*
     * Destroy references
     * */
    public void destroy() {
        mInterstitialOpenApp = null;
        if (mFanInterstitialOpenApp != null) {
            mFanInterstitialOpenApp.destroy();
            mFanInterstitialOpenApp = null;
        }
    }

    public interface InterstitialOPAListener {
        void hideSplash();

        void showExitDialog();

        void onAdOPACompleted(); // Check permissions, request permissions or show dialogs at here
    }

}
