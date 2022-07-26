package com.example.cloudmusic.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.databinding.ItemMusicRecyclerviewBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    List<Song> songList;
    private ViewGroup parent;
    private SongListItemOnClickCallback clickCallback;
    private LinearLayoutManager layoutManager;
    private Song currentSong;
    private SongListItemRemoveCallback removeCallback;
    // private MusicAdapter.ViewHolder currentHolder;


    public void setRemoveCallback(SongListItemRemoveCallback removeCallback) {
        this.removeCallback = removeCallback;
    }

    public void setClickCallback(SongListItemOnClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public MusicAdapter(List<Song> songList) {
        this.songList = songList;
    }

    public MusicAdapter(List<Song> songList, LinearLayoutManager layoutManager) {
        this.songList = songList;
        this.layoutManager = layoutManager;
        currentSong=MediaManager.getInstance().getCurrentSong();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemMusicRecyclerviewBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_music_recyclerview,
                parent,
                false);
        ViewHolder holder = new ViewHolder(itemBinding);
        holder.itemBinding.setPosition(holder.getAdapterPosition());
        itemBinding.setClick(new ItemClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.itemBinding.setPosition(holder.getAdapterPosition() + 1);
        holder.itemBinding.setSvm(song);
        if (song.equals(currentSong)) {
            holder.itemBinding.songName.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.main_color));
            holder.itemBinding.positionTV.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.main_color));
        }
    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMusicRecyclerviewBinding itemBinding;

        public ViewHolder(@NonNull ItemMusicRecyclerviewBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
            itemBinding.songName.setSelected(true);
        }
    }

    public class ItemClickClass {
        public ViewHolder holder;

        public ItemClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void playSong(View view) {
            int position = holder.getAdapterPosition();
            if (position == -1) return;//视图刷新时点击，position为-1
            Song song = songList.get(position);
            //换歌变色
            View currentItem = layoutManager.findViewByPosition(songList.indexOf(currentSong));
            if (currentItem != null) {
                TextView songName = currentItem.findViewById(R.id.songName);
                TextView positionTV = currentItem.findViewById(R.id.positionTV);
                songName.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.black));
                positionTV.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.song_list_position));
            }
            holder.itemBinding.songName.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.main_color));
            holder.itemBinding.positionTV.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.main_color));
            clickCallback.onClick(song);
            currentSong=song;
        }

        public void removeSong(View view) {
            int position = holder.getAdapterPosition();
            if (position == -1) return;//视图刷新时点击，position为-1
            Song song = songList.get(position);
            String name = song.getName();
            songList.remove(song);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            removeCallback.onRemove(song);
        }
    }
}
