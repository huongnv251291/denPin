package com.tohsoft.app.ui.main;


import com.tohsoft.ads.wrapper.InterstitialOPAHelper;
import com.tohsoft.base.mvp.ui.BaseMvpView;

/**
 * Created by Phong on 2/2/2017.
 */

public interface MainMvpView extends BaseMvpView, InterstitialOPAHelper.InterstitialOPAListener {

    void checkAndShowFullScreenQuitApp();
}
