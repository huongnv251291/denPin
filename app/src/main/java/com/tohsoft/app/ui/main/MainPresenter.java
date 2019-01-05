package com.tohsoft.app.ui.main;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.utils.commons.Communicate;
import com.tohsoft.lib.AppSelfLib;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Phong on 2/2/2017.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {
    private final CompositeDisposable mCompositeDisposable;
    private final Handler mRateHandler;

    MainPresenter(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        mRateHandler = new Handler();
    }

    @Override
    public void detachView() {
        super.detachView();
        mCompositeDisposable.clear();
    }

    @Override
    public void initData() {
        // Do something
        getMvpView().hideSplash();
    }

    @Override
    public void onBackPressed() {
        boolean isShowRateDialog = AppSelfLib.showRateActivityNewStyleHighScore(mContext, 1,
                Communicate.EMAIL_COMPANY, mContext.getString(R.string.app_name));
        if (isShowRateDialog) {
            checkRateDialogStopped();
        } else {
            if (ApplicationModules.getInstant().getPreferencesHelper().canShowExitDialog()) {
                if (getMvpView() != null) {
                    getMvpView().checkAndShowFullScreenQuitApp();
                }
            } else {
                ((Activity) mContext).finish();
            }
        }
    }

    private void checkRateDialogStopped() {
        mRateHandler.postDelayed(mRateRunnable, 100);
    }

    private Runnable mRateRunnable = new Runnable() {
        @Override
        public void run() {
            if (AppSelfLib.isStopped()) {
                mRateHandler.removeCallbacks(mRateRunnable);
                if (AppSelfLib.canCloseApplication()) {
                    ((Activity) mContext).finish();
                }
                AppSelfLib.setStopped(false);
                AppSelfLib.setCloseWithButton(false);
            } else {
                mRateHandler.postDelayed(this, 100);
            }
        }
    };

}
