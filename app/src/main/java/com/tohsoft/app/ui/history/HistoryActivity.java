package com.tohsoft.app.ui.history;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tohsoft.app.R;
import com.tohsoft.app.databinding.ActivityHistoryBinding;
import com.tohsoft.base.mvp.ui.BaseActivity;
import com.tohsoft.base.mvp.ui.BasePresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PhongNX on 2/10/2020.
 * <p>
 * Template cho việc hiển thị list data (hiển thị emptyAd...)
 * <p>
 * Note: Nếu không dùng Activity này thì xóa đi
 */
public class HistoryActivity extends BaseActivity<HistoryMvpPresenter> implements HistoryMvpView, View.OnClickListener {
    private ActivityHistoryBinding mBinding;
    private HistoryItemAdapter mAdapter;

    @Override
    protected BasePresenter onRegisterPresenter() {
        return new HistoryPresenter(getContext());
    }

    @Override
    protected ViewGroup getBottomAdsContainer() {
        return mBinding.frBottomBanner;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();

        mPresenter.getData();
    }

    private void init() {
        mAdapter = new HistoryItemAdapter(mContext, new ArrayList<>());
        mBinding.rvData.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rvData.setAdapter(mAdapter);

        mBinding.viewEmptyData.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.VISIBLE);

        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mBinding.btnClearData.setOnClickListener(this);
        mBinding.btnGetData.setOnClickListener(this);
    }

    private void checkAndShowEmptyView() {
        if (mAdapter != null) {
            if (mAdapter.isEmpty()) {
                mBinding.viewEmptyData.setVisibility(View.VISIBLE);
                mBinding.viewEmptyData.showEmptyAd();
            } else {
                mBinding.viewEmptyData.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showData(List<String> data) {
        mBinding. progressBar.setVisibility(View.GONE);
        mAdapter.setData(data);
        checkAndShowEmptyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_data:
                mBinding. progressBar.setVisibility(View.VISIBLE);
                mPresenter.getData();
                break;
            case R.id.btn_clear_data:
                mPresenter.clearData();
                break;
        }
    }
}
