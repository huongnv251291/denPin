package com.tohsoft.base.mvp.ui;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

/**
 * Created by Phong on 12/13/2017.
 */

public abstract class BaseViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {
    private int mCurrentPosition;
    protected T mBinding;

    public BaseViewHolder(T viewBinding) {
        super(viewBinding.getRoot());
        mBinding = viewBinding;
    }

    public void onBind(int position) {
        mCurrentPosition = position;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
