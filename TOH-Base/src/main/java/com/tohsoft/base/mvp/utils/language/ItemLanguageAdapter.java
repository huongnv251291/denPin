package com.tohsoft.base.mvp.utils.language;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tohsoft.base.mvp.databinding.ItemLanguageBinding;
import com.tohsoft.base.mvp.ui.BaseViewHolder;

import java.util.List;

/**
 * Created by PhongNX on 6/30/2020.
 */
public class ItemLanguageAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<String> mLanguageList;
    private int mSelectedIndex;

    int getSelectedIndex() {
        return mSelectedIndex;
    }

    ItemLanguageAdapter(List<String> languageList, int selectedIndex) {
        this.mLanguageList = languageList;
        this.mSelectedIndex = selectedIndex;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLanguageBinding binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    public class ViewHolder extends BaseViewHolder<ItemLanguageBinding> {

        ViewHolder(ItemLanguageBinding binding) {
            super(binding);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            String language = mLanguageList.get(position);

            mBinding.tvItemName.setText(language);
            if (mSelectedIndex == position) {
                mBinding.radioButton.setChecked(true);
            } else {
                mBinding.radioButton.setChecked(false);
            }

            mBinding.llItem.setOnClickListener(v -> {
                mSelectedIndex = position;
                notifyDataSetChanged();
            });
            mBinding.radioButton.setOnClickListener(v -> {
                mSelectedIndex = position;
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mLanguageList.size();
    }
}
