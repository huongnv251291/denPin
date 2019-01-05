package com.tohsoft.app.data.network;



import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.data.local.preference.PreferencesHelper;
import com.tohsoft.app.data.models.MoreApps;
import com.tohsoft.app.data.models.User;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Phong on 11/9/2016.
 */

public class DataManager {
    private RemoteApiService mRemoteApiService;
    private PreferencesHelper mPreferencesHelper;

    public DataManager(RemoteApiService remoteApiService, PreferencesHelper preferencesHelper) {
        this.mRemoteApiService = remoteApiService;
        this.mPreferencesHelper = preferencesHelper;
    }

    public Observable<User> login(String email, String password, String android_push_key) {
        return mRemoteApiService.login(email, password, android_push_key);
    }

    public Observable<MoreApps> getMoreApps() {
        Map<String, String> params = new HashMap<>();
        params.put("app_id", BuildConfig.APPLICATION_ID);
        return mRemoteApiService.moreApps(params);
    }
}
