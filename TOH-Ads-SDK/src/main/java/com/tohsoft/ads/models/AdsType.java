package com.tohsoft.ads.models;

/**
 * Created by PhongNX on 6/9/2020.
 */
public enum AdsType {
    UNKNOWN("unknown"),
    STD_BANNER("std_banner"),
    BANNER_EXIT_DIALOG("banner_exit_dialog"),
    BANNER_EMPTY_SCREEN("banner_empty_screen"),
    INTERSTITIAL_OPA("interstitial_opa"),
    INTERSTITIAL_GIFT("interstitial_gift");

    String value;

    public String getValue() {
        return value;
    }

    AdsType(String value) {
        this.value = value;
    }
}
