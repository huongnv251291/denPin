package com.tohsoft.base.mvp.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tohsoft.base.mvp.R;


/**
 * Created by Phong on 11/9/2016.
 */

public class Util {

    public static MaterialDialog createAlertDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .canceledOnTouchOutside(true)
                .positiveText(context.getString(R.string.action_ok))
                .build();
    }

}
