package com.tohsoft.base.mvp.utils.xiaomi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.IntentUtils;
import com.tohsoft.base.mvp.R;
import com.utility.SharedPreference;


public class Miui {

    private static final String miui = "ro.miui.ui.version.name";
    private static final String miui5 = "V5";
    private static final String miui6 = "V6";
    private static final String miui7 = "V7";


    private static String getProp() {
        return Rom.getProp(miui);
    }

    public static boolean requestStartInBackground(final Context mContext) {
        String prop = getProp();
        if (Build.MANUFACTURER.equals("Xiaomi") && !TextUtils.isEmpty(prop)) {
            new MaterialDialog.Builder(mContext)
                    .content(R.string.lbl_enable_start_in_background)
                    .negativeText(R.string.action_cancel)
                    .positiveText(R.string.action_enable)
                    .onPositive((dialog, which) -> openManagePermissionMui(mContext)).show();
            return true;
        }
        return false;
    }

    public static void openManagePermissionMui(Context context) {
        String prop = getProp();
        setStartInBackgroundShowed(context, true);
        switch (prop) {
            case miui5:
                reqForMiui5(context);
                break;
            case miui6:
            case miui7:
                reqForMiui67(context);
                break;
            default:
                reqForMiuiAbover8(context);
                break;
        }
    }

    public static boolean needManagePermissionMui() {
        return Build.MANUFACTURER.equals("Xiaomi") && !TextUtils.isEmpty(getProp());
    }

    private static void reqForMiui5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent);
        }
    }

    private static void reqForMiui67(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent);
        }
    }

    private static void reqForMiuiAbover8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (IntentUtils.isIntentAvailable(intent)) {
            context.startActivity(intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (IntentUtils.isIntentAvailable(intent)) {
                context.startActivity(intent);
            }
        }
    }

    public static boolean mustToRequestStartInBackground(Context context) {
        return SharedPreference.getBoolean(context, FREF_START_IN_BACKGROUND_SHOWED, true);
    }

    /**
     * Start in background permission on Xiaomi devices (new)
     */
    private static final String FREF_START_IN_BACKGROUND_SHOWED = "FREF_START_IN_BACKGROUND_SHOWED";

    public static void setStartInBackgroundShowed(Context context, boolean isShowed) {
        SharedPreference.setBoolean(context, FREF_START_IN_BACKGROUND_SHOWED, isShowed);
    }

}
