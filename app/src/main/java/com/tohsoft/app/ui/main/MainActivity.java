package com.tohsoft.app.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.base.BaseActivity;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.utils.ads.AdViewWrapper;
import com.tohsoft.app.utils.ads.Advertisements;
import com.tohsoft.app.utils.ads.InterstitialOPAHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.fr_bottom_banner) FrameLayout frBottomBanner;
    @BindView(R.id.fr_splash) FrameLayout frSplash;

    private InterstitialOPAHelper mInterstitialOPAHelper;
    private AdViewWrapper mAdViewWrapper;
    private AlertDialog mDialogExitApp;

    @Override
    protected BasePresenter onRegisterPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initAds();
    }

    /*
    * Kiểm tra và khởi tạo Ads
    * */
    private void initAds() {
        if (BuildConfig.SHOW_AD) {
            // OPA
            mInterstitialOPAHelper = new InterstitialOPAHelper(getContext(), null, this);
            mInterstitialOPAHelper.initInterstitialOpenApp();
            // Others (Banner, Gift, EmptyScreen...)
            new Handler().postDelayed(() -> {
                // AdView exit dialog
                mAdViewWrapper = new AdViewWrapper();
                mAdViewWrapper.initBannerExitDialog(mContext, null);
            }, 2000);
        } else {
            checkPermissions();
        }
    }

    /*
     * Check runtime permissions & init data
     * */
    private void checkPermissions() {
        // Check permission & request

        mPresenter.initData();
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
        RelativeLayout llAdsContainer = exitDialogView.findViewById(R.id.ll_ads_container_exit);
        Advertisements.addBannerAdsToContainer(llAdsContainer, mAdViewWrapper != null ? mAdViewWrapper.getAdView() : null);
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
        showBannerBottom(frBottomBanner);
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
