package com.tohsoft.base.mvp.ui;

/**
 * Created by Phong on 11/9/2016.
 */

public interface MvpPresenter<V extends MvpView> {
    void attachView(V mvpView);

    void detachView();
}
