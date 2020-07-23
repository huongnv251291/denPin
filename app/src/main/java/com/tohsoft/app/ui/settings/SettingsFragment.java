package com.tohsoft.app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.helper.FirebaseRemoteConfigHelper;
import com.tohsoft.app.ui.main.MainActivity;
import com.tohsoft.app.utils.Utils;
import com.tohsoft.app.utils.commons.Communicate;
import com.tohsoft.base.mvp.ui.BaseFragment;
import com.tohsoft.base.mvp.ui.BasePresenter;
import com.tohsoft.base.mvp.utils.language.ChangeLanguageHelper;
import com.tohsoft.base.mvp.utils.xiaomi.Miui;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsFragment extends BaseFragment {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ll_promotion_ads) LinearLayout llPromotionAds;
    @BindView(R.id.ll_other_permissions) LinearLayout llOtherPermissions;
    @BindView(R.id.ll_get_pro_version) LinearLayout llGetProVersion;

    private Unbinder mUnbinder;
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Miui.needManagePermissionMui()) {
            llOtherPermissions.setVisibility(View.GONE);
        }
        if (!BuildConfig.FULL_VERSION && FirebaseRemoteConfigHelper.getInstance().getProVersionEnable()) {
            llGetProVersion.setVisibility(View.VISIBLE);
        } else {
            llGetProVersion.setVisibility(View.GONE);
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showPromotionView(llPromotionAds);
    }

    @OnClick({R.id.ll_language, R.id.ll_report_problem, R.id.ll_rate_us, R.id.ll_more_apps, R.id.ll_share_app, R.id.ll_promotion_ads,
            R.id.ll_other_permissions, R.id.ll_get_pro_version})
    public void onViewClicked(View view) {
        if (!Utils.isAvailableClick()) {
            return;
        }
        switch (view.getId()) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
