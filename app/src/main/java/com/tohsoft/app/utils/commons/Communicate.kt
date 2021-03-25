package com.tohsoft.app.utils.commons

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.tohsoft.app.BaseApplication
import com.tohsoft.app.BuildConfig
import com.tohsoft.app.R
import com.tohsoft.app.data.ApplicationModules
import com.tohsoft.app.data.models.MoreApps
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper
import com.tohsoft.app.utils.Utils
import com.utility.DebugLog
import com.utility.SharedPreference
import com.utility.UtilsLib
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Phong on 8/29/2017.
 */
object Communicate {
    const val EMAIL_COMPANY = "app@tohsoft.com"
    private const val DEFAULT_MORE_APPS = "developer?id=TOHsoft+Co.,+Ltd"
    private const val MORE_APPS_INFO = "MORE_APPS_INFO"
    private const val PACKAGE_NAME_PRO = BuildConfig.APPLICATION_ID + ".pro"
    private const val SHARE_CONTENT = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
    private var sMoreApp = ""

    fun saveMoreAppsDetails(context: Context?, details: String?) {
        SharedPreference.setString(context, MORE_APPS_INFO, details)
    }

    fun rateApp(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            intent.data = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                context.startActivity(intent)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun moreApps(context: Context) {
        var detailMoreApps = sMoreApp
        if (detailMoreApps.isEmpty()) {
            detailMoreApps = DEFAULT_MORE_APPS
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            intent.data = Uri.parse("market://$detailMoreApps")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                intent.data = Uri.parse("https://play.google.com/store/apps/$detailMoreApps")
                context.startActivity(intent)
            } catch (ignored: Exception) {
            }
        }
    }

    fun getFullVersion(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        var proAppUrl = FirebaseRemoteConfigHelper.instance.proAppUrl
        if (TextUtils.isEmpty(proAppUrl)) {
            proAppUrl = "https://play.google.com/store/apps/details?id=$PACKAGE_NAME_PRO"
        }
        try {
            intent.data = Uri.parse(proAppUrl)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onFeedback(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/email"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_COMPANY))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.lbl_report_problem) + " " + context.getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, "\n\n---- Device Info ----\n${UtilsLib.getInfoDevices(context)}".trimIndent())
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.lbl_report_problem_with)))
    }

    fun shareApps(context: Context?) {
        if (context == null) {
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT, SHARE_CONTENT)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.lbl_share_app)))
    }

    fun onMoreApp(context: Context) {
        if (!TextUtils.isEmpty(sMoreApp)) {
            moreApps(context)
            return
        }
        Utils.showProgress(context, context.getString(R.string.msg_please_wait))
        val disposable = ApplicationModules.instant.dataManager.moreApps
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ moreAppsResult: MoreApps? ->
                    Utils.dismissCurrentDialog()
                    if (moreAppsResult != null) {
                        sMoreApp = moreAppsResult.moreApps
                    }
                    moreApps(context)
                }) { throwable: Throwable ->
                    DebugLog.loge(throwable.message)
                    Utils.dismissCurrentDialog()
                    moreApps(context)
                }
        BaseApplication.instance?.addRequest(disposable)
    }
}