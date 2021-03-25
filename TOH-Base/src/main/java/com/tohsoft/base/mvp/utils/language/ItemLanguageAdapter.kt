package com.tohsoft.base.mvp.utils.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohsoft.base.mvp.databinding.ItemLanguageBinding
import com.tohsoft.base.mvp.ui.BaseViewHolder

/**
 * Created by PhongNX on 6/30/2020.
 */
class ItemLanguageAdapter internal constructor(private val mLanguageList: List<String>, var selectedIndex: Int) :
        RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder internal constructor(binding: ItemLanguageBinding) :
            BaseViewHolder<ItemLanguageBinding>(binding) {
        override fun onBind(position: Int) {
            super.onBind(position)
            val language = mLanguageList[position]
            mBinding.tvItemName.text = language
            mBinding.radioButton.isChecked = selectedIndex == position

            mBinding.llItem.setOnClickListener { v: View? ->
                selectedIndex = position
                notifyDataSetChanged()
            }
            mBinding.radioButton.setOnClickListener { v: View? ->
                selectedIndex = position
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return mLanguageList.size
    }
}