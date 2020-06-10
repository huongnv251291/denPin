package com.tohsoft.ads.wrapper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ConvertUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.tohsoft.ads.AdsConfig;
import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.admob.AdmobAdvertisements;
import com.tohsoft.ads.fan.FanAdvertisements;
import com.tohsoft.ads.utils.AdsUtils;
import com.utility.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phong on 11/16/2018.
 */

public class AdViewWrapper {
    private int MAX_TRY_LOAD_ADS = 3;
    private int DEFAULT_CONTAINER_HEIGHT;
    private final List<String> mAdsIds = new ArrayList<>();
    private final Context mContext;

    private ViewGroup mContainer;
    private AdListener mAdListener;
    private AdView mAdView;
    private com.facebook.ads.AdView mFanAdView;
    private boolean isUseAdaptiveBanner = true;
    private int mTryReloadAds = 0;
    private int mAdViewHeight = 0;

    private String mCurrentAdsId;
    private boolean useFanAdNetwork;
    private int mAdsPosition = 0;

    public AdViewWrapper(@NonNull Context context, @NonNull List<String> adsId) {
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

    public void setUseAdaptiveBanner(boolean isUseAdaptiveBanner) {
        this.isUseAdaptiveBanner = isUseAdaptiveBanner;
    }

    public void setAdListener(AdListener adListener) {
        this.mAdListener = adListener;
    }

    public void removeAdListener() {
        this.mAdListener = null;
    }

    public View getAdView() {
        if (useFanAdNetwork) {
            return mFanAdView;
        } else {
            return mAdView;
        }
    }

    public void initBanner(ViewGroup container) {
        mContainer = container;
        getAdsId();
        if (useFanAdNetwork) {
            initFanNormalBanner(container);
        } else {
            initNormalBanner(container);
        }
    }

    public void initMediumBanner(ViewGroup container) {
        mContainer = container;
        initMediumBanner();
    }

    public void initMediumBanner() {
        getAdsId();
        if (useFanAdNetwork) {
            initFanMediumBanner();
        } else {
            initAdmobMediumBanner();
        }
    }

    private void getAdsId() {
        if (mAdsPosition >= mAdsIds.size()) {
            mAdsPosition = 0;
        }
        mCurrentAdsId = mAdsIds.get(mAdsPosition);
        boolean useFanAdNetwork = mCurrentAdsId.startsWith(AdsConstants.FAN_ID_PREFIX);

        // Init default bottom banner height for container
        if (useFanAdNetwork) {
            DEFAULT_CONTAINER_HEIGHT = ConvertUtils.dp2px(50);
        } else {
            DEFAULT_CONTAINER_HEIGHT = ConvertUtils.dp2px(60);
        }

        if (this.useFanAdNetwork != useFanAdNetwork) {
            // Destroy previous Ads instance
            destroy();
        }
        this.useFanAdNetwork = useFanAdNetwork;
    }

    /**
     * Admob
     */
    private void initNormalBanner(final ViewGroup container) {
        if (mContext == null) {
            return;
        }
        if (mAdView != null) {
            int height = mAdViewHeight;
            if (height == 0 && mAdView.getVisibility() != View.GONE) {
                height = DEFAULT_CONTAINER_HEIGHT;
            }
            AdsUtils.setHeightForContainer(container, height);
            AdsUtils.addBannerAdsToContainer(container, mAdView);
            return;
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onAdFailedToLoad(int code) {
                super.onAdFailedToLoad(code);
                DebugLog.logd("\n[Admob - NormalBanner] onAdFailedToLoad - Code: " + code + "\nid: " + (mAdView != null ? mAdView.getAdUnitId() : ""));
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(code);
                }
                mAdViewHeight = 0;
                AdsUtils.setHeightForContainer(container, 0);

                // Destroy Ads instance when load failed
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                    if (mAdView.getParent() != null) {
                        ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                        viewGroup.removeView(mAdView);
                        AdsUtils.setHeightForContainer(viewGroup, 0);
                    }
                    mAdView = null;
                }

                // Try to reload Ads
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initNormalBanner(container);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
                mTryReloadAds = 0; // Reset flag

                // Get Ads height and set for container
                if (mAdView != null) {
                    mAdView.setVisibility(View.VISIBLE);
                    mAdViewHeight = mAdView.getMeasuredHeight();
                    if (container != null && mAdView.getParent() != null && mAdView.getParent() != container) {
                        ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                        AdsUtils.setHeightForContainer(viewGroup, mAdViewHeight);
                    }
                    AdsUtils.setHeightForContainer(container, mAdViewHeight);
                    DebugLog.logd("onAdLoaded - Height: " + mAdViewHeight);
                }

                if (container != null) {
                    container.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                goneAdViewAndContainerWhenAdClicked();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                goneAdViewAndContainerWhenAdClicked();
            }
        };

        // Set default height for container
        mAdViewHeight = 0;
        AdsUtils.setHeightForContainer(container, DEFAULT_CONTAINER_HEIGHT);

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.ADMOB_ID_PREFIX, "");
        if (AdsConfig.getInstance().isTestMode()) {
            adsId = AdsConstants.banner_test_id;
        }
        if (!isUseAdaptiveBanner) {
            mAdView = AdmobAdvertisements.initNormalBanner(mContext.getApplicationContext(), adsId, adListener);
        } else {
            mAdView = AdmobAdvertisements.initAdaptiveBanner(mContext.getApplicationContext(), adsId, adListener);
        }

