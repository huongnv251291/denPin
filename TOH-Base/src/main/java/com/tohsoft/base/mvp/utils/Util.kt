package com.tohsoft.base.mvp.utils

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.tohsoft.base.mvp.R

/**
 * Created by Phong on 11/9/2016.
 */
object Util {
    fun createAlertDialog(context: Context): MaterialDialog {
        return MaterialDialog.Builder(context)
                .canceledOnTouchOutside(true)
                .positiveText(context.getString(R.string.action_ok))
                .build()
    }
}