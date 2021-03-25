package com.tohsoft.app.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ConvertUtils
import com.tohsoft.ads.AdsModule
import com.tohsoft.app.BuildConfig
import com.tohsoft.app.R

/**
 * Created by PhongNX on 2/10/2020.
 */
class EmptyAdView : LinearLayout {
    private lateinit var mEmptyTextView: TextView
    private lateinit var mAdContainer: FrameLayout

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        if (context == null) {
            return
        }
        orientation = VERTICAL
        gravity = Gravity.CENTER
        val typedArray: TypedArray? = context.obtainStyledAttributes(attrs, R.styleable.EmptyAdView)

        mEmptyTextView = TextView(getContext())
        mAdContainer = FrameLayout(getContext())
        val padding = ConvertUtils.dp2px(8f)
        mEmptyTextView.setPadding(padding, padding, padding, padding)
        mEmptyTextView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mAdContainer.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(mEmptyTextView)
        addView(mAdContainer)

        mAdContainer.visibility = GONE
        if (typedArray != null) {
            var msg = typedArray.getText(R.styleable.EmptyAdView_emptyText)
            if (TextUtils.isEmpty(msg)) {
                msg = context.getString(R.string.msg_empty_data)
            }
            mEmptyTextView.text = msg
            mEmptyTextView.setTextColor(typedArray.getColor(R.styleable.EmptyAdView_emptyTextColor, Color.BLACK))
            typedArray.recycle()
        }
    }

    fun showEmptyAd() {
        if (BuildConfig.SHOW_AD) {
            AdsModule.getInstance().showBannerEmptyScreen(mAdContainer)
        } else {
            mAdContainer.visibility = GONE
        }
    }

    fun hideEmptyAd() {
        mAdContainer.removeAllViews()
        mAdContainer.visibility = GONE
    }

    fun setMessage(msg: String?) {
        if (msg != null) {
            mEmptyTextView.text = msg
        }
    }

    fun setMessage(resString: Int) {
        if (resString > 0) {
            mEmptyTextView.text = context.getString(resString)
        }
    }

    fun setTextColor(color: Int) {
        mEmptyTextView.setTextColor(color)
    }

}