        // Add Ads to container
        AdsUtils.addBannerAdsToContainer(container, mAdView);
    }

    private void initAdmobMediumBanner() {
        if (mContext == null) {
            return;
        }
        if (mAdView != null && mContainer != null) {
            AdsUtils.addBannerAdsToContainer(mContainer, mAdView);
            return;
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                DebugLog.logd("\n[Admob - MediumBanner] onAdFailedToLoad - Code: " + i + "\nid: " + (mAdView != null ? mAdView.getAdUnitId() : ""));
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(i);
                }

                // Destroy Ads instance when load failed
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                    if (mAdView.getParent() != null) {
                        ((ViewGroup) mAdView.getParent()).removeView(mAdView);
                    }
                    mAdView = null;
                }

                // Try to reload Ads
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initMediumBanner(mContainer);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
                mTryReloadAds = 0; // Reset flag

                if (mAdView != null) {
                    mAdView.setVisibility(View.VISIBLE);
                }
                if (mContainer != null) {
                    mContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                goneAdViewAndContainerWhenAdClicked();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                goneAdViewAndContainerWhenAdClicked();
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.ADMOB_ID_PREFIX, "");
        if (AdsConfig.getInstance().isTestMode()) {
            adsId = AdsConstants.banner_test_id;
        }
        mAdView = AdmobAdvertisements.initMediumBanner(mContext.getApplicationContext(), adsId, adListener);

        // Add Ads to container
        AdsUtils.addBannerAdsToContainer(mContainer, mAdView);
    }

    /**
     * Fan
     */
    private void initFanNormalBanner(final ViewGroup container) {
        if (mContext == null) {
            return;
        }
        if (mFanAdView != null) {
            int height = mAdViewHeight;
            if (height == 0 && mFanAdView.getVisibility() != View.GONE) {
                height = DEFAULT_CONTAINER_HEIGHT;
            }
            AdsUtils.setHeightForContainer(container, height);
            AdsUtils.addBannerAdsToContainer(container, mFanAdView);
            return;
        }

        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                int code = adError.getErrorCode();
                DebugLog.logd("\n[FAN - NormalBanner] PlacementId: " + ad.getPlacementId() + "\nErrorCode: "
                        + adError.getErrorCode() + "\nErrorMessage: " + adError.getErrorMessage());
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(code);
                }
                mAdViewHeight = 0;
                AdsUtils.setHeightForContainer(container, 0);

                // Destroy Ads instance when load failed
                if (mFanAdView != null) {
                    mFanAdView.setVisibility(View.GONE);
                    if (mFanAdView.getParent() != null) {
                        ViewGroup viewGroup = (ViewGroup) mFanAdView.getParent();
                        viewGroup.removeView(mFanAdView);
                        AdsUtils.setHeightForContainer(viewGroup, 0);
                    }
                    mFanAdView.destroy();
                    mFanAdView = null;
                }

                // Try reload Ads
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initNormalBanner(container);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
                mTryReloadAds = 0; // Reset flag

                // Get Ads height and set for container
                if (mFanAdView != null) {
                    mFanAdView.setVisibility(View.VISIBLE);
                    mAdViewHeight = mFanAdView.getMeasuredHeight();
                    if (container != null && mFanAdView.getParent() != null && mFanAdView.getParent() != container) {
                        ViewGroup viewGroup = (ViewGroup) mFanAdView.getParent();
                        AdsUtils.setHeightForContainer(viewGroup, mAdViewHeight);
                    }
                    AdsUtils.setHeightForContainer(container, mAdViewHeight);
                    DebugLog.logd("FAN - onAdLoaded - Height: " + mAdViewHeight);
                }
                if (container != null) {
                    container.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                goneAdViewAndContainerWhenAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // Set default height for container
        mAdViewHeight = 0;
        AdsUtils.setHeightForContainer(container, DEFAULT_CONTAINER_HEIGHT);

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.FAN_ID_PREFIX, "");
        mFanAdView = FanAdvertisements.initNormalBanner(mContext.getApplicationContext(), adsId, adListener);

        // Add Ads to container
        AdsUtils.addBannerAdsToContainer(container, mFanAdView);
    }

    private void initFanMediumBanner() {
        if (mContext == null) {
            return;
        }
        if (mFanAdView != null && mContainer != null) {
            AdsUtils.addBannerAdsToContainer(mContainer, mFanAdView);
            return;
        }

        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError adError) {
                int code = adError.getErrorCode();
                DebugLog.logd("\n[FAN - MediumBanner] PlacementId: " + ad.getPlacementId() + "\nErrorCode: "
                        + adError.getErrorCode() + "\nErrorMessage: " + adError.getErrorMessage());
                if (mAdListener != null) {
                    mAdListener.onAdFailedToLoad(code);
                }

                // Destroy Ads instance when load failed
                if (mFanAdView != null) {
                    mFanAdView.setVisibility(View.GONE);
                    if (mFanAdView.getParent() != null) {
                        ((ViewGroup) mFanAdView.getParent()).removeView(mFanAdView);
                    }
                    mFanAdView.destroy();
                    mFanAdView = null;
                }

                // Try to load Ads
                if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                    mTryReloadAds++;
                    mAdsPosition++;
                    initMediumBanner(mContainer);
                } else {
                    mTryReloadAds = 0;
                    mAdsPosition = 0;
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
                mTryReloadAds = 0; // Reset flag
                if (mFanAdView != null) {
                    mFanAdView.setVisibility(View.VISIBLE);
                }
                if (mContainer != null) {
                    mContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                goneAdViewAndContainerWhenAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // Init Ads
        String adsId = mCurrentAdsId.replaceAll(AdsConstants.FAN_ID_PREFIX, "");
        mFanAdView = FanAdvertisements.initMediumBanner(mContext.getApplicationContext(), adsId, adListener);

        // Add Ads to container
        AdsUtils.addBannerAdsToContainer(mContainer, mFanAdView);
    }

    /*
     *
     * */
    private void goneAdViewAndContainerWhenAdClicked() {
        if (mAdListener != null) {
            mAdListener.onAdClicked();
        }
        View adView = useFanAdNetwork ? mFanAdView : mAdView;
        if (adView != null) {
            if (adView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) adView.getParent();
                viewGroup.removeView(adView);
                AdsUtils.setHeightForContainer(viewGroup, 0);
            }
        }
    }

    public void setVisibility(int visibility) {
        if (mAdView != null) {
            mAdView.setVisibility(visibility);
        }
        if (mFanAdView != null) {
            mFanAdView.setVisibility(visibility);
        }
    }

    /*
     * Destroy references
     *
     * */
    public void destroy() {
        if (mAdView != null) {
            mAdView.setVisibility(View.GONE);
            if (mAdView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                viewGroup.removeView(mAdView);
                AdsUtils.setHeightForContainer(viewGroup, 0);
            }
            mAdView.destroy();
            mAdView = null;
        }
        if (mFanAdView != null) {
            mFanAdView.setVisibility(View.GONE);
            if (mFanAdView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) mFanAdView.getParent();
                viewGroup.removeView(mFanAdView);
                AdsUtils.setHeightForContainer(viewGroup, 0);
            }
            mFanAdView.destroy();
            mFanAdView = null;
        }
    }

}
