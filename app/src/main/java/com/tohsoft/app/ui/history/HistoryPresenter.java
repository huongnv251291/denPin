package com.tohsoft.app.ui.history;

import android.content.Context;


import com.tohsoft.base.mvp.ui.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongNX on 2/10/2020.
 */
public class HistoryPresenter<V extends HistoryMvpView> extends BasePresenter<V> implements HistoryMvpPresenter<V> {

    HistoryPresenter(Context context) {
        super(context);
    }

    @Override
    public void getData() {
        // Do something
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("History item " + i);
        }
        if (isViewAttached()) {
            getMvpView().showData(data);
        }
    }

    @Override
    public void clearData() {
        // Do something
        if (isViewAttached()) {
            getMvpView().showData(new ArrayList<>());
        }
    }
}
