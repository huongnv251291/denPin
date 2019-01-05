package com.tohsoft.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tohsoft.app.R;


/**
 * Created by Phong on 11/9/2016.
 */

public class Utils {
    @SuppressLint("StaticFieldLeak")
    private static MaterialDialog sProgressDialog;

    public static void showProgress(Context context, String message) {
        dismissCurrentDialog();
        sProgressDialog = new MaterialDialog.Builder(context)
                .content((message == null || message.isEmpty()) ? context.getString(R.string.msg_please_wait) : message)
                .progress(true, 0)
                .show();
    }

    public static void dismissCurrentDialog() {
        if (sProgressDialog != null) {
            if (sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
            sProgressDialog = null;
        }
    }

    public static MaterialDialog createAlertDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .canceledOnTouchOutside(true)
                .positiveText(context.getString(R.string.action_ok))
                .build();
    }

    public static void showToast(Context context, String message) {
        if (!message.isEmpty()) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(17, 0, 230);
            toast.show();
        }
    }

    public static void loadImageWithGlide(Context context, Object model, int place_holder, ImageView target) {
        if (model == null || context == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(place_holder)
                .error(place_holder)
                .centerCrop();
        Glide.with(context)
                .load(model)
                .apply(requestOptions)
                .into(target);
    }

    public static Drawable getResourceByName(Context context, String name) {
        if (context == null || TextUtils.isEmpty(name)) {
            return null;
        }
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);
    }

}
