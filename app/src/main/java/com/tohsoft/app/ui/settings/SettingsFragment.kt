package com.tohsoft.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.tohsoft.app.BuildConfig
import com.tohsoft.app.R
import com.tohsoft.app.databinding.FragmentSettingsBinding
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper
import com.tohsoft.app.ui.main.MainActivity
import com.tohsoft.app.utils.Utils
import com.tohsoft.app.utils.commons.Communicate
import com.tohsoft.base.mvp.ui.BaseFragment
import com.tohsoft.base.mvp.utils.language.ChangeLanguageHelper
import com.tohsoft.base.mvp.utils.xiaomi.Miui.needManagePermissionMui
import com.tohsoft.base.mvp.utils.xiaomi.Miui.openManagePermissionMui

class SettingsFragment : BaseFragment(), View.OnClickListener {
    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }
    
    private lateinit var mBinding: FragmentSettingsBinding
    private var mChangeLanguageHelper: ChangeLanguageHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!needManagePermissionMui()) {
            mBinding.llOtherPermissions.visibility = View.GONE
        }
        if (!BuildConfig.FULL_VERSION && FirebaseRemoteConfigHelper.instance.proVersionEnable) {
            mBinding.llGetProVersion.visibility = View.VISIBLE
        } else {
            mBinding.llGetProVersion.visibility = View.GONE
        }
        mBinding.toolbar.setNavigationOnClickListener { v: View? ->
            if (activity != null) {
                activity!!.onBackPressed()
            }
        }
        mBinding.llLanguage.setOnClickListener(this)
        mBinding.llOtherPermissions.setOnClickListener(this)
        mBinding.llGetProVersion.setOnClickListener(this)
        mBinding.llReportProblem.setOnClickListener(this)
        mBinding.llRateUs.setOnClickListener(this)
        mBinding.llMoreApps.setOnClickListener(this)
        mBinding.llShareApp.setOnClickListener(this)
        mBinding.llPromotionAds.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        showPromotionView(mBinding.llPromotionAds)
    }

    override fun onClick(v: View) {
        if (!Utils.isAvailableClick) {
            return
        }
        when (v.id) {
            R.id.ll_language -> {
                if (mChangeLanguageHelper == null) {
                    mChangeLanguageHelper = ChangeLanguageHelper(mContext, null)
                }
                mChangeLanguageHelper!!.changeLanguage(MainActivity::class.java)
            }
            R.id.ll_other_permissions -> openManagePermissionMui(mContext)
            R.id.ll_get_pro_version -> Communicate.getFullVersion(mContext)
            R.id.ll_report_problem -> Communicate.onFeedback(mContext)
            R.id.ll_rate_us -> Communicate.rateApp(mContext)
            R.id.ll_more_apps -> Communicate.onMoreApp(mContext)
            R.id.ll_share_app -> Communicate.shareApps(mContext)
            R.id.ll_promotion_ads -> showPromotionAds()
        }
    }
}