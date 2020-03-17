package com.tohsoft.app.ui.main;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.tohsoft.app.R;
import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.utils.commons.Communicate;
import com.tohsoft.lib.AppSelfLib;

/**
 * Created by Phong on 2/2/2017.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    private final Handler mRateHandler;

    MainPresenter(Context context) {
        super(context);
        mRateHandler = new Handler();
    }

    @Override
    public void initData() {
        // Do something
        if (isViewAttached()) {
            getMvpView().hideSplash();
        }
    }

    private void finish() {
        if (mContext != null) {
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void checkRateDialogStopped() {
        mRateHandler.postDelayed(mRateRunnable, 100);
    }

    private Runnable mRateRunnable = new Runnable() {
        @Override
        public void run() {
            if (AppSelfLib.isStopped()) {
                mRateHandler.removeCallbacks(mRateRunnable);
                if (AppSelfLib.canCloseApplication()) {
                    if (AppSelfLib.isCloseWithNoThanks() && isViewAttached() &&
                            ApplicationModules.getInstant().getPreferencesHelper().canShowExitDialog()) {
                        getMvpView().checkAndShowFullScreenQuitApp();
                    } else {
                        finish();
                    }
                }
            } else {
                mRateHandler.postDelayed(this, 100);
            }
        }
    };

}
