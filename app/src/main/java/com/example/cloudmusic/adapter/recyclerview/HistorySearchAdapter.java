package com.example.cloudmusic.adapter.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemHistorysearchRvBinding;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.utils.callback.HistorySearchItemClickCallback;

import java.util.List;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.ViewHolder> {

    private List<HistorySearch> historySearchList;
    private HistorySearchItemClickCallback itemClickCallback;

    public HistorySearchAdapter(List<HistorySearch> historySearchList) {
        this.historySearchList = historySearchList;
    }

    public void setItemClickCallback(HistorySearchItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorysearchRvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_historysearch_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setSvm(historySearchList.get(position));
    }

    @Override
    public int getItemCount() {
        return historySearchList == null ? 0 : historySearchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistorysearchRvBinding binding;

        public ViewHolder(@NonNull ItemHistorysearchRvBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public class ClickClass {
        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void search(View view) {
            if (itemClickCallback == null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            HistorySearch historySearch = historySearchList.get(position);
            itemClickCallback.onClick(historySearch);
        }
    }
}
