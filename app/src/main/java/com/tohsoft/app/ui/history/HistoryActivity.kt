package com.tohsoft.app.ui.history

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohsoft.app.R
import com.tohsoft.app.data.models.History
import com.tohsoft.app.databinding.ActivityHistoryBinding
import com.tohsoft.base.mvp.ui.BaseActivity
import com.utility.DebugLog
import java.util.*

/**
 * Created by PhongNX on 2/10/2020.
 *
 * Template cho việc hiển thị list data (hiển thị emptyAd...)
 * Note: Nếu không dùng Activity này thì xóa đi
 */
class HistoryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityHistoryBinding
    private lateinit var mViewModel: HistoryViewModel
    private var mAdapter: HistoryPagingAdapter? = null
    private var mAdapterSearched: HistoryPagingAdapter? = null

    override val bottomAdsContainer: ViewGroup
        get() = mBinding.frBottomBanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        init()
        registerLiveDataListener()
    }

    private fun init() {
        mViewModel = getViewModel(HistoryViewModel::class.java)

        mAdapter = HistoryPagingAdapter()
        mBinding.rvData.layoutManager = LinearLayoutManager(mContext)
        mBinding.rvData.adapter = mAdapter

        mBinding.viewEmptyData.visibility = View.GONE
        mBinding.progressBar.visibility = View.VISIBLE
        mBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
        mBinding.btnClearData.setOnClickListener(this)
        mBinding.btnGetData.setOnClickListener(this)

        mBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.searchHistory(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mViewModel.searchHistory(newText)
                return true
            }
        })

        mBinding.searchView.setOnCloseListener {
            mBinding.searchView.onActionViewCollapsed()
            mBinding.rvData.adapter = mAdapter
            false
        }

        mBinding.searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (mBinding.rvData.adapter == mAdapter) {
                mAdapterSearched = HistoryPagingAdapter()
                mBinding.rvData.adapter = mAdapterSearched
            }
        }
    }

    private fun registerLiveDataListener() {
        mViewModel.progressState.observe(this, { visible ->
            mBinding.progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        })
        mViewModel.historiesPaged.observe(this, { data ->
            showData(data)
        })
        mViewModel.historiesSearchPaged.observe(this, { data ->
            showSearchedData(data)
        })
    }

    private fun checkAndShowEmptyView(data: PagedList<History>?) {
        if (data == null || data.isEmpty()) {
            mBinding.viewEmptyData.visibility = View.VISIBLE
            mBinding.viewEmptyData.showEmptyAd()
        } else {
            mBinding.viewEmptyData.visibility = View.GONE
        }
    }

    private fun showSearchedData(data: PagedList<History>?) {
        DebugLog.loge("showSearchedData ${data?.size}")
        mAdapterSearched?.submitList(data)
        checkAndShowEmptyView(data)
    }

    private fun showData(data: PagedList<History>?) {
        DebugLog.loge("showData ${data?.size}")
        mAdapter?.submitList(data)
        checkAndShowEmptyView(data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_get_data -> mViewModel.initData()
            R.id.btn_clear_data -> mViewModel.clearData()
        }
    }
}