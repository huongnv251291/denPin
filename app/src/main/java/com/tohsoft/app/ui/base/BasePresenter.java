package com.tohsoft.app.ui.base;

import android.content.Context;

/**
 * Created by Phong on 11/9/2016.
 */

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mvpView;
    protected Context mContext;

    public BasePresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        mvpView = null;
    }

    public boolean isViewAttached() {
        return mvpView != null;
    }

    public V getMvpView() {
        return mvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new MvpViewNotAttachedException();
        }
    }

    private static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call MvpPresenter.attachView(MvpView) before requesting data to presenter");
        }
    }
}
