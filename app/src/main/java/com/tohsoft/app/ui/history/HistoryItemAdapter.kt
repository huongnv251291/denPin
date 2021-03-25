package com.tohsoft.app.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohsoft.app.data.models.History
import com.tohsoft.app.databinding.ItemHistoryBinding
import com.tohsoft.base.mvp.ui.BaseViewHolder
import com.utility.UtilsLib

/**
 * Created by PhongNX on 2/10/2020.
 */
class HistoryItemAdapter internal constructor(private val mDataList: MutableList<History>) :
        RecyclerView.Adapter<BaseViewHolder<*>>() {
    val isEmpty: Boolean
        get() = UtilsLib.isEmptyList(mDataList)

    fun setData(data: List<History>?) {
        mDataList.clear()
        if (data != null) {
            mDataList.addAll(data)
        }
        notifyDataSetChanged()
    }

    fun addData(data: List<History>?) {
        if (data != null) {
            mDataList.addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(viewBinding: ItemHistoryBinding) : BaseViewHolder<ItemHistoryBinding>(viewBinding) {
        override fun onBind(position: Int) {
            super.onBind(position)
            mBinding.tvContent.text = mDataList[position].title
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        mDataList[position]?.let {
            holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }
}