package com.tohsoft.app.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.FragmentUtils;
import com.tohsoft.app.BaseApplication;
import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.base.BaseActivity;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.ui.settings.SettingsFragment;
import com.tohsoft.app.utils.AutoStartManagerUtil;
import com.tohsoft.app.utils.ads.AdViewWrapper;
import com.tohsoft.app.utils.ads.Advertisements;
import com.tohsoft.app.utils.ads.InterstitialOPAHelper;
import com.tohsoft.app.utils.language.LocaleManager;
import com.tohsoft.lib.AppSelfLib;
import com.utility.DebugLog;
import com.utility.RuntimePermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.fr_bottom_banner) FrameLayout frBottomBanner;
    @BindView(R.id.fr_empty_ads) FrameLayout frEmptyAds;
    @BindView(R.id.fr_splash) View frSplash;
    @BindView(R.id.ll_fake_progress) View llFakeProgress;
    @BindView(R.id.iv_warning) View ivWarning;

    private InterstitialOPAHelper mInterstitialOPAHelper;
    private AdViewWrapper mAdViewWrapper;
    private AlertDialog mDialogExitApp;

    @Override
    protected BasePresenter onRegisterPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected ViewGroup getBottomAdsContainer() {
        return frBottomBanner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSelfLib.language = LocaleManager.getLocale(getResources()).getLanguage();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initAds();

        checkAutoStartManager();
    }

    /*
     * Check and init Ads
     * */
    private void initAds() {
        if (BuildConfig.SHOW_AD) {
            frSplash.setVisibility(View.VISIBLE);
            // OPA
            mInterstitialOPAHelper = new InterstitialOPAHelper(getContext(), llFakeProgress, this);
            mInterstitialOPAHelper.initInterstitialOpenApp();
            // Others (Banner, Gift, EmptyScreen...)
            new Handler().postDelayed(() -> {
                // AdView exit dialog
                mAdViewWrapper = new AdViewWrapper();
                mAdViewWrapper.initBannerExitDialog(mContext, null);
                // Empty Ads
                showBannerEmptyScreen(frEmptyAds);
            }, 2000);
        } else {
            checkPermissions();
        }
    }

    /**
     * Kiểm tra và xin cấp quyền chạy service khi app bị kill trên một số dòng máy
     *
     * Start service sau method này {@link com.tohsoft.app.services.BackgroundService}
     */
    private void checkAutoStartManager() {
        if (AutoStartManagerUtil.shouldShowEnableAutoStart(getContext())) {
            ivWarning.setVisibility(View.VISIBLE);
            /*if (AutoStartManagerUtil.canShowWarningIcon(getContext())) {
                // Hiển thị icon warning
            } else {
                // Ẩn icon warning và hiển thị chỗ khác
            }*/
        } else {
            ivWarning.setVisibility(View.GONE);
        }
    }

    private MaterialDialog.SingleButtonCallback enableAutoStartListener = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            ivWarning.setVisibility(View.GONE);
        }
    };

    /*
     * Check runtime permissions & init data
     * */
    @SuppressLint("CheckResult")
    private void checkPermissions() {
        frSplash.setVisibility(View.GONE);
        // Check permission & request
        if (RuntimePermissions.checkAccessStoragePermission(mContext)) {
            mPresenter.initData();
        } else {
            RuntimePermissions.requestStoragePermission(mContext);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RuntimePermissions.RequestCodePermission.REQUEST_CODE_GRANT_STORAGE_PERMISSIONS) {
            checkPermissions();
        }
    }

    @Override
    public void onAdOPACompleted() {
        checkPermissions();
    }

    @Override
    public void checkAndShowFullScreenQuitApp() {
        if (mInterstitialOPAHelper != null) {
            mInterstitialOPAHelper.checkAndShowFullScreenQuitApp();
        } else {
            showExitDialog();
        }
    }

    @Override
    public void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.msg_exit_app);
        @SuppressLint("InflateParams") View exitDialogView = getLayoutInflater().inflate(R.layout.dialog_exit_app, null);
        // Ads
        ViewGroup adsContainer = exitDialogView.findViewById(R.id.fr_ads_container_exit);
        Advertisements.addBannerAdsToContainer(adsContainer, mAdViewWrapper != null ? mAdViewWrapper.getAdView() : null);
        // Checkbox never show again
        CheckBox cbNeverShowAgain = exitDialogView.findViewById(R.id.cb_never_show_again);
        cbNeverShowAgain.setOnCheckedChangeListener((buttonView, isChecked) ->
                ApplicationModules.getInstant().getPreferencesHelper().setShowExitDialog(!isChecked));

        builder.setView(exitDialogView);
        builder.setPositiveButton(R.string.action_yes, (dialog, which) -> {
            mDialogExitApp.dismiss();
            finishApplication();
        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());
        mDialogExitApp = builder.create();
        mDialogExitApp.show();
    }

    private void finishApplication() {
        new Handler().postDelayed(this::finish, 150);
    }

    @Override
    public void hideSplash() {
        if (frSplash != null) {
            frSplash.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInterstitialOPAHelper != null) {
            mInterstitialOPAHelper.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInterstitialOPAHelper != null) {
            mInterstitialOPAHelper.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().clearAllRequest();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            // Có thể show OPA ở đây khi back về từ 1 Fragment nào đó
//            if (mInterstitialOPAHelper != null && mInterstitialOPAHelper.isLoaded()) {
//                mInterstitialOPAHelper.show();
//            }
            return;
        }
        if (mPresenter != null) {
            mPresenter.onBackPressed();
        }
    }

    @OnClick(R.id.btn_settings)
    public void onSettings() {
        FragmentUtils.add(getSupportFragmentManager(), SettingsFragment.newInstance(),
                android.R.id.content, true, R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.iv_warning)
    public void showDialogAutoStartManager() {
        AutoStartManagerUtil.showDialogEnableAutoStart(getContext(), enableAutoStartListener);
    }
}
