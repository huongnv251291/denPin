package com.tohsoft.app.ui.main;


import com.tohsoft.ads.wrapper.InterstitialOPAHelper;
import com.tohsoft.app.ui.base.BaseMvpView;

/**
 * Created by Phong on 2/2/2017.
 */

public interface MainMvpView extends BaseMvpView, InterstitialOPAHelper.InterstitialOPAListener {

    void checkAndShowFullScreenQuitApp();
}
