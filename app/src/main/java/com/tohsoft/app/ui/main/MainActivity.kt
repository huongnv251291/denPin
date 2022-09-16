package com.tohsoft.app.ui.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.ads.MobileAds
import com.simplemobiletools.flashlight.helpers.MyCameraImpl
import com.tohsoft.ads.AdsModule
import com.tohsoft.ads.utils.AdsUtils
import com.tohsoft.ads.wrapper.AdViewWrapper
import com.tohsoft.ads.wrapper.InterstitialOPAHelper
import com.tohsoft.ads.wrapper.InterstitialOPAHelper.InterstitialOPAListener
import com.tohsoft.app.BaseApplication
import com.tohsoft.app.BuildConfig
import com.tohsoft.app.R
import com.tohsoft.app.data.ApplicationModules
import com.tohsoft.app.databinding.ActivityMainBinding
import com.tohsoft.app.databinding.FragmentBrightnessBinding
import com.tohsoft.app.databinding.FragmentHomeBinding
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper
import com.tohsoft.app.services.BackgroundService
import com.tohsoft.app.ui.fragment.home.AdapterSettingLight
import com.tohsoft.app.ui.fragment.home.HomeFragment
import com.tohsoft.app.ui.fragment.home.OnSnapPositionChangeListener
import com.tohsoft.app.ui.helpers.AudioUlti
import com.tohsoft.app.ui.helpers.config
import com.tohsoft.app.utils.commons.Communicate
import com.tohsoft.base.mvp.ui.BaseActivity
import com.tohsoft.base.mvp.utils.AutoStartManagerUtil.shouldShowEnableAutoStart
import com.tohsoft.base.mvp.utils.AutoStartManagerUtil.showDialogEnableAutoStart
import com.tohsoft.base.mvp.utils.language.LocaleManager.getLocale
import com.tohsoft.base.mvp.utils.xiaomi.Miui.mustToRequestStartInBackground
import com.tohsoft.base.mvp.utils.xiaomi.Miui.requestStartInBackground
import com.tohsoft.lib.AppSelfLib
import com.utility.DebugLog
import com.utility.RuntimePermissions
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams

class MainActivity : BaseActivity(), View.OnClickListener, InterstitialOPAListener {
    companion object {
        private const val REQUEST_CODE_HISTORY = 1
    }

    private lateinit var mBinding: ActivityMainBinding
    private var mInterstitialOPAHelper: InterstitialOPAHelper? = null
    private var mAdViewWrapper: AdViewWrapper? = null
    private var mDialogExitApp: AlertDialog? = null
    private var mDialogGetProVersion: MaterialDialog? = null
    private var mRateHandler: Handler? = null
    private var mLoopingLayoutManager: LoopingLayoutManager? = null
    private var mCameraImpl: MyCameraImpl? = null
    private var handler: MyHandler? = null
    override val bottomAdsContainer: ViewGroup
        get() = mBinding.frBottomBanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSelfLib.language = getLocale(resources).language
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        FirebaseRemoteConfigHelper.instance.fetchRemoteData(mContext)
        handler = MyHandler()
        initViews()
        setSplashMargin()

        initAds()

