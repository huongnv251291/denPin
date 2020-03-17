package com.tohsoft.app.utils.ads;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ConvertUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;

/**
 * Created by Phong on 11/16/2018.
 */

public class AdViewWrapper implements AdsId {
    private static final int MAX_TRY_LOAD_ADS = 3;
    private AdView mAdView;
    private int mTryReloadAds = 0;
    private int mAdsPosition = 0;
    private boolean mIsEmptyAds = false;
    private final int DEFAULT_CONTAINER_HEIGHT;
    private int mAdViewHeight = 0;

    public AdViewWrapper() {
        DEFAULT_CONTAINER_HEIGHT = ConvertUtils.dp2px(60);
    }

    public AdView getAdView() {
        return mAdView;
    }

    public void initBanner(Context context, ViewGroup container) {
        initBanner(context, container, null);
    }

    public void initEmptyAdView(Context context, ViewGroup container) {
        mIsEmptyAds = true;
        initBanner(context, container, null);
    }

    /*
     * Auto try reload ads
     * */
    public void initBanner(Context context, ViewGroup container, AdListener adListener) {
        if (context == null || container == null || !BuildConfig.SHOW_AD) {
            return;
        }
        if (mAdView != null) {
            if (adListener != null) {
                mAdView.setAdListener(adListener);
            }
            if (!mIsEmptyAds) {
                int height = mAdViewHeight;
                if (height == 0 && mAdView.getVisibility() != View.GONE) {
                    height = DEFAULT_CONTAINER_HEIGHT;
                }
                Advertisements.setHeightForContainer(container, height);
            }
            Advertisements.addBannerAdsToContainer(container, mAdView);
            return;
        }
        if (adListener == null) {
            adListener = new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    DebugLog.loge("\n[NormalBanner] onAdFailedToLoad - Code: " + i + "\nid: " + (mAdView != null ? mAdView.getAdUnitId() : ""));
                    mAdViewHeight = 0;
                    Advertisements.setHeightForContainer(container, 0);
                    if (mAdView != null) {
                        mAdView.setVisibility(View.GONE);
                        if (mAdView.getParent() != null) {
                            ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                            viewGroup.removeView(mAdView);
                            Advertisements.setHeightForContainer(viewGroup, 0);
                        }
                        mAdView = null;
                    }
                    if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                        initBanner(context, container, null);
                        mTryReloadAds++;
                        mAdsPosition++;
                    } else {
                        mTryReloadAds = 0;
                        mAdsPosition = 0;
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mTryReloadAds = 0;
                    if (mAdView != null) {
                        mAdView.setVisibility(View.VISIBLE);
                        if (!mIsEmptyAds) {
                            mAdViewHeight = mAdView.getMinimumHeight();
                            if (mAdView.getParent() != null && mAdView.getParent() != container) {
                                ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                                Advertisements.setHeightForContainer(viewGroup, mAdViewHeight);
                            }
                            Advertisements.setHeightForContainer(container, mAdViewHeight);
                            DebugLog.logd("onAdLoaded - Height: " + mAdViewHeight);
                        }
                    }
                    container.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    goneAdViewAndContainer();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    goneAdViewAndContainer();
                }
            };
        }

        if (mIsEmptyAds ? mAdsPosition >= bannersEmptyScreen.length : mAdsPosition >= banners.length) {
            mAdsPosition = 0;
        }
        mAdViewHeight = 0;
        if (!mIsEmptyAds) {
            Advertisements.setHeightForContainer(container, DEFAULT_CONTAINER_HEIGHT);
        }
        if (mIsEmptyAds) {
            mAdView = Advertisements.initMediumBanner(context.getApplicationContext(), bannersEmptyScreen[mAdsPosition], adListener);
        } else {
            mAdView = Advertisements.initNormalBanner(context.getApplicationContext(), banners[mAdsPosition], adListener);
        }
        Advertisements.addBannerAdsToContainer(container, mAdView);
    }

    /*
     * Just initData & auto try reload ads
     * */
    public void initBannerExitDialog(Context context, AdListener adListener) {
        if (context == null || !BuildConfig.SHOW_AD) {
            return;
        }

        if (adListener == null) {
            adListener = new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    DebugLog.loge("\n[BannerExitDialog] onAdFailedToLoad - Code: " + i + "\nid: " + (mAdView != null ? mAdView.getAdUnitId() : ""));
                    if (mAdView != null) {
                        mAdView.setVisibility(View.GONE);
                        if (mAdView.getParent() != null) {
                            ((ViewGroup) mAdView.getParent()).removeView(mAdView);
                        }
                        mAdView = null;
                    }
                    if (mTryReloadAds < MAX_TRY_LOAD_ADS) {
                        initBannerExitDialog(context, null);
                        mTryReloadAds++;
                        mAdsPosition++;
                    } else {
                        mTryReloadAds = 0;
                        mAdsPosition = 0;
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mTryReloadAds = 0;
                    if (mAdView != null) {
                        mAdView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    goneAdViewAndContainer();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    goneAdViewAndContainer();
                }
            };
        }

        if (mAdsPosition >= bannersExitDialog.length) {
            mAdsPosition = 0;
        }
        mAdView = Advertisements.initMediumBanner(context.getApplicationContext(), bannersExitDialog[mAdsPosition], adListener);
    }

    private void goneAdViewAndContainer() {
        if (mAdView != null) {
            if (mAdView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                viewGroup.removeAllViews();
                Advertisements.setHeightForContainer(viewGroup, 0);
            }
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
                viewGroup.removeAllViews();
                Advertisements.setHeightForContainer(viewGroup, 0);
            }
            mAdView = null;
        }
    }

}
