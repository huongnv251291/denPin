package com.tohsoft.app.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tohsoft.app.databinding.ItemHistoryBinding;
import com.tohsoft.base.mvp.ui.BaseViewHolder;
import com.utility.UtilsLib;

import java.util.List;

/**
 * Created by PhongNX on 2/10/2020.
 */
public class HistoryItemAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final Context mContext;
    private final List<String> mDataList;

    HistoryItemAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    boolean isEmpty() {
        return UtilsLib.isEmptyList(mDataList);
    }

    public void setData(List<String> data) {
        mDataList.clear();
        if (data != null) {
            mDataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<String> data) {
        if (data != null) {
            mDataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(binding);
    }

    public class ViewHolder extends BaseViewHolder<ItemHistoryBinding> {

        public ViewHolder(ItemHistoryBinding viewBinding) {
            super(viewBinding);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            mBinding.tvContent.setText(mDataList.get(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
