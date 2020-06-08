package com.tohsoft.ads.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tohsoft.ads.AdsConstants;
import com.tohsoft.ads.AdsModules;
import com.utility.DebugLog;
import com.utility.UtilsLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PhongNX on 6/5/2020.
 */
public class AdsConfig {
    private Context mContext;
    @Mode private int mMode;
    private String[] mAdsIds;
    private boolean isTestMode;

    private long mSplashDelayTimeInMs;
    private long mOPAProgressDelayTimeInMs;

    private AdsConfig(Context context) {
        this.mContext = context;
        this.mSplashDelayTimeInMs = AdsModules.getInstance().getSplashDelayInMs();
        this.mOPAProgressDelayTimeInMs = AdsModules.getInstance().getInterOPAProgressDelayInMs();
    }

    public Context getContext() {
        return mContext;
    }

    public int getMode() {
        return mMode;
    }

    public String[] getAdsIds() {
        return mAdsIds;
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public long getSplashDelayTimeInMs() {
        return mSplashDelayTimeInMs;
    }

    public long getOPAProgressDelayTimeInMs() {
        return mOPAProgressDelayTimeInMs;
    }

    public static class Builder {
        private AdsConfig mAdsConfig;
        private int mMode = -1;
        private String[] mAdmobIds;
        private String[] mFanIds;

        public Builder(@NonNull Context context) {
            mAdsConfig = new AdsConfig(context);
        }

        public Builder setMode(@NonNull String mode) {
            if (mode.equalsIgnoreCase(AdsConstants.FLAG_ONLY_ADMOB)) {
                mMode = Mode.ADMOB;
            } else if (mode.equalsIgnoreCase(AdsConstants.FLAG_ONLY_FAN)) {
                mMode = Mode.FAN;
            } else if (mode.equalsIgnoreCase(AdsConstants.FLAG_ADMOB_FAN)) {
                mMode = Mode.ADMOB_FAN;
            } else if (mode.equalsIgnoreCase(AdsConstants.FLAG_FAN_ADMOB)) {
                mMode = Mode.FAN_ADMOB;
            } else if (mode.equalsIgnoreCase(AdsConstants.FLAG_MIX_FAN)) {
                mMode = Mode.MIX_FAN;
            } else if (mode.equalsIgnoreCase(AdsConstants.FLAG_MIX_ADMOB)) {
                mMode = Mode.MIX_ADMOB;
            }
            return this;
        }

        public Builder setAdIds(String[] admobIds, String[] fanIds) {
            mAdmobIds = buildPrefixAdmobIds(admobIds);
            mFanIds = buildPrefixFanIds(fanIds);
            return this;
        }

        public Builder setAdmobIds(String[] admobIds) {
            mAdmobIds = buildPrefixAdmobIds(admobIds);
            return this;
        }

        public Builder setFanIds(String[] fanIds) {
            mFanIds = buildPrefixFanIds(fanIds);
            return this;
        }

        public Builder setTestMode(boolean isTestMode) {
            mAdsConfig.isTestMode = isTestMode;
            return this;
        }

        public Builder setInterOPADelayTime(long splashDelayInMs, long progressDelayInMs) {
            mAdsConfig.mSplashDelayTimeInMs = splashDelayInMs;
            mAdsConfig.mOPAProgressDelayTimeInMs = progressDelayInMs;
            return this;
        }

        public AdsConfig build() {
            if (mMode < 0) {
                throw new IllegalStateException("Please set mode for AdsConfig!");
            }
            buildAdsIdsByMode();
            mAdsConfig.mMode = mMode;
            return mAdsConfig;
        }

        private void buildAdsIdsByMode() {
            String[] ids = null;
            if (mMode == Mode.ADMOB) {
                if (mAdmobIds == null) {
                    throw new NullPointerException("Admob ids must not be null!");
                }
                ids = mAdmobIds;
            } else if (mMode == Mode.FAN) {
                if (mFanIds == null) {
                    throw new NullPointerException("FAN ids must not be null!");
                }
                ids = mFanIds;
            } else if (mMode == Mode.ADMOB_FAN) {
                List<String> adsIds = new ArrayList<>();
                if (mAdmobIds != null) {
                    adsIds.addAll(Arrays.asList(mAdmobIds));
                }
                if (mFanIds != null) {
                    adsIds.addAll(Arrays.asList(mFanIds));
                }
                if (!UtilsLib.isEmptyList(adsIds)) {
                    ids = adsIds.toArray(new String[0]);
                }
            } else if (mMode == Mode.FAN_ADMOB) {
                List<String> adsIds = new ArrayList<>();
                if (mFanIds != null) {
                    adsIds.addAll(Arrays.asList(mFanIds));
                }
                if (mAdmobIds != null) {
                    adsIds.addAll(Arrays.asList(mAdmobIds));
                }
                if (!UtilsLib.isEmptyList(adsIds)) {
                    ids = adsIds.toArray(new String[0]);
                }
            } else if (mMode == Mode.MIX_FAN) {  // Mix Ads IDs, xen kẽ IDs (FAN trước, Admob sau)
                List<String> adsIds = new ArrayList<>();

                List<String> admobIds = new ArrayList<>();
                List<String> fanIds = new ArrayList<>();
                if (mFanIds != null) {
                    fanIds = Arrays.asList(mFanIds);
                }
                if (mAdmobIds != null) {
                    admobIds = Arrays.asList(mAdmobIds);
                }

                // Mix Ads ids của FAN và Admob
                for (int i = 0; i < fanIds.size(); i++) {
                    adsIds.add(fanIds.get(i));
                    if (i < admobIds.size()) {
                        adsIds.add(admobIds.get(i));
                    }
                }

                // Nếu số lượng ids của Admob nhiều hơn thì thêm nốt vào danh sách Ads ids
                if (fanIds.size() < admobIds.size()) {
                    adsIds.addAll(admobIds.subList(fanIds.size(), admobIds.size()));
                }

                if (!UtilsLib.isEmptyList(adsIds)) {
                    ids = adsIds.toArray(new String[0]);
                }
            } else if (mMode == Mode.MIX_ADMOB) { // Mix Ads IDs, xen kẽ IDs (Admob trước, FAN sau)
                List<String> adsIds = new ArrayList<>();

                List<String> admobIds = new ArrayList<>();
                List<String> fanIds = new ArrayList<>();
                if (mFanIds != null) {
                    fanIds = Arrays.asList(mFanIds);
                }
                if (mAdmobIds != null) {
                    admobIds = Arrays.asList(mAdmobIds);
                }

                // Mix Ads ids của Admob và FAN
                for (int i = 0; i < admobIds.size(); i++) {
                    adsIds.add(admobIds.get(i));
                    if (i < fanIds.size()) {
                        adsIds.add(fanIds.get(i));
                    }
                }

                // Nếu số lượng ids của FAN nhiều hơn thì thêm nốt vào danh sách Ads ids
                if (admobIds.size() < fanIds.size()) {
                    adsIds.addAll(fanIds.subList(admobIds.size(), fanIds.size()));
                }

                if (!UtilsLib.isEmptyList(adsIds)) {
                    ids = adsIds.toArray(new String[0]);
                }
            }

            if (ids == null) {
                throw new NullPointerException("Ads ids must not be null!");
            }

            StringBuilder builder = new StringBuilder("Ads ids:");
            for (String id : ids) {
                builder.append("\n").append(id);
            }
            DebugLog.logd(builder.toString());
            mAdsConfig.mAdsIds = ids;
        }

        private String[] buildPrefixAdmobIds(String[] ids) {
            if (ids != null && ids.length > 0) {
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = AdsConstants.ADMOB_ID_PREFIX + ids[i];
                }
            }
            return ids;
        }

        private String[] buildPrefixFanIds(String[] ids) {
            if (ids != null && ids.length > 0) {
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = AdsConstants.FAN_ID_PREFIX + ids[i];
                }
            }
            return ids;
        }
    }

}
