package com.tohsoft.app.ui.history;

import com.tohsoft.app.ui.base.MvpPresenter;

/**
 * Created by PhongNX on 2/10/2020.
 */
public interface HistoryMvpPresenter<V extends HistoryMvpView> extends MvpPresenter<V> {

    void getData();

    void clearData();
}
