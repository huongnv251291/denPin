package com.tohsoft.app.data.network.helper;

import com.tohsoft.app.data.ApplicationModules;
import com.tohsoft.app.data.network.DataManager;
import com.tohsoft.app.data.network.ApiResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Phong on 3/1/2017.
 */

public class LoginHelper {
    private final DataManager mDataManager;
    private final ApiResult mApiResult;
    private final CompositeDisposable mCompositeDisposable;

    public LoginHelper(CompositeDisposable compositeDisposable, ApiResult apiResult) {
        mCompositeDisposable = compositeDisposable;
        mApiResult = apiResult;
        mDataManager = ApplicationModules.getInstant().getDataManager();
    }

    public void login(String email, String password, String push_key) {
        Disposable disposable = mDataManager.login(email, password, push_key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    if (user != null && mApiResult != null) {
                        mApiResult.onSuccess("");
                    }
                }, throwable -> {
                    if (mApiResult != null) {
                        mApiResult.onError(throwable.getMessage());
                    }
                });

        mCompositeDisposable.add(disposable);
    }
}
