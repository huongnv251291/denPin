package com.tohsoft.base.mvp.ui

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.tohsoft.ads.AdsConfig
import com.tohsoft.ads.AdsModule
import com.tohsoft.base.mvp.R
import com.tohsoft.base.mvp.ui.subview.BaseSubView
import com.tohsoft.base.mvp.ui.subview.LifeCycle
import com.tohsoft.base.mvp.ui.subview.SubViewLifeCycleHelper
import com.tohsoft.base.mvp.utils.Util
import com.tohsoft.base.mvp.utils.language.LocaleManager
import com.utility.DebugLog

/**
 * Created by Phong on 11/9/2016.
 */
abstract class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: MaterialDialog? = null
    private var mAlertDialog: MaterialDialog? = null
    private var mSubViewLifeCycleHelper: SubViewLifeCycleHelper? = null
    lateinit var mContext: Context

    abstract val bottomAdsContainer: ViewGroup?

    protected fun <M : ViewModel> getViewModel(modelClass: Class<M>): M {
        return ViewModelProviders.of(this).get(modelClass)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        try {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
            return super.dispatchTouchEvent(event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.setLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = LocaleManager.setLocale(this)
        setupWindowAnimations()
        createAlertDialog()
    }

    private fun createAlertDialog() {
        mAlertDialog = Util.createAlertDialog(this)
    }

    fun attachSubView(baseSubView: BaseSubView) {
        if (mSubViewLifeCycleHelper == null) {
            mSubViewLifeCycleHelper = SubViewLifeCycleHelper()
        }
        mSubViewLifeCycleHelper!!.attach(baseSubView)
    }

    protected fun showBannerBottom(container: ViewGroup?) {
        AdsModule.getInstance().showBannerBottom(container)
    }

    protected fun showBannerEmptyScreen(container: ViewGroup?) {
        AdsModule.getInstance().showBannerEmptyScreen(container)
    }

    protected fun showPromotionView(viewPromotionAds: View?) {
        if (!AdsConfig.getInstance().isFullVersion) {
            AdsModule.getInstance().showPromotionAdsView(viewPromotionAds)
        }
    }

    protected fun showPromotionAds() {
        AdsModule.getInstance().showPromotionAds()
    }

    fun showLoading() {
        hideLoading()
        try {
            mProgressDialog = MaterialDialog.Builder(this)
                    .content(R.string.msg_please_wait)
                    .progress(true, 0)
                    .show()
        } catch (e: Exception) {
            DebugLog.loge(e)
        }
    }

    fun showLoading(message: String?) {
        hideLoading()
        try {
            mProgressDialog = MaterialDialog.Builder(this)
                    .content(message!!)
                    .progress(true, 0)
                    .show()
        } catch (e: Exception) {
            DebugLog.loge(e)
        }
    }

    fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun showAlertDialog(message: String?) {
        hideAlertDialog()
        if (message == null || message.trim { it <= ' ' }.isEmpty()) {
            return
        }
        mAlertDialog!!.setContent(message)
        mAlertDialog!!.show()
    }

    fun hideAlertDialog() {
        if (mAlertDialog != null && mAlertDialog!!.isShowing) {
            mAlertDialog!!.dismiss()
        }
    }

    val context: Context
        get() = this

    override fun onStart() {
        super.onStart()
        updateLifeCycleForSubViews(LifeCycle.ON_START)
    }

    override fun onResume() {
        super.onResume()
        updateLifeCycleForSubViews(LifeCycle.ON_RESUME)
        showBannerBottom(bottomAdsContainer)
    }

    override fun onPause() {
        super.onPause()
        updateLifeCycleForSubViews(LifeCycle.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        updateLifeCycleForSubViews(LifeCycle.ON_STOP)
    }

    override fun onDestroy() {
        hideLoading()
        hideAlertDialog()
        mAlertDialog = null
        mProgressDialog = null
        updateLifeCycleForSubViews(LifeCycle.ON_DESTROY)
        super.onDestroy()
    }

    private fun updateLifeCycleForSubViews(lifeCycle: LifeCycle) {
        if (mSubViewLifeCycleHelper != null) {
            mSubViewLifeCycleHelper!!.onLifeCycle(lifeCycle)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupWindowAnimations() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        overridePendingTransition(R.anim.anim_slide_in_from_right, R.anim.anim_fade_out)
    }
}