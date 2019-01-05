package com.tohsoft.app.ui.base;

import android.content.Context;

/**
 * Created by Phong on 11/9/2016.
 */

public interface BaseMvpView extends MvpView {

    Context getContext();

    void showLoading();

    void showLoading(String message);

    void hideLoading();

    void showAlertDialog(String message);

    void hideAlertDialog();

}
