package com.tohsoft.base.mvp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tohsoft.ads.AdsConfig;
import com.tohsoft.ads.AdsModule;
import com.tohsoft.base.mvp.utils.language.LocaleManager;
import com.utility.DebugLog;

/**
 * Created by Phong on 3/24/2017.
 */

public abstract class BaseFragment<P extends MvpPresenter> extends Fragment implements BaseMvpView {
    private BaseActivity mActivity;
    protected Context mContext;
    protected P mPresenter;

    protected abstract BasePresenter onRegisterPresenter();

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    private void initPresenter() {
        try {
            BasePresenter basePresenter = onRegisterPresenter();
            if (basePresenter != null) {
                basePresenter.attachView(this);
                mPresenter = (P) basePresenter;
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setHasOptionsMenu(false);
        initPresenter();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LocaleManager.setLocale(context));
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

    protected void showBannerEmptyScreen(ViewGroup container) {
        AdsModule.getInstance().showBannerEmptyScreen(container);
    }

    protected void showPromotionView(View viewPromotionAds) {
        AdsModule.getInstance().showPromotionAdsView(viewPromotionAds);
    }

    protected void showPromotionAds() {
        AdsModule.getInstance().showPromotionAds();
    }

    @Override
    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    @Override
    public void showLoading(String message) {
        mActivity.showLoading(message);
    }

    @Override
    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    @Override
    public void showAlertDialog(String message) {
        if (mActivity != null) {
            mActivity.showAlertDialog(message);
        }
    }

    @Override
    public void hideAlertDialog() {
        if (mActivity != null) {
            mActivity.hideAlertDialog();
        }
    }

    @Override
    public void onDestroy() {
        if (mActivity != null) {
            mActivity.hideAlertDialog();
        }
        if (mActivity != null) {
            mActivity.hideLoading();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

}
