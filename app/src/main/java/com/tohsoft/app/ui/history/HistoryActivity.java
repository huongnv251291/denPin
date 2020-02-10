package com.tohsoft.app.ui.history;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tohsoft.app.R;
import com.tohsoft.app.ui.base.BaseActivity;
import com.tohsoft.app.ui.base.BasePresenter;
import com.tohsoft.app.ui.custom.EmptyAdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PhongNX on 2/10/2020.
 * <p>
 * Template cho việc hiển thị list data (hiển thị emptyAd...)
 * <p>
 * Note: Nếu không dùng Activity này thì xóa đi
 */
public class HistoryActivity extends BaseActivity<HistoryMvpPresenter> implements HistoryMvpView {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_data) RecyclerView rvData;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.view_empty_data) EmptyAdView viewEmptyData;
    @BindView(R.id.fr_bottom_banner) FrameLayout frBottomBanner;

    private HistoryItemAdapter mAdapter;

    @Override
    protected BasePresenter onRegisterPresenter() {
        return new HistoryPresenter(getContext());
    }

    @Override
    protected ViewGroup getBottomAdsContainer() {
        return frBottomBanner;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        init();

        mPresenter.getData();
    }

    private void init() {
        mAdapter = new HistoryItemAdapter(mContext, new ArrayList<>());
        rvData.setLayoutManager(new LinearLayoutManager(mContext));
        rvData.setAdapter(mAdapter);

        viewEmptyData.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void checkAndShowEmptyView() {
        if (mAdapter != null) {
            if (mAdapter.isEmpty()) {
                viewEmptyData.setVisibility(View.VISIBLE);
                viewEmptyData.showEmptyAd();
            } else {
                viewEmptyData.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showData(List<String> data) {
        progressBar.setVisibility(View.GONE);
        mAdapter.setData(data);
        checkAndShowEmptyView();
    }

    @OnClick({R.id.btn_get_data, R.id.btn_clear_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_data:
                progressBar.setVisibility(View.VISIBLE);
                mPresenter.getData();
                break;
            case R.id.btn_clear_data:
                mPresenter.clearData();
                break;
        }
    }
}
