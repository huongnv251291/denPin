package com.tohsoft.base.mvp.utils.language

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.utility.SharedPreference
import java.util.*

object LocaleManager {
    var LANGUAGE_SELECTED = "LANGUAGE_SELECTED"
    @JvmField
    var MODE_AUTO = "auto"
    fun setLocale(c: Context): Context {
        return updateResources(c, getLanguage(c))
    }

    @JvmStatic
    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(c, language)
        return updateResources(c, language)
    }

    @JvmStatic
    fun getLanguage(c: Context?): String {
        return SharedPreference.getString(c, LANGUAGE_SELECTED, MODE_AUTO)
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(c: Context, language: String) {
        SharedPreference.setString(c, LANGUAGE_SELECTED, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale: Locale = if (language == MODE_AUTO) {
            Resources.getSystem().configuration.locale
        } else {
            if (language == "zh-rCN" || language == "zh") {
                Locale.SIMPLIFIED_CHINESE
            } else if (language == "zh-rTW") {
                Locale.TRADITIONAL_CHINESE
            } else {
                val spk = language.split("-".toRegex()).toTypedArray()
                if (spk.size > 1) {
                    Locale(spk[0], spk[1])
                } else {
                    Locale(spk[0])
                }
            }
        }
        Locale.setDefault(locale)
        return updateResourcesLocaleLegacy(context, locale)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    @JvmStatic
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales[0] else config.locale
    }
}