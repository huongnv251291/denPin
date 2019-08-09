package com.tohsoft.app.utils.language;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.tohsoft.app.BuildConfig;
import com.utility.SharedPreference;

import java.util.Locale;

public class LocaleManager {
    public static String LANGUAGE_SELECTED = BuildConfig.APPLICATION_ID + "LANGUAGE_SELECTED";
    public static String MODE_AUTO = "auto";

    public static Context setLocale(Context c) {
        return updateResources(c, getLanguage(c));
    }

    public static Context setNewLocale(Context c, String language) {
        persistLanguage(c, language);
        return updateResources(c, language);
    }

    public static String getLanguage(Context c) {
        return SharedPreference.getString(c, LANGUAGE_SELECTED, MODE_AUTO);
    }

    @SuppressLint("ApplySharedPref")
    private static void persistLanguage(Context c, String language) {
        SharedPreference.setString(c, LANGUAGE_SELECTED, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale;
        if (language.equals(MODE_AUTO)) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            if (language.equals("zh-rCN")||language.equals("zh")) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else if (language.equals("zh-rTW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else {
                String[] spk = language.split("-");
                if (spk.length > 1) {
                    locale = new Locale(spk[0], spk[1]);
                } else {
                    locale = new Locale(spk[0]);
                }
            }
        }
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }
}