package com.tohsoft.app.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.base.BaseActivity;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.utils.ads.AdViewWrapper;
import com.tohsoft.app.utils.ads.Advertisements;
import com.tohsoft.app.utils.ads.InterstitialOPAHelper;
import com.utility.DebugLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.fr_bottom_banner) FrameLayout frBottomBanner;
    @BindView(R.id.fr_empty_ads) FrameLayout frEmptyAds;
    @BindView(R.id.fr_splash) View frSplash;
    @BindView(R.id.ll_fake_progress) View llFakeProgress;

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
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initAds();
    }

    /*
     * Check and init Ads
     * */
    private void initAds() {
        if (BuildConfig.SHOW_AD) {
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

    /*
     * Check runtime permissions & init data
     * */
    @SuppressLint("CheckResult")
    private void checkPermissions() {
        // Check permission & request
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Permissions granted
                        mPresenter.initData();
                    } else { // Permissions denied
                        ToastUtils.showLong(R.string.msg_alert_storage_permission_denied);
                    }
                }, throwable -> {
                    DebugLog.loge(throwable.getMessage());
                });
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }
        if (mPresenter != null) {
            mPresenter.onBackPressed();
        }
    }

    @OnClick(R.id.fr_splash)
    public void onFakeClick() {}
}
