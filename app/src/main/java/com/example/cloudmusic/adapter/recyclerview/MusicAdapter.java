package com.example.cloudmusic.adapter.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemRemoveCallback;
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
    private Toast toast;

    public void setRemoveCallback(SongListItemRemoveCallback removeCallback) {
        this.removeCallback = removeCallback;
    }

    public void setClickCallback(SongListItemOnClickCallback clickCallback) {
        this.clickCallback = clickCallback;
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
        toast = Toast.makeText(parent.getContext(), "⊙▽⊙您操作太快la~", Toast.LENGTH_SHORT);
        ItemMusicRecyclerviewBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_music_recyclerview,
                parent,
                false);
        ViewHolder holder = new ViewHolder(itemBinding);
        holder.itemBinding.setPosition(holder.getAdapterPosition());
        holder.itemBinding.setClick(new ItemClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.itemBinding.setPosition(holder.getAdapterPosition() + 1);
        holder.itemBinding.setSvm(song);
        if (song.getSongId().equals(currentSong.getSongId())) {
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
            int currentSongPos=-1;
            for(int i=0;i< songList.size();i++){
                if(songList.get(i).getSongId().equals(currentSong.getSongId())){
                    currentSongPos=i;
                    break;
                }
            }
            if(CloudMusic.isGettingSongUrl){
                toast.show();
                return;
            }else {
                toast.cancel();
            }
            //换歌变色
            View currentItem = layoutManager.findViewByPosition(currentSongPos);
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
            if(CloudMusic.isGettingSongUrl){
                toast.show();
                return;
            }else {
                toast.cancel();
            }
            songList.remove(song);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            removeCallback.onRemove(song);
        }
    }
}
