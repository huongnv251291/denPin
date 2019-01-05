package com.tohsoft.app.utils.ads;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;

/**
 * Created by Phong on 01/05/2019.
 */
public class InterstitialOPAHelper implements AdsId {
    private static final int MAX_TRY_LOAD_ADS = 3;
    private Context mContext;
    private final InterstitialOPAListener mListener;
    private InterstitialAd mInterstitialOpenApp;
    private CountDownTimer mCounter;
    private View mProgressLoading;

    private static final int DELAY_SPLASH = 3000; // Splash timeout
    private static final int DELAY_TRY_LOAD_ADS = 0; // Fake progress timeout
    private volatile boolean mIsInterstitialOpenAppShownOnStartup = false;
    private volatile boolean mIsInterstitialOpenAppShownOnQuit = false;
    private volatile boolean mIsStop = false;
    private int mTryToReloadInterstitialOPA = 0;
    private int mAdsPosition = 0;

    public InterstitialOPAHelper(Context context, View progressLoading, InterstitialOPAListener listener) {
        this.mContext = context;
        this.mProgressLoading = progressLoading;
        this.mListener = listener;
    }

    public void initInterstitialOpenApp() {
        initInterstitialOpenApp(interstitialOPA[0]);
        startAdOPALoadingCounter();
    }

    private void initInterstitialOpenApp(String adsId) {
        if (BuildConfig.SHOW_AD) {
            mInterstitialOpenApp = Advertisements.initInterstitialAd(mContext, adsId, new AdListener() {

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    DebugLog.logd("\n---\nadsId: " + adsId + "\nmTryToReloadInterstitialOPA: " + mTryToReloadInterstitialOPA + "\n---");
                    mInterstitialOpenApp = null;
                    if (mTryToReloadInterstitialOPA < MAX_TRY_LOAD_ADS) {
                        mTryToReloadInterstitialOPA++;
                        mAdsPosition++;

                        if (mAdsPosition >= interstitialOPA.length) {
                            mAdsPosition = 0;
                        }
                        initInterstitialOpenApp(AdsId.interstitialOPA[mAdsPosition]);
                    } else {
                        mTryToReloadInterstitialOPA = 0; // Reset flag
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
            });
        }
    }

    private void startAdOPALoadingCounter() {
        if (mProgressLoading != null) {
            mProgressLoading.setVisibility(View.VISIBLE);
        }

        long checkInterval = 100;//
        long counterTimeout = DELAY_SPLASH + DELAY_TRY_LOAD_ADS;
        mCounter = new CountDownTimer(counterTimeout, checkInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Check if ad OPA loaded
                if (mInterstitialOpenApp == null || (mInterstitialOpenApp.isLoaded() && !mIsStop)) {
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
        if (!mIsStop && mInterstitialOpenApp != null && mInterstitialOpenApp.isLoaded()) {
            mIsInterstitialOpenAppShownOnStartup = true;
            mInterstitialOpenApp.show();
        } else { // Ads not showing
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
        if (mInterstitialOpenApp != null && mInterstitialOpenApp.isLoaded()) {
            mIsInterstitialOpenAppShownOnQuit = true;
            mInterstitialOpenApp.show();
        } else if (mListener != null) {
            mListener.showExitDialog();
        }
    }

    public void onResume() {
        mIsStop = false;
    }

    public void onStop() {
        mIsStop = true;
    }

    public interface InterstitialOPAListener {
        void hideSplash();

        void showExitDialog();

        void onAdOPACompleted(); // Check permissions, request permissions or show dialogs at here
    }

}
