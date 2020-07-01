package com.tohsoft.app.ui.main;


import com.tohsoft.base.mvp.ui.MvpPresenter;

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void initData();

    void checkRateDialogStopped();
}
