package com.tohsoft.lib;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class RateLibLocaleManager {

    public static Context setLocale(Context c, String language) {
        return updateResources(c, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale;
        if (language.equals("auto")) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            if (language.equals("zh-rCN") || language.equals("zh")) {
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