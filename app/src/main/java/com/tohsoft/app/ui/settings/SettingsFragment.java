package com.tohsoft.app.ui.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tohsoft.app.ui.base.BaseFragment;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.utils.commons.Communicate;

public class SettingsFragment extends BaseFragment {

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        * General settings
        * */
        // Action Report problem
        Communicate.onFeedback(mContext);
        // Action Rate app
        Communicate.rateApp(mContext);
        // Action More app
        Communicate.onMoreApp(mContext);
        // Action Share app
        Communicate.shareApps(mContext);
        // Check and show Promotion Ads
        View viewPromotionAds = null; // Thay thế view này bằng view thật
        showPromotionView(viewPromotionAds);
        // Action Promotion Ads
        showPromotionAds();
    }
}
