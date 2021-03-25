package com.tohsoft.base.mvp.utils.language

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.tohsoft.ads.AdsModule
import com.tohsoft.base.mvp.R
import com.tohsoft.base.mvp.utils.language.LocaleManager.getLanguage
import com.tohsoft.base.mvp.utils.language.LocaleManager.setNewLocale
import com.utility.DebugLog
import com.utility.SharedPreference
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ChangeLanguageHelper(private val mContext: Context, private val mListener: ChangeLanguageListener?) {
    companion object {
        private const val URL_GET_COUNTRY_CODE_BY_IP = "http://gsp1.apple.com/pep/gcc"
        private const val DEFAULT_COUNTRY_CODE = "US"
        private const val DEFAULT_LANGUAGE = "en"
        private const val KEY_COUNTRY_CODE_BY_IP = "country_code_by_ip"
    }

    private var mMaterialDialog: MaterialDialog? = null

    interface ChangeLanguageListener {
        fun onChangeLanguageSuccess()
    }

    val isDialogShowing: Boolean
        get() = mMaterialDialog != null && mMaterialDialog!!.isShowing

    fun dismissDialog() {
        if (mMaterialDialog != null && mMaterialDialog!!.isShowing) {
            mMaterialDialog!!.dismiss()
        }
    }

    private fun showProgressDialog() {
        dismissDialog()
        val builder = MaterialDialog.Builder(mContext)
                .progress(true, 100)
                .content(R.string.msg_please_wait)
        mMaterialDialog = builder.build()
        mMaterialDialog?.show()
    }

    @SuppressLint("CheckResult")
    fun changeLanguage(mainActivity: Class<*>) {
        if (isDialogShowing) {
            return
        }
        if (TextUtils.isEmpty(getCountryCode(mContext))) {
            showProgressDialog()
        }
        Observable.concat(countryBySim, countryByIp)
                .filter { s: String? -> !TextUtils.isEmpty(s) }
                .first(DEFAULT_COUNTRY_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ countryCode: String ->
                    showDialog(countryCode, mainActivity)
                }) {
                    showDialog(DEFAULT_COUNTRY_CODE, mainActivity)
                }
    }

    private fun showDialog(countryCode: String, mainActivity: Class<*>) {
        val languageKeys = mContext.resources.getStringArray(R.array.key_language_support)
        val languages: MutableList<String> = ArrayList()
        var loc: Locale
        var selected: String? = mContext.getString(R.string.lbl_auto)
        val detectedLanguage = getLanguageFromCountry(mContext, countryCode)
        var hasDetectLanguage = false
        for (key in languageKeys) {
            val spk = key.split("-".toRegex()).toTypedArray()
            loc = if (spk.size > 1) {
                Locale(spk[0], spk[1])
            } else {
                Locale(key)
            }
            if (key.equals(getLanguage(mContext), ignoreCase = true)) {
                selected = loc.getDisplayName(loc)
            }
            if (key.equals(DEFAULT_LANGUAGE, ignoreCase = true)) {
                continue
            }
            if (key.equals(detectedLanguage, ignoreCase = true)) {
                hasDetectLanguage = true
                continue
            }
            languages.add(toDisplayCase(loc.getDisplayName(loc)))
        }
        languages.sort()
        if (!DEFAULT_LANGUAGE.equals(detectedLanguage, ignoreCase = true) && hasDetectLanguage) {
            languages.add(0, toDisplayCase(Locale(detectedLanguage).getDisplayName(Locale(detectedLanguage))))
        }
        languages.add(0, toDisplayCase(Locale(DEFAULT_LANGUAGE).getDisplayName(Locale(DEFAULT_LANGUAGE))))
        languages.add(0, toDisplayCase(mContext.getString(R.string.lbl_auto)))
        var pos = 0
        for (i in languages.indices) {
            if (languages[i].equals(selected, ignoreCase = true)) {
                pos = i
                break
            }
        }
        val selectedPos = pos
        getLanguage(mContext)
        showDialogSelectLanguage(mContext, languages, selectedPos) { dialog: MaterialDialog, which: DialogAction? ->
            var adapter: ItemLanguageAdapter? = null
            if (dialog.recyclerView != null && dialog.recyclerView.adapter is ItemLanguageAdapter) {
                adapter = dialog.recyclerView.adapter as ItemLanguageAdapter?
            }
            if (adapter != null && adapter.selectedIndex != selectedPos) {
                if (adapter.selectedIndex == 0) {
                    setNewLocale(mContext, LocaleManager.MODE_AUTO)
                    restartToApplyLanguage(mainActivity)
                    return@showDialogSelectLanguage
                }
                val selectedLang = languages[adapter.selectedIndex]
                for (key in languageKeys) {
                    var lloc: Locale
                    val spk = key.split("-".toRegex()).toTypedArray()
                    lloc = if (spk.size > 1) {
                        Locale(spk[0], spk[1])
                    } else {
                        Locale(key)
                    }
                    if (selectedLang.equals(lloc.getDisplayName(lloc), ignoreCase = true)) {
                        setNewLocale(mContext, key)
                        restartToApplyLanguage(mainActivity)
                        break
                    }
                }
            }
        }
    }

    private fun showDialogSelectLanguage(context: Context?, languages: List<String>, selectedPos: Int, callbackDone: MaterialDialog.SingleButtonCallback) {
        if (context == null) {
            return
        }
        try {
            dismissDialog()
            val builder = MaterialDialog.Builder(context)
                    .title(R.string.lbl_select_language)
                    .adapter(ItemLanguageAdapter(languages, selectedPos), LinearLayoutManager(context))
                    .positiveText(R.string.action_done)
                    .onPositive(callbackDone)
            mMaterialDialog = builder.build()
            mMaterialDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restartToApplyLanguage(mainActivity: Class<*>) {
        mListener?.onChangeLanguageSuccess()
        val dialog = MaterialDialog.Builder(mContext)
                .content(R.string.msg_restart_to_change_config)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show()
        Handler().postDelayed({
            dialog.dismiss()
            AdsModule.getInstance().setIgnoreDestroyStaticAd(true)
            val intent = Intent(mContext, mainActivity)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mContext.startActivity(intent)
        }, 3000)
    }

    private val countryByIp: Observable<String>
        get() = Observable.create { subscriber: ObservableEmitter<String> ->
            try {
                val countryCode = getCountryCode(mContext)
                if (countryCode.isEmpty()) {
                    try {
                        val response = NetworkCall().makeServiceCall(URL_GET_COUNTRY_CODE_BY_IP)
                        if (response != null && response.isNotEmpty()) {
                            setCountryCode(mContext, response)
                        }
                        if (response != null) {
                            subscriber.onNext(response.toLowerCase())
                        }
                    } catch (e: Exception) {
                        DebugLog.loge(e)
                    }
                } else {
                    subscriber.onNext(countryCode)
                }
            } catch (e: Exception) {
                subscriber.onNext("")
            }
            subscriber.onComplete()
        }

    private val countryBySim: Observable<String>
        get() = Observable.create { subscriber: ObservableEmitter<String> ->
            try {
                val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simCountry: String? = tm.simCountryIso
                if (simCountry != null && simCountry.length == 2) {
                    subscriber.onNext(simCountry.toLowerCase(Locale.US))
                } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                    val networkCountry = tm.networkCountryIso
                    if (networkCountry != null && networkCountry.length == 2) {
                        subscriber.onNext(networkCountry.toLowerCase(Locale.US))
                    }
                }
            } catch (e: Exception) {
                subscriber.onNext("")
            }
            subscriber.onComplete()
        }

    private fun getLanguageFromCountry(context: Context, country: String): String? {
        val countryCode = context.resources.getStringArray(R.array.CountryCodes)
        val languageCodeSupport = listOf(context.resources.getStringArray(R.array.key_language_support))
        for (s in countryCode) {
            val lg = s.split("_".toRegex()).toTypedArray()
            if (lg.size > 1 && lg[0].equals(country, ignoreCase = true) && languageCodeSupport.contains(lg[0])) {
                return lg[0]
            }
        }
        for (s in countryCode) {
            val lg = s.split("_".toRegex()).toTypedArray()
            if (lg.size > 1 && lg[1].equals(country, ignoreCase = true) && languageCodeSupport.contains(lg[0])) {
                return lg[0]
            }
        }
        return null
    }

    private fun toDisplayCase(s: String): String {
        val ACTIONABLE_DELIMITERS = " '-/"
        val sb = StringBuilder()
        var capNext = true
        for (c in s.toCharArray()) {
            val char = if (capNext) Character.toUpperCase(c) else Character.toLowerCase(c)
            sb.append(char)
            capNext = ACTIONABLE_DELIMITERS.indexOf(char) >= 0
        }
        return sb.toString()
    }

    private fun getCountryCode(context: Context): String {
        return SharedPreference.getString(context, KEY_COUNTRY_CODE_BY_IP, "")
    }

    private fun setCountryCode(context: Context, value: String) {
        SharedPreference.setString(context, KEY_COUNTRY_CODE_BY_IP, value)
    }
}