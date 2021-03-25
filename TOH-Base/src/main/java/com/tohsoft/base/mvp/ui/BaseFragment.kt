package com.tohsoft.base.mvp.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.tohsoft.ads.AdsModule
import com.tohsoft.base.mvp.utils.language.LocaleManager

/**
 * Created by Phong on 3/24/2017.
 */
abstract class BaseFragment : Fragment() {
    private var mBaseActivity: BaseActivity? = null
    protected lateinit var mContext: Context

    protected fun <M : ViewModel> getViewModel(modelClass: Class<M>): M {
        return ViewModelProviders.of(activity!!).get(modelClass)
    }

    protected fun <M : ViewModel> getActivityViewModel(modelClass: Class<M>): M {
        return ViewModelProviders.of(activity!!).get(modelClass)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context!!
        setHasOptionsMenu(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(LocaleManager.setLocale(context))
        if (context is BaseActivity) {
            mBaseActivity = context
        }
    }

    protected fun showBannerEmptyScreen(container: ViewGroup?) {
        AdsModule.getInstance().showBannerEmptyScreen(container)
    }

    protected fun showPromotionView(viewPromotionAds: View?) {
        AdsModule.getInstance().showPromotionAdsView(viewPromotionAds)
    }

    protected fun showPromotionAds() {
        AdsModule.getInstance().showPromotionAds()
    }

    fun showLoading() {
        mBaseActivity?.showLoading()
    }

    fun showLoading(message: String?) {
        mBaseActivity?.showLoading(message)
    }

    fun hideLoading() {
        mBaseActivity?.hideLoading()
    }

    fun showAlertDialog(message: String?) {
        mBaseActivity?.showAlertDialog(message)
    }

    fun hideAlertDialog() {
        mBaseActivity?.hideAlertDialog()
    }

    override fun onDestroy() {
        mBaseActivity?.hideAlertDialog()
        mBaseActivity?.hideLoading()
        super.onDestroy()
    }
}