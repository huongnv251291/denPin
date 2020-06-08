package com.tohsoft.ads.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.utility.DebugLog;

public
/**
 * Created by PhongNX on 6/8/2020.
 */
class AdsUtils {

    public static void addBannerAdsToContainer(final ViewGroup container, final View adView) {
        try {
            if (container == null) {
                return;
            }
            if (adView != null) {
                if (adView.getParent() != null) {
                    if (adView.getParent() == container) {
                        return;
                    }
                    ((ViewGroup) adView.getParent()).removeAllViews();
                }

                container.setVisibility(adView.getVisibility());
                container.removeAllViews();
                container.addView(adView);

                // Adview cách view liền kế tối thiểu 2px
                ViewGroup.LayoutParams layoutParams = adView.getLayoutParams();
                int topMargin = 2;
                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) layoutParams).topMargin = topMargin;
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) layoutParams).topMargin = topMargin;
                } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) layoutParams).topMargin = topMargin;
                }
                adView.setLayoutParams(layoutParams);

            } else {
                AdsUtils.setHeightForContainer(container, 0);
                container.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

    public static void setHeightForContainer(ViewGroup container, int height) {
        if (container != null) {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            if (height == 0) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                layoutParams.height = height + 2;
            }
            container.setLayoutParams(layoutParams);
        }
    }
}
