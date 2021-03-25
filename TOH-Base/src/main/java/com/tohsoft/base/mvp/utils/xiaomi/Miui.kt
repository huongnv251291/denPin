package com.tohsoft.base.mvp.utils.xiaomi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.IntentUtils
import com.tohsoft.base.mvp.R
import com.tohsoft.base.mvp.utils.xiaomi.Rom.getProp
import com.utility.SharedPreference

object Miui {
    private const val miui = "ro.miui.ui.version.name"
    private const val miui5 = "V5"
    private const val miui6 = "V6"
    private const val miui7 = "V7"
    private val prop: String?
        private get() = getProp(miui)

    @JvmStatic
    fun requestStartInBackground(mContext: Context): Boolean {
        val prop = prop
        if (Build.MANUFACTURER == "Xiaomi" && !TextUtils.isEmpty(prop)) {
            MaterialDialog.Builder(mContext)
                    .content(R.string.lbl_enable_start_in_background)
                    .negativeText(R.string.action_cancel)
                    .positiveText(R.string.action_enable)
                    .onPositive { dialog: MaterialDialog?, which: DialogAction? -> openManagePermissionMui(mContext) }.show()
            return true
        }
        return false
    }

    @JvmStatic
    fun openManagePermissionMui(context: Context) {
        val prop = prop
        setStartInBackgroundShowed(context, true)
        when (prop) {
            miui5 -> reqForMiui5(context)
            miui6, miui7 -> reqForMiui67(context)
            else -> reqForMiuiAbover8(context)
        }
    }

    @JvmStatic
    fun needManagePermissionMui(): Boolean {
        return Build.MANUFACTURER == "Xiaomi" && !TextUtils.isEmpty(prop)
    }

    private fun reqForMiui5(context: Context) {
        val packageName = context.packageName
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent)
        }
    }

    private fun reqForMiui67(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        intent.putExtra("extra_pkgname", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent)
        }
    }

    private fun reqForMiuiAbover8(context: Context) {
        var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        intent.putExtra("extra_pkgname", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent)
        } else {
            intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setPackage("com.miui.securitycenter")
            intent.putExtra("extra_pkgname", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (IntentUtils.isIntentAvailable(intent)) {
                context.startActivity(intent)
            }
        }
    }

    @JvmStatic
    fun mustToRequestStartInBackground(context: Context?): Boolean {
        return SharedPreference.getBoolean(context, FREF_START_IN_BACKGROUND_SHOWED, true)
    }

    /**
     * Start in background permission on Xiaomi devices (new)
     */
    private const val FREF_START_IN_BACKGROUND_SHOWED = "FREF_START_IN_BACKGROUND_SHOWED"
    fun setStartInBackgroundShowed(context: Context?, isShowed: Boolean) {
        SharedPreference.setBoolean(context, FREF_START_IN_BACKGROUND_SHOWED, isShowed)
    }
}