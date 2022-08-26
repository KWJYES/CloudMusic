package com.example.cloudmusic.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemMusiclistRecommendBinding;
import com.example.cloudmusic.databinding.ItemPlaylistRvBinding;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.utils.callback.MusicListRecommendClickCallback;
import com.example.cloudmusic.utils.callback.PlayListClickCallback;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private List<PlayList> playLists;
    private PlayListClickCallback clickCallback;
    private ViewGroup parent;

    public PlayListAdapter(List<PlayList> playLists) {
        this.playLists = playLists;
    }

    public void setClickCallback(PlayListClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        ItemPlaylistRvBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_playlist_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList playList= playLists.get(position);
        holder.binding.setSvm(playList);
        Glide.with(parent.getContext()).load(playList.getCoverImgUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_cd_default).transform(new RoundedCorners(20),new CenterCrop()).into(holder.binding.imageView2);
    }

    @Override
    public int getItemCount() {
        return playLists == null ? 0 : playLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPlaylistRvBinding binding;

        public ViewHolder(@NonNull ItemPlaylistRvBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public class ClickClass {
        public ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void itemOnClick(View view) {
            int position = holder.getAdapterPosition();
            if (position == -1) return;//视图刷新时点击，position为-1
            PlayList playList=playLists.get(position);
            if (clickCallback != null)
                clickCallback.onClick(playList);
        }
    }
}