        checkAutoStartManager()
        checkStartInBackgroundPermission()
//        AdsModule.getInstance().showNativeAdView(mBinding.frNativeAdview, arrayOf("native_adview_id"))
    }

    private fun setupCameraImpl() {
        mCameraImpl = MyCameraImpl.newInstance(this)
        if (config.turnFlashlightOn) {
            mCameraImpl!!.enableFlashlight()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }

    inner class MyHandler : Handler() {
        private var firstTime = true
        private var position: Int = 0

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (this.position != msg.what) {
                if (!firstTime) {
                    AudioUlti.getInstance().startRotateDP()
                } else {
                    firstTime = false
                }
                this.position = msg.what
            }
            if (position != 0) {
                var duration = 100;
                when (position) {
                    1 -> duration *= 1
                    2 -> duration *= 2
                    3 -> duration *= 3
                    4 -> duration *= 4
                    5 -> duration *= 5
                    6 -> duration *= 6
                    7 -> duration *= 7
                    8 -> duration *= 8
                    9 -> duration = 2000
                }
                mCameraImpl!!.stroboFrequency = duration.toLong()
                handlerTurnOnorOff(true)
            } else {
                handlerTurnOnorOff(false)
            }
        }
    }

    private fun handlerTurnOnorOff(b: Boolean) {
        if (b) {
            if (mBinding.contentMain.containerPin.switchButton.isChecked) {
                if (!mCameraImpl!!.isSOSRunning()) {
                    stopFlash();
                    mCameraImpl!!.toggleSOS()
                }
            } else {
                stopFlash()

            }
        } else {
            if (mBinding.contentMain.containerPin.switchButton.isChecked) {
                mCameraImpl!!.stopSOS()
                mCameraImpl!!.enableFlashlight()
            } else {
                stopFlash();
            }
        }

    }

    private fun stopFlash() {
        mCameraImpl!!.stopSOS()
        mCameraImpl!!.stopStroboscope()
        mCameraImpl!!.disableFlashlight()
    }

    private fun initViews() {
        mBinding.contentMain.ivWarning.setOnClickListener(this)
        mBinding.contentMain.containerPin.switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeModeflash((mBinding.contentMain.containerPin.recycler.adapter as AdapterSettingLight).mCurrentPosition)
                mBinding.contentMain.containerPin.ivBeam.visibility = View.VISIBLE
                AudioUlti.getInstance().turnOnPin()
            } else {
                stopFlash()
                mBinding.contentMain.containerPin.ivBeam.visibility = View.INVISIBLE
                AudioUlti.getInstance().turnOffPin()
            }
        }
        mLoopingLayoutManager =
            LoopingLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.contentMain.containerPin.recycler.layoutManager = mLoopingLayoutManager
        mBinding.contentMain.containerPin.recycler.adapter =
            AdapterSettingLight(mBinding.contentMain.containerPin.recycler, object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(oldPosition: Int, position: Int) {
                    changeModeflash(position);
                }

            })
        mBinding.contentMain.containerPin.recycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        updateVol();
        mBinding.contentMain.containerPin.btnShare.setOnClickListener(this)
        mBinding.contentMain.containerPin.btnVolume.setOnClickListener(this)
        mBinding.contentMain.containerPin.btnRateStar.setOnClickListener(this)
        mBinding.contentMain.containerPin.btnBrightness.setOnClickListener(this)
    }

    private fun updateVol() {
        if (AudioUlti.getInstance().isHaveSound) {
            mBinding.contentMain.containerPin.btnVolume.setIconEnabled(true)
        } else {
            mBinding.contentMain.containerPin.btnVolume.setIconEnabled(false)
        }
    }

    private fun changeModeflash(position: Int) {
        handler!!.removeCallbacksAndMessages(null)
        val msg = Message.obtain()
        msg.what = position
        handler!!.sendMessageDelayed(msg, 50)
    }

    /*
     * Check and init Ads
     * */
    private fun initAds() {
        if (BuildConfig.SHOW_AD) {
            mBinding.frSplash.visibility = View.VISIBLE

            // Initialize Ads
            MobileAds.initialize(mContext)

            // OPA
            initInterstitialOPA()

            // Others (Banner, Gift, EmptyScreen...)
            Handler().postDelayed({
                // AdView exit dialog
                initBannerExitDialog()
            }, 2000)
        } else {
            checkPermissions()
        }
    }

    private fun initInterstitialOPA() {
        if (BuildConfig.SHOW_AD) {
            mInterstitialOPAHelper = AdsModule.getInstance().getInterstitialOPAHelper(this, this)
            mInterstitialOPAHelper?.initInterstitialOpenApp() ?: onAdOPACompleted()
        }
    }

    private fun initBannerExitDialog() {
        if (BuildConfig.SHOW_AD) {
            mAdViewWrapper = AdsModule.getInstance().bannerExitDialog
            mAdViewWrapper?.initMediumBanner()
        }
    }

    /**
     * Fix lỗi co kéo ảnh splash so với ảnh window background
     */
    private fun setSplashMargin() {
        val layoutParams = mBinding.ivSplash.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = -BarUtils.getStatusBarHeight()
        if (BarUtils.isSupportNavBar()) {
            layoutParams.bottomMargin = -BarUtils.getNavBarHeight()
        }
        mBinding.ivSplash.layoutParams = layoutParams
    }

    /**
     * Kiểm tra và xin cấp quyền chạy service khi app bị kill trên một số dòng máy
     *
     *
     * Start service sau method này [BackgroundService]
     */
    private fun checkAutoStartManager() {
        if (shouldShowEnableAutoStart(context, BackgroundService::class.java)) {
            mBinding.contentMain.ivWarning.visibility = View.VISIBLE
            /*if (AutoStartManagerUtil.canShowWarningIcon(getContext())) {
                // Hiển thị icon warning
            } else {
                // Ẩn icon warning và hiển thị chỗ khác
            }*/
        } else {
            mBinding.contentMain.ivWarning.visibility = View.GONE
        }
    }

    /**
     * Kiểm tra và hiển thị pop-up gợi ý users mua bản PRO
     */
    private fun checkAndShowGetProVersion() {
        if (mDialogGetProVersion != null && mDialogGetProVersion!!.isShowing) {
            return
        }
        if (FirebaseRemoteConfigHelper.instance.proVersionEnable &&
            ApplicationModules.instant.preferencesHelper.canShowGetProVersion()
        ) {
            val builder = MaterialDialog.Builder(mContext)
                .content(R.string.lbl_get_pro_version_title)
                .negativeText(R.string.action_later)
                .positiveText(R.string.action_ok_buy_now)
                .onPositive { _: MaterialDialog?, _: DialogAction? ->
                    ApplicationModules.instant.preferencesHelper.setGetProVersionEnable(false)
                    Communicate.getFullVersion(mContext)
                }
                .neutralText(R.string.action_no_thanks)
                .onNeutral { _: MaterialDialog?, _: DialogAction? ->
                    ApplicationModules.instant.preferencesHelper.setGetProVersionEnable(
                        false
                    )
                }
            mDialogGetProVersion = builder.build()
            mDialogGetProVersion?.show()
        }
    }

    /**
     * Kiểm tra và xin cấp quyền StartInBackground khi startIntent như mở một app khác trên MIUI (Xiaomi devices)
     */
    private fun checkStartInBackgroundPermission() {
        if (!mustToRequestStartInBackground(mContext)) {
            requestStartInBackground(mContext)
        }
    }

    private val enableAutoStartListener = SingleButtonCallback { dialog, which ->
        mBinding.contentMain.ivWarning.visibility = View.GONE
    }

    /*
     * Check runtime permissions & init data
     * */
    @SuppressLint("CheckResult")
    private fun checkPermissions() {
        hideSplash()
        mBinding.llFakeProgress.visibility = View.GONE

        // Check permission & request
        if (RuntimePermissions.checkAccessStoragePermission(mContext)) {
            // Do something to init data
            ToastUtils.showShort("Init data")
        } else {
            RuntimePermissions.requestStoragePermission(mContext)
        }
        checkAndShowGetProVersion()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RuntimePermissions.RequestCodePermission.REQUEST_CODE_GRANT_STORAGE_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else {
                ToastUtils.showLong(getString(R.string.msg_alert_storage_permission_denied))
            }
        }
    }

    override fun onAdOPACompleted() {
        DebugLog.loge("onAdOPACompleted")
        checkPermissions()
    }

    fun checkAndShowFullScreenQuitApp() {
        mInterstitialOPAHelper?.checkAndShowFullScreenQuitApp() ?: showExitDialog()
    }

    private fun dismissExitDialog() {
        if (mDialogExitApp != null && mDialogExitApp!!.isShowing) {
            mDialogExitApp!!.dismiss()
        }
    }

    override fun showExitDialog() {
        dismissExitDialog()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.msg_exit_app)
        @SuppressLint("InflateParams") val exitDialogView =
            layoutInflater.inflate(R.layout.dialog_exit_app, null)
        // Ads
        val adsContainer = exitDialogView.findViewById<ViewGroup>(R.id.fr_ads_container_exit)
        AdsUtils.addAdsToContainer(
            adsContainer,
            if (mAdViewWrapper != null) mAdViewWrapper!!.adView else null
        )
        // Checkbox never show again
        val cbNeverShowAgain = exitDialogView.findViewById<CheckBox>(R.id.cb_never_show_again)
        cbNeverShowAgain.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            ApplicationModules.instant.preferencesHelper.setShowExitDialog(
                !isChecked
            )
        }
        builder.setView(exitDialogView)
        builder.setPositiveButton(R.string.action_yes) { dialog: DialogInterface?, which: Int ->
            mDialogExitApp!!.dismiss()
            finishApplication()
        }
        builder.setNegativeButton(R.string.action_cancel) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        mDialogExitApp = builder.create()
        mDialogExitApp!!.show()
    }

    private fun finishApplication() {
        Handler().postDelayed({ finish() }, 150)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_HISTORY && mInterstitialOPAHelper != null && mInterstitialOPAHelper!!.isLoaded) {
            mInterstitialOPAHelper!!.onResume()
            mInterstitialOPAHelper!!.show()
        }
    }

    override fun hideSplash() {
        mBinding.frSplash.visibility = View.GONE
    }

    override fun onResume() {
        mInterstitialOPAHelper?.onResume()
        super.onResume()
        mCameraImpl!!.handleCameraSetup()
    }

    override fun onPause() {
        mInterstitialOPAHelper?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        AdsModule.getInstance().destroyStaticAds()
        BaseApplication.instance?.clearAllRequest()
        releaseCamera()
        super.onDestroy()
    }

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }

    override fun onBackPressed() {
        if (mInterstitialOPAHelper != null && mInterstitialOPAHelper!!.isCounting) {
            return
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
            return
        }
        onQuitApp()
    }

    override fun finish() {
        AdsModule.getInstance().setIgnoreDestroyStaticAd(false)
        super.finish()
    }

    private fun onQuitApp() {
        val isShowRateDialog = AppSelfLib.showRateActivityNewStyleHighScore(
            mContext,
            1,
            Communicate.EMAIL_COMPANY,
            mContext.getString(R.string.app_name)
        )
        if (isShowRateDialog) {
            dismissExitDialog()
            checkRateDialogStopped()
        } else {
            if (ApplicationModules.instant.preferencesHelper.canShowExitDialog()) {
                checkAndShowFullScreenQuitApp()
            } else {
                finish()
            }
        }
    }

    private fun checkRateDialogStopped() {
        if (mRateHandler == null) {
            mRateHandler = Handler()
        }
        mRateHandler!!.postDelayed(mRateRunnable, 100)
    }

    private val mRateRunnable: Runnable = object : Runnable {
        override fun run() {
            if (AppSelfLib.isStopped()) {
                mRateHandler!!.removeCallbacks(this)
                if (AppSelfLib.canCloseApplication()) {
                    if (AppSelfLib.isCloseWithNoThanks() &&
                        ApplicationModules.instant.preferencesHelper.canShowExitDialog()
                    ) {
                        checkAndShowFullScreenQuitApp()
                    } else {
                        finish()
                    }
                }
            } else {
                mRateHandler!!.postDelayed(this, 100)
            }
        }
    }

    open fun addFragment(fragment: Fragment, isAddStack: Boolean) {
        FragmentUtils.add(
            supportFragmentManager, fragment, R.id.content_main,
            fragment.javaClass.simpleName, isAddStack, R.anim.fade_in,
            R.anim.fade_out, R.anim.fade_in,
            R.anim.fade_out
        )
    }

    open fun removeFragment(fragment: Fragment?) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out, R.anim.fade_in,
            R.anim.fade_out
        )
        transaction.remove(fragment!!).commit()
        manager.popBackStackImmediate()
    }

    open fun popFragment() {
        FragmentUtils.pop(supportFragmentManager)
    }

    private fun requestWriteSettingsPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + applicationInfo.packageName)
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_warning -> showDialogEnableAutoStart(context, enableAutoStartListener)
            R.id.btnShare -> Communicate.shareApps(mContext)
            R.id.btnBrightness -> {
                var mBinding =
                    FragmentBrightnessBinding.inflate(LayoutInflater.from(this), null, false)
                var builder = MaterialDialog.Builder(this).customView(mBinding.root, false);
                val attributes = window.attributes
                var curBrightnessValue =
                    Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                        .toFloat()
                var screen_brightness = Math.round(curBrightnessValue / 25.5).toInt()
                mBinding.seekbarBrightness.setProgress(screen_brightness.toFloat())
                mBinding.seekbarBrightness.setOnSeekChangeListener(object :
                    OnSeekChangeListener {
                    override fun onSeeking(seekParams: SeekParams?) {
                        attributes.screenBrightness = seekParams?.progress?.div(mBinding.seekbarBrightness.max)!!
                        window.attributes = attributes
                    }

                    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {

                    }
                })
                builder.title(R.string.brightness)
                builder.positiveText(R.string.close)
                builder.positiveColor(ContextCompat.getColor(this,R.color.yellow))
                builder.onPositive { dialog, _ ->
                    Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        mBinding.seekbarBrightness.progress + 10
                    )
                    dialog.dismiss()
                }
                builder.show()

            }
            R.id.btnRateStar -> Communicate.rateApp(mContext)
            R.id.btnVolume -> {
                AudioUlti.getInstance().soundChange();
                updateVol()
            }
        }
    }
}