package com.tohsoft.app.ui.history;


import com.tohsoft.base.mvp.ui.BaseMvpView;

import java.util.List;

/**
 * Created by PhongNX on 2/10/2020.
 */
public interface HistoryMvpView extends BaseMvpView {

    void showData(List<String> data);
}
