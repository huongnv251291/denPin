package com.tohsoft.app.utils.ads;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;

/**
 * Created by Phong on 4/28/2017.
 */

public class Advertisements {

    private static AdRequest buildAdRequest(Context context) {
        AdRequest.Builder builder = new AdRequest.Builder();
        return builder.build();
    }

    public static void addBannerAdsToContainer(final ViewGroup container, final AdView adView) {
        try {
            if (BuildConfig.SHOW_AD && adView != null) {
                if (adView.getParent() != null) {
                    if (adView.getParent() == container) {
                        return;
                    }
                    ((ViewGroup) adView.getParent()).removeAllViews();
                }
                if (adView.getVisibility() == View.VISIBLE) {
                    container.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.GONE);
                }
                container.removeAllViews();
                container.addView(adView);
            } else {
                container.removeAllViews();
                container.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

    /*
     * InterstitialAd
     * */
    public static InterstitialAd initInterstitialAd(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
            adsId = AdsId.interstitial_test_id;
        }
        InterstitialAd mInterstitialAdGift = new InterstitialAd(context);
        mInterstitialAdGift.setAdUnitId(adsId);
        if (adListener != null) {
            mInterstitialAdGift.setAdListener(adListener);
        }
        mInterstitialAdGift.loadAd(buildAdRequest(context));
        return mInterstitialAdGift;
    }

    /*
     * AdView
     * */
    public static AdView initNormalBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
            adsId = AdsId.banner_test_id;
        }
        final AdView adView = new AdView(context.getApplicationContext());
        adView.setAdSize(getAdSize(context)); // 320x50
        adView.setAdUnitId(adsId);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd(buildAdRequest(context));
        return adView;
    }

    public static AdView initMediumBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
            adsId = AdsId.banner_test_id;
        }
        final AdView adView = new AdView(context);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE); // 300x250
        adView.setAdUnitId(adsId);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd(buildAdRequest(context));
        return adView;
    }

    public static AdView initLargeBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        if (BuildConfig.DEBUG || BuildConfig.TEST_AD) {
            adsId = AdsId.banner_test_id;
        }
        final AdView adView = new AdView(context);
        adView.setAdSize(AdSize.LARGE_BANNER); // 320 x 100
        adView.setAdUnitId(adsId);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd(buildAdRequest(context));
        return adView;
    }

    /*
    * Adaptive Banner
    * */
    private static AdSize getAdSize(Context context) {
        if (context == null) {
            return AdSize.BANNER;
        }

        // Determine the screen width (less decorations) to use for the ad width.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

}
