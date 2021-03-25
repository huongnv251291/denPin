package com.tohsoft.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.text.TextUtils
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tohsoft.app.R

/**
 * Created by Phong on 11/9/2016.
 */
object Utils {
    private var mLastClickTime: Long = 0

    val isAvailableClick: Boolean
        get() {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                return false
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            return true
        }

    @SuppressLint("StaticFieldLeak")
    private var sProgressDialog: MaterialDialog? = null
    fun showProgress(context: Context, message: String?) {
        dismissCurrentDialog()
        sProgressDialog = MaterialDialog.Builder(context)
                .content(if (message == null || message.isEmpty()) context.getString(R.string.msg_please_wait) else message)
                .progress(true, 0)
                .show()
    }

    fun dismissCurrentDialog() {
        if (sProgressDialog != null) {
            if (sProgressDialog!!.isShowing) {
                sProgressDialog!!.dismiss()
            }
            sProgressDialog = null
        }
    }

    fun createAlertDialog(context: Context): MaterialDialog {
        return MaterialDialog.Builder(context)
                .canceledOnTouchOutside(true)
                .positiveText(context.getString(R.string.action_ok))
                .build()
    }

    fun loadImageWithGlide(context: Context?, model: Any?, place_holder: Int, target: ImageView) {
        if (model == null || context == null) {
            return
        }
        val requestOptions = RequestOptions()
                .placeholder(place_holder)
                .error(place_holder)
                .centerCrop()
        Glide.with(context)
                .load(model)
                .apply(requestOptions)
                .into(target)
    }

}