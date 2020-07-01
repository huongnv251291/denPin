package com.tohsoft.base.mvp.utils.language;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tohsoft.base.mvp.R;
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false));
    }

    public class ViewHolder extends BaseViewHolder {
        private ViewGroup llItem;
        private RadioButton radioButton;
        private TextView tvItemName;

        ViewHolder(View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.ll_item);
            radioButton = itemView.findViewById(R.id.radio_button);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
        }

        @Override
        protected void clear() {
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            String language = mLanguageList.get(position);

            tvItemName.setText(language);
            if (mSelectedIndex == position) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }

            llItem.setOnClickListener(v -> {
                mSelectedIndex = position;
                notifyDataSetChanged();
            });
            radioButton.setOnClickListener(v -> {
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
