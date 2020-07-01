package com.tohsoft.base.mvp.ui.subview;

/**
 * Created by Phong on 3/24/2017.
 */


import com.tohsoft.base.mvp.ui.BaseMvpView;
import com.tohsoft.base.mvp.ui.MvpView;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View MvpPresenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface SubMvpView extends BaseMvpView {
    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void attachParentMvpView(MvpView mvpView);
}
