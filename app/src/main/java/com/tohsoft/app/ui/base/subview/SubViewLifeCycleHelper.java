package com.tohsoft.app.ui.base.subview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phong on 10/26/2017.
 */

public class SubViewLifeCycleHelper {
    private final List<BaseSubView> mListSubview = new ArrayList<>();

    public SubViewLifeCycleHelper() {
    }

    public void attach(BaseSubView baseSubView) {
        mListSubview.add(baseSubView);
    }

    public void detach(BaseSubView baseSubView) {
        if (mListSubview.contains(baseSubView)) {
            mListSubview.remove(baseSubView);
        }
    }

    public void onLifeCycle(LifeCycle lifeCycle) {
        if (mListSubview != null) {
            for (int i = 0; i < mListSubview.size(); i++) {
                if (lifeCycle == LifeCycle.ON_CREATE) {
                    mListSubview.get(i).onCreate();
                } else if (lifeCycle == LifeCycle.ON_START) {
                    mListSubview.get(i).onStart();
                } else if (lifeCycle == LifeCycle.ON_RESUME) {
                    mListSubview.get(i).onResume();
                } else if (lifeCycle == LifeCycle.ON_PAUSE) {
                    mListSubview.get(i).onPause();
                } else if (lifeCycle == LifeCycle.ON_STOP) {
                    mListSubview.get(i).onStop();
                } else if (lifeCycle == LifeCycle.ON_DESTROY) {
                    mListSubview.get(i).onDestroy();
                }
            }
        }
    }

}
