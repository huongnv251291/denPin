package com.tohsoft.ads.admob;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Phong on 4/28/2017.
 */

public class AdmobAdvertisements {

    private static AdRequest buildAdRequest(Context context) {
        AdRequest.Builder builder = new AdRequest.Builder();
        return builder.build();
    }

    /*
     * InterstitialAd
     * */
    public static InterstitialAd initInterstitialAd(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
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
    public static AdView initAdaptiveBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        final AdView adView = new AdView(context.getApplicationContext());
        adView.setAdSize(getAdSize(context));
        adView.setAdUnitId(adsId);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.loadAd(buildAdRequest(context));
        return adView;
    }

    public static AdView initNormalBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        final AdView adView = new AdView(context.getApplicationContext());
        adView.setAdSize(AdSize.BANNER); // 320x50
        adView.setAdUnitId(adsId);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
//        adView.setVisibility(View.GONE);
        adView.loadAd(buildAdRequest(context));
        return adView;
    }

    public static AdView initMediumBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
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
