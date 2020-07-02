package com.tohsoft.ads.wrapper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.ads.AdsConfig;
import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.admob.AdmobAdvertisements;
import com.tohsoft.ads.fan.FanAdvertisements;
import com.utility.DebugLog;
import com.utility.UtilsLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phong on 11/16/2018.
 */

public class InterstitialAdWrapper {
    private int MAX_TRY_LOAD_ADS = 3;
    private final List<String> mAdsIds = new ArrayList<>();
    private final Context mContext;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd mFanInterstitialAd;
    private AdListener mAdListener;
    private View mGiftView;
    private int mTryReloadAds = 0;

    private String mCurrentAdsId;
    private boolean useFanAdNetwork;
    private int mAdsPosition = 0;

    public InterstitialAdWrapper(@NonNull Context context, @NonNull List<String> adsId) {
        this.mContext = context;
        this.mAdsIds.addAll(adsId);

        if (mAdsIds.size() > 3) {
            MAX_TRY_LOAD_ADS = mAdsIds.size();
        }
    }

    public void setAdsId(List<String> adsId) {
        if (adsId != null) {
            this.mAdsIds.clear();
            this.mAdsIds.addAll(adsId);
        }
    }

    public void setAdListener(AdListener adListener) {
        this.mAdListener = adListener;
    }

    public void initAds(View giftView) {
        getAdsId();
        if (TextUtils.isEmpty(mCurrentAdsId)) {
            DebugLog.loge("mCurrentAdsId is NULL");
            return;
        }
        if (useFanAdNetwork) {
            initFanAds(giftView);
        } else {
            initAdmobAds(giftView);
        }
    }

    private void getAdsId() {
        if (UtilsLib.isEmptyList(mAdsIds)) {
            DebugLog.loge("mAdsIds is EMPTY");
            return;
        }
        if (mAdsPosition >= mAdsIds.size()) {
            mAdsPosition = 0;
        }
        mCurrentAdsId = mAdsIds.get(mAdsPosition);
        boolean useFanAdNetwork = mCurrentAdsId.startsWith(AdsConstants.FAN_ID_PREFIX);

        if (this.useFanAdNetwork != useFanAdNetwork) {
            // Destroy previous Ads instance
            destroy();
        }
        this.useFanAdNetwork = useFanAdNetwork;
    }

    private void initAdmobAds(View giftView) {
        if (mContext == null) {
            return;
        }
        mGiftView = giftView;

        if (mInterstitialAd != null) {
            if (mAdListener != null && mInterstitialAd.isLoaded()) {
                mAdListener.onAdLoaded();
            }
            if (mGiftView != null) {
                if (mInterstitialAd.isLoaded()) {
                    mGiftView.setVisibility(View.VISIBLE);
                    DebugLog.logd("Show Gift button");
                } else {
                    mGiftView.setVisibility(View.GONE);
                }
            }
            return;
        }

        if (mGiftView != null) {
            mGiftView.setVisibility(View.GONE);
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                    DebugLog.logd("Hide Gift button");
                }
                mInterstitialAd = null;
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(errorCode);
                }
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initAds(mGiftView);
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
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                }
                if (mAdListener != null) {
                    mAdListener.onAdOpened();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd = null;
                if (mAdListener != null) {
                    mAdListener.onAdClosed();
                }
                initAds(mGiftView);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (mAdListener != null) {
                    mAdListener.onAdClicked();
                }
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.ADMOB_ID_PREFIX, "");
        if (AdsConfig.getInstance().isTestMode()) {
            adsId = AdsConstants.interstitial_test_id;
        }
        mInterstitialAd = AdmobAdvertisements.initInterstitialAd(mContext.getApplicationContext(), adsId, adListener);
    }

    private void initFanAds(View giftView) {
        if (mContext == null) {
            return;
        }
        mGiftView = giftView;

        if (mFanInterstitialAd != null) {
            if (mAdListener != null && mFanInterstitialAd.isAdLoaded()) {
                mAdListener.onAdLoaded();
            }
            if (mGiftView != null) {
                if (mFanInterstitialAd.isAdLoaded()) {
                    mGiftView.setVisibility(View.VISIBLE);
                    DebugLog.logd("FAN - Show Gift button");
                } else {
                    mGiftView.setVisibility(View.GONE);
                }
            }
            return;
        }

        if (mGiftView != null) {
            mGiftView.setVisibility(View.GONE);
        }

        InterstitialAdListener adListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                }
                if (mAdListener != null) {
                    mAdListener.onAdOpened();
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mFanInterstitialAd = null;
                if (mAdListener != null) {
                    mAdListener.onAdClosed();
                }
                initAds(mGiftView);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                DebugLog.logd("\n[FAN - Interstitial] PlacementId: " + ad.getPlacementId() + "\nErrorCode: "
                        + adError.getErrorCode() + "\nErrorMessage: " + adError.getErrorMessage());
                if (mGiftView != null) {
                    mGiftView.setVisibility(View.GONE);
                }
                if (mFanInterstitialAd != null) {
                    mFanInterstitialAd.destroy();
                    mFanInterstitialAd = null;
                }
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(adError.getErrorCode());
                }

                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initAds(mGiftView);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mTryReloadAds = 0;
                if (mGiftView != null) {
                    DebugLog.logd("FAN - Show Gift button");
                    mGiftView.setVisibility(View.VISIBLE);
                }
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mAdListener != null) {
                    mAdListener.onAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.FAN_ID_PREFIX, "");
        mFanInterstitialAd = FanAdvertisements.initInterstitialAd(mContext.getApplicationContext(), adsId, adListener);
    }

    public boolean isLoaded() {
        if (useFanAdNetwork) {
            return mFanInterstitialAd != null && mFanInterstitialAd.isAdLoaded();
        } else {
            return mInterstitialAd != null && mInterstitialAd.isLoaded();
        }
    }

    public void show() {
        if (isLoaded()) {
            if (useFanAdNetwork) {
                mFanInterstitialAd.show();
            } else {
                mInterstitialAd.show();
            }
        }
    }

    /*
     * Destroy references
     * */
    public void destroy() {
        mInterstitialAd = null;
        if (mFanInterstitialAd != null) {
            mFanInterstitialAd.destroy();
            mFanInterstitialAd = null;
        }
        mAdListener = null;
    }
}
