package com.tohsoft.base.mvp.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tohsoft.ads.AdsModule;
import com.tohsoft.base.mvp.R;
import com.tohsoft.base.mvp.ui.subview.BaseSubView;
import com.tohsoft.base.mvp.ui.subview.LifeCycle;
import com.tohsoft.base.mvp.ui.subview.SubViewLifeCycleHelper;
import com.tohsoft.base.mvp.utils.Util;
import com.tohsoft.base.mvp.utils.language.LocaleManager;
import com.utility.DebugLog;


/**
 * Created by Phong on 11/9/2016.
 */

public abstract class BaseActivity<P extends MvpPresenter> extends AppCompatActivity implements BaseMvpView {
    private MaterialDialog mProgressDialog;
    private MaterialDialog mAlertDialog;
    private SubViewLifeCycleHelper mSubViewLifeCycleHelper;
    protected Context mContext;
    protected P mPresenter;

    protected abstract BasePresenter onRegisterPresenter();

    protected abstract ViewGroup getBottomAdsContainer();

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
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        v.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
            return super.dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.setLocale(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setupWindowAnimations();
        createAlertDialog();
        initPresenter();
    }

    private void createAlertDialog() {
        mAlertDialog = Util.createAlertDialog(this);
    }

    public void attachSubView(BaseSubView baseSubView) {
        if (mSubViewLifeCycleHelper == null) {
            mSubViewLifeCycleHelper = new SubViewLifeCycleHelper();
        }
        mSubViewLifeCycleHelper.attach(baseSubView);
    }

    protected void showBannerBottom(ViewGroup container) {
        AdsModule.getInstance().showBannerBottom(container);
    }

    public void showBannerEmptyScreen(ViewGroup container) {
        AdsModule.getInstance().showBannerEmptyScreen(container);
    }

    @Override
    public void showLoading() {
        hideLoading();
        try {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .content(R.string.msg_please_wait)
                    .progress(true, 0)
                    .show();
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

    @Override
    public void showLoading(String message) {
        hideLoading();
        try {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .content(message)
                    .progress(true, 0)
                    .show();
        } catch (Exception e) {
            DebugLog.loge(e);
        }
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showAlertDialog(String message) {
        hideAlertDialog();
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        mAlertDialog.setContent(message);
        mAlertDialog.show();
    }

    @Override
    public void hideAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateLifeCycleForSubViews(LifeCycle.ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLifeCycleForSubViews(LifeCycle.ON_RESUME);
        showBannerBottom(getBottomAdsContainer());
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLifeCycleForSubViews(LifeCycle.ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateLifeCycleForSubViews(LifeCycle.ON_STOP);
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        hideAlertDialog();
        mAlertDialog = null;
        mProgressDialog = null;
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        updateLifeCycleForSubViews(LifeCycle.ON_DESTROY);
        super.onDestroy();
    }

    private void updateLifeCycleForSubViews(LifeCycle lifeCycle) {
        if (mSubViewLifeCycleHelper != null) {
            mSubViewLifeCycleHelper.onLifeCycle(lifeCycle);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        overridePendingTransition(R.anim.anim_slide_in_from_right, R.anim.anim_fade_out);
    }

}
