package com.tohsoft.app.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.tohsoft.app.data.local.db.AppDatabase
import com.tohsoft.app.data.models.History
import com.tohsoft.app.databinding.ItemHistoryBinding
import com.tohsoft.base.mvp.ui.BaseViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryPagingAdapter : PagedListAdapter<History, HistoryPagingAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(viewBinding: ItemHistoryBinding) : BaseViewHolder<ItemHistoryBinding>(viewBinding) {
        override fun onBind(position: Int) {
            super.onBind(position)
            val history = getItem(position)
            mBinding.tvContent.text = history?.title ?: "Item $position is NULL"

            mBinding.tvContent.setOnClickListener {
                changeHistoryTitle(history, position)
            }
        }
    }

    private fun changeHistoryTitle(history: History?, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            if (history != null) {
                history.title = "${history.title} clicked"
                AppDatabase.getInstance(mContext).historyDao().update(history)

                notifyItemChanged(position)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<History>() {
            // History details may have changed if reloaded from the database,  but ID is fixed.
            override fun areItemsTheSame(oldHistory: History, newHistory: History) = oldHistory.uuid == newHistory.uuid

            override fun areContentsTheSame(oldHistory: History, newHistory: History) = oldHistory == newHistory
        }
    }
}
