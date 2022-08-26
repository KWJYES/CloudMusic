package com.example.cloudmusic.adapter.recyclerview;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemLrcRvBinding;
import com.example.cloudmusic.entity.Lyric;
import com.example.cloudmusic.utils.callback.LrcClickCallback;

import java.util.ArrayList;
import java.util.List;

public class LrcAdapter extends RecyclerView.Adapter<LrcAdapter.ViewHolder> {
    private List<Lyric> lyricList;
    private LrcClickCallback lrcClickCallback;
    private ViewGroup parent;
    private int currentPos = 0;
    private int lastPos = 0;

    public void setLrcClickCallback(LrcClickCallback lrcClickCallback) {
        this.lrcClickCallback = lrcClickCallback;
    }

    public LrcAdapter(List<Lyric> lyricList) {
        this.lyricList = lyricList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemLrcRvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_lrc_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lyric lyric = lyricList.get(position);
        holder.binding.setSvm(lyric);
        if (lyric.getPosition() == currentPos) {
            holder.binding.tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.current_lrc));
            holder.binding.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            holder.binding.tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.normal_lrc));
            holder.binding.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            if (lyric.getPosition() != lastPos) {
                Animation translate = new TranslateAnimation(-50, 0, 0, 0);
                translate.setDuration(300);
                holder.binding.tv.startAnimation(translate);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lyricList == null ? 0 : lyricList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemLrcRvBinding binding;

        public ViewHolder(@NonNull ItemLrcRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tv.setSelected(true);
        }
    }

    public void updateCurrentLrc(int position) {
        if(position==currentPos)return;
        lastPos=currentPos;
        currentPos = position;
        notifyItemChanged(lastPos);
        notifyItemChanged(position);
    }

    public class ClickClass {
        private ViewHolder holder;

        public void onItemClick(View view) {
            if (lrcClickCallback == null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Lyric lyric = lyricList.get(position);
            if (lyric.getSentence().equals(" ")) return;
            lrcClickCallback.onClick(lyric.getTime());
        }

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }
    }

}
