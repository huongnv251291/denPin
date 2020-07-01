package com.tohsoft.base.mvp.ui;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Phong on 12/13/2017.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
