package com.tohsoft.app.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ConvertUtils;
import com.tohsoft.app.BuildConfig;
import com.tohsoft.app.R;
import com.tohsoft.app.utils.ads.AdViewWrapper;
import com.tohsoft.app.utils.ads.AdsConstants;

/**
 * Created by PhongNX on 2/10/2020.
 */
public class EmptyAdView extends LinearLayout {
    private TextView mEmptyTextView;
    private FrameLayout mAdContainer;

    public EmptyAdView(Context context) {
        this(context, null);
    }

    public EmptyAdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmptyAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context == null) {
            return;
        }
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptyAdView);

        mEmptyTextView = new TextView(getContext());
        mAdContainer = new FrameLayout(getContext());

        int padding = ConvertUtils.dp2px(8);
        mEmptyTextView.setPadding(padding, padding, padding, padding);
        mEmptyTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mAdContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addView(mEmptyTextView);
        addView(mAdContainer);

        mAdContainer.setVisibility(View.GONE);

        if (typedArray != null) {
            CharSequence msg = typedArray.getText(R.styleable.EmptyAdView_emptyText);
            if (TextUtils.isEmpty(msg)) {
                msg = context.getString(R.string.msg_empty_data);
            }
            mEmptyTextView.setText(msg);
            mEmptyTextView.setTextColor(typedArray.getColor(R.styleable.EmptyAdView_emptyTextColor, Color.BLACK));
            typedArray.recycle();
        }
    }

    public void showEmptyAd() {
        if (mAdContainer != null) {
            if (BuildConfig.SHOW_AD) {
                if (AdsConstants.bannerEmptyScreen == null) {
                    AdsConstants.bannerEmptyScreen = new AdViewWrapper();
                }
                AdsConstants.bannerEmptyScreen.initEmptyAdView(getContext().getApplicationContext(), mAdContainer);
            } else {
                mAdContainer.setVisibility(View.GONE);
            }
        }
    }

    public void hideEmptyAd() {
        if (mAdContainer != null) {
            mAdContainer.removeAllViews();
            mAdContainer.setVisibility(View.GONE);
        }
    }

    public void setMessage(String msg) {
        if (mEmptyTextView != null && msg != null) {
            mEmptyTextView.setText(msg);
        }
    }

    public void setMessage(int resString) {
        if (mEmptyTextView != null && resString > 0) {
            mEmptyTextView.setText(getContext().getString(resString));
        }
    }

    public void setTextColor(int color) {
        if (mEmptyTextView != null) {
            mEmptyTextView.setTextColor(color);
        }
    }

}
