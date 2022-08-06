package com.example.cloudmusic.adapter.recyclerview.searched;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemOneSongBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.utils.callback.OneSongMoreOperateClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;

import java.util.List;

public class OneSongAdapter extends RecyclerView.Adapter<OneSongAdapter.ViewHolder> {

    private List<Song> songList;
    private SongListItemOnClickCallback clickCallback;
    private OneSongMoreOperateClickCallback moreOperateOneSongMoreOperateClickCallback;

    public void setMoreOperateClickCallback(OneSongMoreOperateClickCallback moreOperateOneSongMoreOperateClickCallback) {
        this.moreOperateOneSongMoreOperateClickCallback = moreOperateOneSongMoreOperateClickCallback;
    }

    public OneSongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    public void setClickCallback(SongListItemOnClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOneSongBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_one_song,
                parent,
                false);
        ViewHolder holder=new ViewHolder(binding);
        binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setSvm(songList.get(position));
    }

    @Override
    public int getItemCount() {
        return songList==null?0:songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOneSongBinding binding;
        public ViewHolder(@NonNull ItemOneSongBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public class ClickClass{
        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void itemClick(View view){
            if(clickCallback==null)return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Song song= songList.get(position);
            clickCallback.onClick(song);

        }
        public void moreOperate(View view){
            if(moreOperateOneSongMoreOperateClickCallback ==null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Song song= songList.get(position);
            moreOperateOneSongMoreOperateClickCallback.onClick(song);
        }
    }
}
