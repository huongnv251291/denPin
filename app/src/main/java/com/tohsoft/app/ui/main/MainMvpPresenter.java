package com.tohsoft.app.ui.main;

import com.tohsoft.app.ui.base.MvpPresenter;

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void initData();

    void onBackPressed();
}
