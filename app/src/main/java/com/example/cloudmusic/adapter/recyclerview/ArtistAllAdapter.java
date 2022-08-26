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
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemArtstAllRvBinding;
import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.utils.callback.ArtistItemClickCallback;

import java.util.List;

public class ArtistAllAdapter extends RecyclerView.Adapter<ArtistAllAdapter.ViewHolder> {
    private List<Artist> artistList;
    private ArtistItemClickCallback clickCallback;
    private ViewGroup parent;

    public void setClickCallback(ArtistItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public ArtistAllAdapter(List<Artist> artistList) {
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        ItemArtstAllRvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_artst_all_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        Glide.with(parent.getContext()).load(artist.getPicUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).transform(new CenterCrop()).placeholder(R.drawable.ic_cd_default).into(holder.binding.artistPic);
        holder.binding.setSvm(artist);
    }

    @Override
    public int getItemCount() {
        return artistList == null ? 0 : artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemArtstAllRvBinding binding;

        public ViewHolder(@NonNull ItemArtstAllRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ClickClass {

        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void itemCLick(View view) {
            if (clickCallback == null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Artist artist = artistList.get(position);
            clickCallback.onCLick(artist);
        }
    }
}
