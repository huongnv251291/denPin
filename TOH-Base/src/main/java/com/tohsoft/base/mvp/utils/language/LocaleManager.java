package com.tohsoft.base.mvp.utils.language;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import com.utility.DebugLog;
import com.utility.SharedPreference;

import java.util.Locale;

public class LocaleManager {
    public static String LANGUAGE_SELECTED = "LANGUAGE_SELECTED";
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
        DebugLog.logd("Change language:\nlanguage: " + language + "\nlocale: " + locale.getLanguage());

        return updateResourcesLocaleLegacy(context, locale);
    }

    private static Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }
}