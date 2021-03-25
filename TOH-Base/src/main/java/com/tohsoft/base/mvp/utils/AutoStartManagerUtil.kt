package com.tohsoft.base.mvp.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.tohsoft.base.mvp.R
import com.utility.DebugLog
import com.utility.SharedPreference
import com.utility.UtilsLib

/**
 * Util để xin cấp quyền chạy service khi app bị kill trên một số dòng máy
 *
 * Implementation:
 *
 * 1. Call method shouldShowEnableAutoStart() để biết có cần show icon warning nay không (true/false)
 * 2. Nếu icon warning hiển thị -> khi click vào icon warning thì call method showDialogEnableAutoStart() để show dialog yêu cầu user cấp quyền
 * - Implement listener khi call method showDialogEnableAutoStart() để biết user có click vào btn Enable hay không để ẩn icon warning
 * 3. Option: Có thể dùng method canShowWarningIcon() để hạn chế việc hiển thị icon warning làm rối mắt user, thay vì hiển thị icon warning thì có thể hiển thị item Enable background service ở menu
 *
 * Note: Cần phải check logic này trước khi start Service chạy ngầm, chính là service app dùng để check trong method shouldShowEnableAutoStart()
 *
 * Sample logic:
 *
 * if (AutoStartManagerUtil.shouldShowEnableAutoStart(getContext())) {
 * if (AutoStartManagerUtil.canShowWarningIcon(getContext())) {
 * // Hiển thị icon warning
 * } else {
 * // Ẩn icon warning và hiển thị chỗ khác
 * }
 * } else {
 * // Ẩn icon warning
 * }
 */
object AutoStartManagerUtil {
    private const val COUNT_SHOW_WARNING_ICON = "count_show_warning_icon"
    private const val FIRST_APP_INSTALLED = "first_app_installed"
    private val POWER_MANAGER_INTENTS = arrayOf(
            Intent().setComponent(ComponentName("com.mediatek.duraspeed", "com.mediatek.duraspeed.view.RunningBoosterMainActivity")),
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    )

    private fun isFirstAppInstalled(context: Context): Boolean {
        return SharedPreference.getBoolean(context, FIRST_APP_INSTALLED, true)
    }

    fun canShowWarningIcon(context: Context?): Boolean {
        val currentCount = SharedPreference.getInt(context, COUNT_SHOW_WARNING_ICON, 0)
        SharedPreference.setInt(context, COUNT_SHOW_WARNING_ICON, currentCount + 1)
        return currentCount < 2
    }

    /**
     * Phải gọi method này trước khi khởi chạy backgroundService
     */
    @JvmStatic
    fun shouldShowEnableAutoStart(context: Context?, backgroundService: Class<*>?): Boolean {
        if (context == null) {
            return false
        }
        if (hasPowerManagerIntent(context) && isFirstAppInstalled(context)) {
            SharedPreference.setBoolean(context, FIRST_APP_INSTALLED, false)
            DebugLog.loge("The first check shouldShowEnableAutoStart")
            return true
        }
        return if (backgroundService != null && !UtilsLib.isServiceRunning(context, backgroundService)) {
            hasPowerManagerIntent(context)
        } else false
    }

    @JvmStatic
    fun showDialogEnableAutoStart(context: Context, enableButtonListener: SingleButtonCallback?) {
        val builder = MaterialDialog.Builder(context)
                .content(R.string.lbl_enable_auto_start_content)
                .negativeText(R.string.action_cancel)
        if (!Build.MANUFACTURER.toLowerCase().contains("oppo")) {
            builder.positiveText(R.string.action_enable)
                    .onPositive { dialog: MaterialDialog?, which: DialogAction? ->
                        SharedPreference.setInt(context, COUNT_SHOW_WARNING_ICON, 3)
                        startEnableRestartService(context)
                        enableButtonListener?.onClick(dialog!!, which!!)
                    }
        } else {
            builder.negativeText(R.string.action_ok_got_it)
        }
        try {
            builder.build().show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun hasPowerManagerIntent(context: Context): Boolean {
        for (intent in POWER_MANAGER_INTENTS) if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            DebugLog.loge("hasPowerManagerIntent: " + intent.component!!.className)
            return true
        }
        return false
    }

    private fun startEnableRestartService(context: Context) {
        if (hasPowerManagerIntent(context)) {
            for (intent in POWER_MANAGER_INTENTS) if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    DebugLog.loge(e)
                }
                break
            }
        }
    }
}