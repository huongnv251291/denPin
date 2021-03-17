package com.tohsoft.app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.databinding.FragmentSettingsBinding;
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper;
import com.tohsoft.app.ui.main.MainActivity;
import com.tohsoft.app.utils.Utils;
import com.tohsoft.app.utils.commons.Communicate;
import com.tohsoft.base.mvp.ui.BaseFragment;
import com.tohsoft.base.mvp.ui.BasePresenter;
import com.tohsoft.base.mvp.utils.language.ChangeLanguageHelper;
import com.tohsoft.base.mvp.utils.xiaomi.Miui;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {
    private FragmentSettingsBinding mBinding;
    private ChangeLanguageHelper mChangeLanguageHelper;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter onRegisterPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Miui.needManagePermissionMui()) {
            mBinding.llOtherPermissions.setVisibility(View.GONE);
        }
        if (!BuildConfig.FULL_VERSION && FirebaseRemoteConfigHelper.getInstance().getProVersionEnable()) {
            mBinding.llGetProVersion.setVisibility(View.VISIBLE);
        } else {
            mBinding.llGetProVersion.setVisibility(View.GONE);
        }

        mBinding.toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        mBinding.llLanguage.setOnClickListener(this);
        mBinding.llOtherPermissions.setOnClickListener(this);
        mBinding.llGetProVersion.setOnClickListener(this);
        mBinding.llReportProblem.setOnClickListener(this);
        mBinding.llRateUs.setOnClickListener(this);
        mBinding.llMoreApps.setOnClickListener(this);
        mBinding.llShareApp.setOnClickListener(this);
        mBinding.llPromotionAds.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showPromotionView(mBinding.llPromotionAds);
    }

    @Override
    public void onClick(View v) {
        if (!Utils.isAvailableClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_language:
                if (mChangeLanguageHelper == null) {
                    mChangeLanguageHelper = new ChangeLanguageHelper(mContext, null);
                }
                mChangeLanguageHelper.changeLanguage(MainActivity.class);
                break;
            case R.id.ll_other_permissions:
                Miui.openManagePermissionMui(mContext);
                break;
            case R.id.ll_get_pro_version:
                Communicate.getFullVersion(mContext);
                break;
            case R.id.ll_report_problem:
                Communicate.onFeedback(mContext);
                break;
            case R.id.ll_rate_us:
                Communicate.rateApp(mContext);
                break;
            case R.id.ll_more_apps:
                Communicate.onMoreApp(mContext);
                break;
            case R.id.ll_share_app:
                Communicate.shareApps(mContext);
                break;
            case R.id.ll_promotion_ads:
                showPromotionAds();
                break;
        }
    }
}
