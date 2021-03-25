package com.tohsoft.base.mvp.ui.subview

import java.util.*

/**
 * Created by Phong on 10/26/2017.
 */
class SubViewLifeCycleHelper {
    private val mListSubview: MutableList<BaseSubView> = ArrayList()

    fun attach(baseSubView: BaseSubView) {
        mListSubview.add(baseSubView)
    }

    fun detach(baseSubView: BaseSubView) {
        if (mListSubview.contains(baseSubView)) {
            mListSubview.remove(baseSubView)
        }
    }

    fun onLifeCycle(lifeCycle: LifeCycle) {
        for (i in mListSubview.indices) {
            if (lifeCycle == LifeCycle.ON_CREATE) {
                mListSubview[i].onCreate()
            } else if (lifeCycle == LifeCycle.ON_START) {
                mListSubview[i].onStart()
            } else if (lifeCycle == LifeCycle.ON_RESUME) {
                mListSubview[i].onResume()
            } else if (lifeCycle == LifeCycle.ON_PAUSE) {
                mListSubview[i].onPause()
            } else if (lifeCycle == LifeCycle.ON_STOP) {
                mListSubview[i].onStop()
            } else if (lifeCycle == LifeCycle.ON_DESTROY) {
                mListSubview[i].onDestroy()
            }
        }
    }
}