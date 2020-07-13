package com.tohsoft.ads.wrapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.tohsoft.ads.AdsConfig;
import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.R;
import com.tohsoft.ads.utils.AdDebugLog;
import com.utility.UtilsLib;

import java.util.List;
import java.util.Locale;

@SuppressLint("StaticFieldLeak")
public class NativeAdViewWrapper {
    private UnifiedNativeAd unifiedNativeAd;
    private UnifiedNativeAdView unifiedNativeAdView;
    private ViewGroup viewGroupContainer;
    private int sAdsPosition = 0;
    private int sTryLoadAds = 0;
    private final String[] adIds;

    public NativeAdViewWrapper(@NonNull String[] adIds) {
        this.adIds = adIds;
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    @SuppressLint("InflateParams")
    public void refreshAd(Context context, ViewGroup viewGroup) {
        if (AdsConfig.getInstance().isFullVersion()) {
            return;
        }
        viewGroupContainer = viewGroup;
        if (unifiedNativeAd != null) { // Ad đã được load
            if (initNativeAdView(context)) {
                populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
            }
            if (unifiedNativeAdView.getParent() != null) { // Nếu Ad đã được add vào container nào đó rồi thì remove Ad khỏi container đó
                ((ViewGroup) unifiedNativeAdView.getParent()).removeView(unifiedNativeAdView);
            }

            // Add ads to container
            addAdsToContainer();
            return;
        }

        // Lấy ads id trong mảng
        if (sAdsPosition >= adIds.length) {
            sAdsPosition = 0;
        }
        String id = adIds[sAdsPosition];
        if (AdsConfig.getInstance().isTestMode()) {
            id = AdsConstants.native_ad_test_id;
        }
        AdLoader.Builder builder = new AdLoader.Builder(context, id);

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(nativeAd -> {
            initNativeAdView(context);
            if (unifiedNativeAd != null) {
                unifiedNativeAd.destroy();
            }
            unifiedNativeAd = nativeAd;
            populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);

            // Add ads to container
            addAdsToContainer();
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                AdDebugLog.loge("Failed to load native ad: " + errorCode );
                if (sTryLoadAds < 3) {
                    sTryLoadAds++;
                    sAdsPosition++;
                    refreshAd(context, viewGroup);
                }
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @SuppressLint("InflateParams")
    private boolean initNativeAdView(Context context) {
        if (unifiedNativeAdView == null) {
            unifiedNativeAdView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified, null);
            return true;
        }
        return false;
    }

    private void addAdsToContainer() {
        if (viewGroupContainer != null) {
            viewGroupContainer.setVisibility(View.VISIBLE);
            viewGroupContainer.removeAllViews();

            viewGroupContainer.addView(unifiedNativeAdView);
        }
    }

    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private static void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        ImageView mainImageView = adView.findViewById(R.id.ad_image);

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            mainImageView.setVisibility(View.GONE);
            AdDebugLog.loge(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before refreshing
                    // or replacing them with another ad in the same UI location.
                    AdDebugLog.loge("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAd.getImages();
            if (!UtilsLib.isEmptyList(images)) {
                mainImageView.setImageDrawable(images.get(0).getDrawable());
            } else {
                mainImageView.setVisibility(View.GONE);
            }

            AdDebugLog.loge("Video status: Ad does not contain a video asset.");
        }
    }

    public void onDestroy() {
        if (unifiedNativeAd != null) {
            unifiedNativeAd.destroy();
        }
        if (viewGroupContainer != null) {
            viewGroupContainer.removeAllViews();
            viewGroupContainer.setVisibility(View.GONE);
        }
        sAdsPosition = 0;
        unifiedNativeAdView = null;
        unifiedNativeAd = null;
        viewGroupContainer = null;
    }
}
