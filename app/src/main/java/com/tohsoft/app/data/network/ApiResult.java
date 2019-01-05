package com.tohsoft.app.data.network;

/**
 * Created by Phong on 11/9/2016.
 */

public interface ApiResult {
    void onSuccess(String result);

    void onError(String error);
}
