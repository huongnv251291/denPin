package com.tohsoft.base.mvp.ui.subview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.tohsoft.base.mvp.ui.BaseActivity

/**
 * Created by Phong on 3/24/2017.
 */
abstract class BaseSubView : FrameLayout {
    private var baseActivity: BaseActivity? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        if (context is BaseActivity) {
            baseActivity = context
            context.attachSubView(this)
        }
    }

    fun showLoading() {
        baseActivity?.showLoading()
    }

    fun showLoading(message: String?) {
        baseActivity?.showLoading(message)
    }

    fun hideLoading() {
        baseActivity?.hideLoading()
    }

    fun showAlertDialog(message: String?) {
        baseActivity?.showAlertDialog(message)
    }

    fun hideAlertDialog() {
        baseActivity?.hideAlertDialog()
    }

    fun onCreate() {}
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}
    fun onDestroy() {
        baseActivity?.hideAlertDialog()
        baseActivity?.hideLoading()
    }

    protected fun init() {}
}