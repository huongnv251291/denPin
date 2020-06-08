package com.tohsoft.ads.models;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by PhongNX on 6/5/2020.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({Mode.ADMOB, Mode.FAN, Mode.ADMOB_FAN, Mode.FAN_ADMOB, Mode.MIX_FAN, Mode.MIX_ADMOB})
public @interface Mode {
    int ADMOB = 0; // Chỉ sử dụng Admob
    int FAN = 1; // Chỉ sử dụng Admob
    int ADMOB_FAN = 2; // Gọi Admob Ads IDs list trước, load fail thì gọi FAN
    int FAN_ADMOB = 3; // Gọi FAN Ads IDs list trước, load fail thì gọi Admob
    int MIX_FAN = 4; // Mix Ads IDs, xen kẽ IDs (FAN trước, Admob sau)
    int MIX_ADMOB = 5; // Mix Ads IDs, xen kẽ IDs (Admob trước, FAN sau)
}
