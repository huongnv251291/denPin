package com.tohsoft.app.data.network;


import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.data.local.preference.PreferencesHelper;
import com.tohsoft.app.data.models.MoreApps;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

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

    public Single<MoreApps> getMoreApps() {
        Map<String, String> params = new HashMap<>();
        params.put("app_id", BuildConfig.APPLICATION_ID);
        return mRemoteApiService.moreApps(params);
    }
}
