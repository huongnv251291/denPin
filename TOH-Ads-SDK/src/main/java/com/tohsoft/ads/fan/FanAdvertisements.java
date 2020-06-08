package com.tohsoft.ads.fan;

import android.content.Context;
import android.view.View;

import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.tohsoft.ads.AdsModules;

import java.util.Arrays;

/**
 * Created by Phong on 4/28/2017.
 */

public class FanAdvertisements {

    /*
     * InterstitialAd
     * */
    public static InterstitialAd initInterstitialAd(Context context, String adsId, InterstitialAdListener adListener) {
        if (context == null) {
            return null;
        }
        AdSettings.addTestDevices(AdsModules.getInstance().getTestDevices());
        InterstitialAd interstitialAd = new InterstitialAd(context, adsId);
        if (adListener != null) {
            interstitialAd.setAdListener(adListener);
        }
        interstitialAd.loadAd();
        return interstitialAd;
    }

    /*
     * AdView
     * */
    public static AdView initNormalBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        AdSettings.addTestDevices(AdsModules.getInstance().getTestDevices());
        final AdView adView = new AdView(context, adsId, AdSize.BANNER_HEIGHT_50);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd();
        return adView;
    }

    public static AdView initMediumBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        AdSettings.addTestDevices(AdsModules.getInstance().getTestDevices());
        final AdView adView = new AdView(context, adsId, AdSize.RECTANGLE_HEIGHT_250);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd();
        return adView;
    }

    public static AdView initLargeBanner(Context context, String adsId, AdListener adListener) {
        if (context == null) {
            return null;
        }
        AdSettings.addTestDevices(AdsModules.getInstance().getTestDevices());
        final AdView adView = new AdView(context, adsId, AdSize.BANNER_HEIGHT_90);
        if (adListener != null) {
            adView.setAdListener(adListener);
        }
        adView.setVisibility(View.GONE);
        adView.loadAd();
        return adView;
    }

}
