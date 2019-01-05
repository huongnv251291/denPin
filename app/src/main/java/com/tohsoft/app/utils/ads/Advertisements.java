package com.tohsoft.app.utils.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tohsoft.app.BuildConfig;
import com.utility.DebugLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Phong on 4/28/2017.
 */

public class Advertisements {

    private static AdRequest buildAdRequest(Context context) {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.TEST_AD || BuildConfig.DEBUG) {
            builder.addTestDevice(getAdmodDeviceId(context));
        }
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
                container.setVisibility(View.VISIBLE);
                container.removeAllViews();
                container.addView(adView);
            } else {
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
        adView.setAdSize(AdSize.BANNER); // 320x50
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

    //
    @SuppressLint("HardwareIds")
    private static String getAdmodDeviceId(Context context) {
        try {
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(android_id.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
