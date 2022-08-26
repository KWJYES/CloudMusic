package com.example.cloudmusic.adapter.recyclerview;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemLrcSearchRvBinding;
import com.example.cloudmusic.entity.Lyrics;

import java.util.List;

public class LrcSearchedAdapter extends RecyclerView.Adapter<LrcSearchedAdapter.ViewHolder> {
    private List<Lyrics> lyricsList;
    private ViewGroup parent;

    public LrcSearchedAdapter(List<Lyrics> lyricsList) {
        this.lyricsList = lyricsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemLrcSearchRvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_lrc_search_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        holder.binding.getRoot().setOnLongClickListener(view -> {
            int position = holder.getAdapterPosition();
            if (position != -1) {
                Lyrics lyrics = lyricsList.get(position);
                copySentence(lyrics.getLyrics());
                Toast.makeText(parent.getContext(), "已复制", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lyrics lyrics = lyricsList.get(position);
        holder.binding.setSvm(lyrics);
        Glide.with(parent.getContext()).load(lyrics.getPicUrl())
                .transform(new RoundedCorners(18), new CenterCrop())
                .placeholder(R.drawable.ic_cd_default)
                .into(holder.binding.imageView11);
    }

    @Override
    public int getItemCount() {
        return lyricsList == null ? 0 : lyricsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemLrcSearchRvBinding binding;

        public ViewHolder(@NonNull ItemLrcSearchRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    /**
     * 复制文案到剪切板
     *
     * @param sentence
     */
    private void copySentence(String sentence) {
        ClipboardManager clipboardManager = (ClipboardManager) parent.getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("精美文案", sentence);
        clipboardManager.setPrimaryClip(clip);//放到剪切板
    }
}
