package com.tohsoft.app.utils.ads;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;

/**
 * Created by Phong on 11/16/2018.
 */

public class InterstitialAdWrapper implements AdsId {
    private static final int MAX_TRY_LOAD_ADS = 3;
    private InterstitialAd mInterstitialAd;
    private View mGiftView;
    private int mTryReloadAds = 0;
    private int mAdsPosition = 0;

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public void initAds(Context context, View giftView) {
        if (context == null || !BuildConfig.SHOW_AD) {
            return;
        }
        mGiftView = giftView;

        if (mInterstitialAd != null && mGiftView != null) {
            if (mInterstitialAd.isLoaded()) {
                mGiftView.setVisibility(View.VISIBLE);
                DebugLog.logd("Show Gift button");
            } else {
                mGiftView.setVisibility(View.GONE);
            }
            return;
        }

        if (mGiftView != null) {
            mGiftView.setVisibility(View.GONE);
        }
        AdListener adListener = new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                    DebugLog.logd("Hide Gift button");
                }
                mInterstitialAd = null;
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    initAds(context, mGiftView);
                    mTryReloadAds++;
                    mAdsPosition++;
                    DebugLog.logd("Try load InterstitialAd: " + mTryReloadAds);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mTryReloadAds = 0;
                if (mGiftView != null) {
                    DebugLog.logd("Show Gift button");
                    mGiftView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd = null;
                initAds(context, mGiftView);
            }
        };

        if (mAdsPosition >= banners.length) {
            mAdsPosition = 0;
        }
        mInterstitialAd = Advertisements.initInterstitialAd(context.getApplicationContext(), interstitialGift[mAdsPosition], adListener);
    }

    public void show() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    /*
     * Destroy references
     * */
    public void destroy() {
        mGiftView = null;
        mInterstitialAd = null;
    }
}
