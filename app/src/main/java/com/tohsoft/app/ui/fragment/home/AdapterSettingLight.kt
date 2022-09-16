package com.tohsoft.app.ui.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tohsoft.app.R
import com.tohsoft.app.databinding.LayoutItemRecycleviewLoopBinding
import com.tohsoft.base.mvp.ui.BaseViewHolder
import kotlinx.android.synthetic.main.layout_item_recycleview_loop.view.*

class AdapterSettingLight(
    private val recycler: RecyclerView,
    private val onSnapPositionChangeListener: OnSnapPositionChangeListener
) :
    RecyclerView.Adapter<AdapterSettingLight.ViewHolder>(),
    OnSnapPositionChangeListener {
     var mCurrentPosition: Int = 0
    val snapHelper: LinearSnapHelper
    protected var wifiList = ArrayList<Int>()

    init {
        for (i in 1..10) {
            wifiList.add(i)
        }
        snapHelper = LinearSnapHelper()
        recycler.let {

            snapHelper.attachToRecyclerView(recycler)
            recycler.attachSnapHelperWithListener(
                snapHelper,
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                this
            );
        }
        recycler.post {
            recycler.layoutManager!!.scrollToPosition(8)
        }
    }

    inner class ViewHolder(itemView: LayoutItemRecycleviewLoopBinding) :
        BaseViewHolder<LayoutItemRecycleviewLoopBinding>(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)
            itemView.tvStt.text = wifiList[position].toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemRecycleviewLoopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return wifiList.size
    }

    override fun onSnapPositionChange(oldPosition: Int, position: Int) {
        mCurrentPosition = position;
        var holder = recycler.findViewHolderForAdapterPosition(oldPosition)
        if (holder != null) {
            (holder as ViewHolder).let {
                it.itemView.tvStt.setTextColor(
                    ContextCompat.getColor(
                        it.itemView.context,
                        R.color.black
                    )
                )
            }
        }
        holder = recycler.findViewHolderForAdapterPosition(position)
        if (holder != null) {
            (holder as ViewHolder).let {
                it.itemView.tvStt.setTextColor(
                    ContextCompat.getColor(
                        it.itemView.context,
                        R.color.white
                    )
                )
            }
        }
        onSnapPositionChangeListener.onSnapPositionChange(oldPosition, position)
    }
}