package com.example.cloudmusic.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.callback.MusicListRecommendClickCallback;
import com.example.cloudmusic.databinding.ItemMusiclistRecommendBinding;
import com.example.cloudmusic.entity.MusicList;

import java.util.List;

public class MusicListRecommendAdapter extends RecyclerView.Adapter<MusicListRecommendAdapter.ViewHolder> {
    private List<MusicList> musicLists;
    private MusicListRecommendClickCallback clickCallback;
    private ViewGroup parent;

    public MusicListRecommendAdapter(List<MusicList> musicLists) {
        this.musicLists = musicLists;
    }

    public void setClickCallback(MusicListRecommendClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        ItemMusiclistRecommendBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_musiclist_recommend,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicList musicList= musicLists.get(position);
        holder.binding.setSvm(musicList);
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(15);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options= RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(parent.getContext()).load(musicList.getPicUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).apply(options).into(holder.binding.imageView2);
    }

    @Override
    public int getItemCount() {
        return musicLists == null ? 0 : musicLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMusiclistRecommendBinding binding;

        public ViewHolder(@NonNull ItemMusiclistRecommendBinding binding) {
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
            MusicList musicList = musicLists.get(position);
            if (clickCallback != null)
                clickCallback.onClick(musicList);
        }
    }
}
