package com.tohsoft.ads.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;
import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.models.AdsId;
import com.tohsoft.ads.models.AdsType;
import com.utility.UtilsLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongNX on 6/8/2020.
 */
public class AdsUtils {

    public static void addAdsToContainer(final ViewGroup container, final View adView) {
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
                    ((LinearLayout.LayoutParams) layoutParams).gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) layoutParams).topMargin = topMargin;
                    ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) layoutParams).topMargin = topMargin;
                    ((RelativeLayout.LayoutParams) layoutParams).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    ((RelativeLayout.LayoutParams) layoutParams).addRule(RelativeLayout.CENTER_HORIZONTAL);
                }
                adView.setLayoutParams(layoutParams);

            } else {
                AdsUtils.setHeightForContainer(container, 0);
                container.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            AdDebugLog.loge(e);
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

    public static AdsId readIdsFromAssetsFile(Context context, String assetsFileName) {
        if (context == null || assetsFileName == null) {
            return null;
        }
        String data = UtilsLib.readTextFileInAsset(context, assetsFileName);
        if (!TextUtils.isEmpty(data)) {
            return GsonUtils.fromJson(data, GsonUtils.getType(AdsId.class));
        }
        return null;
    }

    // Sắp xếp Ads id theo config truyền vào
    public static AdsId mixAdsIdWithConfig(AdsId admobIds, AdsId fanIds, @NonNull List<String> adsIdConfigList) {
        if (!UtilsLib.isEmptyList(adsIdConfigList)) {
            AdsId adsId = new AdsId();
            // std_banner
            AdDebugLog.logd("Mix std_banner");
            adsId.std_banner = mixAdsId(
                    admobIds != null ? admobIds.std_banner : null,
                    fanIds != null ? fanIds.std_banner : null,
                    adsIdConfigList
            );
            // banner_exit_dialog
            AdDebugLog.logd("Mix banner_exit_dialog");
            adsId.banner_exit_dialog = mixAdsId(
                    admobIds != null ? admobIds.banner_exit_dialog : null,
                    fanIds != null ? fanIds.banner_exit_dialog : null,
                    adsIdConfigList
            );
            // banner_empty_screen
            AdDebugLog.logd("Mix banner_empty_screen");
            adsId.banner_empty_screen = mixAdsId(
                    admobIds != null ? admobIds.banner_empty_screen : null,
                    fanIds != null ? fanIds.banner_empty_screen : null,
                    adsIdConfigList
            );
            // interstitial_opa
            AdDebugLog.logd("Mix interstitial_opa");
            adsId.interstitial_opa = mixAdsId(
                    admobIds != null ? admobIds.interstitial_opa : null,
                    fanIds != null ? fanIds.interstitial_opa : null,
                    adsIdConfigList
            );
            // interstitial_gift
            AdDebugLog.logd("Mix interstitial_gift");
            adsId.interstitial_gift = mixAdsId(
                    admobIds != null ? admobIds.interstitial_gift : null,
                    fanIds != null ? fanIds.interstitial_gift : null,
                    adsIdConfigList
            );

            return adsId;
        }
        return null;
    }

    public static List<String> mixAdsId(List<String> admobIds, List<String> fanIds, @NonNull List<String> adsIdConfigList) {
        if (!UtilsLib.isEmptyList(adsIdConfigList)) {
            List<String> adsIdList = new ArrayList<>();
            for (String adsConfig : adsIdConfigList) { // adsConfig = ADMOB-0 | FAN-0
                int position = 0;
                try {
                    // Lấy ra vị trí id cần lấy trong mảng
                    position = Integer.parseInt(adsConfig.split("-")[1].trim());
                } catch (Exception e) {
                    AdDebugLog.loge(e);
                }

                // Kiểm tra xem position của id có trong mảng tương ứng không, nếu có thì thêm tiền tố tương ứng rồi add vào list
                if (adsConfig.toLowerCase().contains(AdsConstants.ADMOB) && admobIds != null && position < admobIds.size()) {
                    adsIdList.add(AdsConstants.ADMOB_ID_PREFIX + admobIds.get(position));
                } else if (adsConfig.toLowerCase().contains(AdsConstants.FAN) && fanIds != null && position < fanIds.size()) {
                    adsIdList.add(AdsConstants.FAN_ID_PREFIX + fanIds.get(position));
                }
            }

            logAdsId(adsIdList.toArray(new String[0]));
            return adsIdList;
        }
        return new ArrayList<>();
    }

    public static void logAdsId(String[] adsIdList) {
        StringBuilder builder = new StringBuilder();
        for (String id : adsIdList) {
            builder.append("\n").append(id);
        }
        AdDebugLog.logd("Ads id:" + builder.toString());
    }

    public static void mixCustomAdsIdConfig(AdsId adsId, AdsId admobIds, AdsId fanIds, AdsType adsType, @NonNull List<String> adsIdConfigList) {
        if (adsId != null && adsType != null && !UtilsLib.isEmptyList(adsIdConfigList)) {
            AdDebugLog.logd("mixCustomAdsIdConfig - " + adsType.getValue());
            if (adsType == AdsType.STD_BANNER) {
                adsId.std_banner = mixAdsId(
                        admobIds != null ? admobIds.std_banner : null,
                        fanIds != null ? fanIds.std_banner : null,
                        adsIdConfigList
                );
            } else if (adsType == AdsType.BANNER_EXIT_DIALOG) {
                adsId.banner_exit_dialog = mixAdsId(
                        admobIds != null ? admobIds.banner_exit_dialog : null,
                        fanIds != null ? fanIds.banner_exit_dialog : null,
                        adsIdConfigList
                );
            } else if (adsType == AdsType.BANNER_EMPTY_SCREEN) {
                adsId.banner_empty_screen = mixAdsId(
                        admobIds != null ? admobIds.banner_empty_screen : null,
                        fanIds != null ? fanIds.banner_empty_screen : null,
                        adsIdConfigList
                );
            } else if (adsType == AdsType.INTERSTITIAL_OPA) {
                adsId.interstitial_opa = mixAdsId(
                        admobIds != null ? admobIds.interstitial_opa : null,
                        fanIds != null ? fanIds.interstitial_opa : null,
                        adsIdConfigList
                );
            } else if (adsType == AdsType.INTERSTITIAL_GIFT) {
                adsId.interstitial_gift = mixAdsId(
                        admobIds != null ? admobIds.interstitial_gift : null,
                        fanIds != null ? fanIds.interstitial_gift : null,
                        adsIdConfigList
                );
            }
        }
    }

}
