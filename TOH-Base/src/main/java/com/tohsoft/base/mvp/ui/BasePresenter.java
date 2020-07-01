package com.tohsoft.base.mvp.ui;

import android.content.Context;

import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by Phong on 11/9/2016.
 */

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mvpView;
    protected Context mContext;
    protected CompositeDisposable mCompositeDisposable;

    public BasePresenter(Context context) {
        this.mContext = context;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        mvpView = null;
        mCompositeDisposable.clear();
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
