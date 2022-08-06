package com.example.cloudmusic.adapter.recyclerview;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.callback.SearchWordsItemClickCallback;
import com.example.cloudmusic.databinding.ItemHotlistBinding;
import com.example.cloudmusic.entity.SearchWord;

import java.util.List;

public class HotListAdapter extends RecyclerView.Adapter<HotListAdapter.ViewHolder> {
    private List<SearchWord> searchWordList;
    private SearchWordsItemClickCallback itemClickCallback;
    private ViewGroup parent;

    public HotListAdapter(List<SearchWord> searchWordList) {
        this.searchWordList = searchWordList;
    }

    public void setItemClickCallback(SearchWordsItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemHotlistBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_hotlist,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setPosition(position + 1);
        holder.binding.setSvm(searchWordList.get(position));
        if (position < 3) {
            holder.binding.position.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.main_color));
            holder.binding.searchWords.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        }
    }

    @Override
    public int getItemCount() {
        return searchWordList == null ? 0 : searchWordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemHotlistBinding binding;

        public ViewHolder(@NonNull ItemHotlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ClickClass {
        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void onItemClick(View view) {
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            SearchWord searchWord = searchWordList.get(position);
            if (itemClickCallback != null)
                itemClickCallback.onItemClick(searchWord);
        }
    }
}
