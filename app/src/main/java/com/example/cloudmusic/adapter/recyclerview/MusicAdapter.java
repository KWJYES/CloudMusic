package com.example.cloudmusic.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemMusicRecyclerviewBinding;
import com.example.cloudmusic.entity.Song;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    List<Song> songList;
    private ViewGroup parent;

    public MusicAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        ItemMusicRecyclerviewBinding itemBinding= DataBindingUtil.inflate(
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
        Song song= songList.get(position);
        holder.itemBinding.setSvm(song);
        holder.itemBinding.setPosition(holder.getAdapterPosition()+1);

    }

    @Override
    public int getItemCount() {
        return songList==null?0:songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMusicRecyclerviewBinding itemBinding;
        public ViewHolder(@NonNull ItemMusicRecyclerviewBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
            itemBinding.songName.setSelected(true);
        }
    }

    public class ItemClickClass{
        public ViewHolder holder;

        public ItemClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void playSong(View view){
            int position=holder.getAdapterPosition();
            if(position==-1) return;//视图刷新时点击，position为-1
            Song song= songList.get(position);
            Toast.makeText(parent.getContext(), song.getName(), Toast.LENGTH_SHORT).show();
        }
        public void removeSong(View view){
            int position=holder.getAdapterPosition();
            if(position==-1) return;//视图刷新时点击，position为-1
            Song song= songList.get(position);
            String name=song.getName();
            songList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,getItemCount());
            Toast.makeText(parent.getContext(), "remove"+name, Toast.LENGTH_SHORT).show();
        }
    }
}
