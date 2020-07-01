package com.tohsoft.app.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tohsoft.app.R;
import com.tohsoft.base.mvp.ui.BaseViewHolder;
import com.utility.UtilsLib;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_content) TextView tvContent;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            tvContent.setText(mDataList.get(position));
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
