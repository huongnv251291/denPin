package com.tohsoft.app.utils.ads;

import android.annotation.SuppressLint;

@SuppressLint("StaticFieldLeak")
public class AdsConstants {
    public static AdViewWrapper bannerBottom;
    public static AdViewWrapper bannerEmptyScreen;
    public static InterstitialAdWrapper promotionAds;

    public static void destroy() {
        if (bannerBottom != null) {
            bannerBottom.destroy();
            bannerBottom = null;
        }
        if (bannerEmptyScreen != null) {
            bannerEmptyScreen.destroy();
            bannerEmptyScreen = null;
        }
        if (promotionAds != null) {
            promotionAds.destroy();
            promotionAds = null;
        }
    }

}
