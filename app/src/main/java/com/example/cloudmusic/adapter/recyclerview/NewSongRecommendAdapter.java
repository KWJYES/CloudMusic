package com.example.cloudmusic.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemNewsongRecommendRvBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.OneSongMoreOperateClickCallback;

import java.util.ArrayList;
import java.util.List;

public class NewSongRecommendAdapter extends RecyclerView.Adapter<NewSongRecommendAdapter.ViewHolder> {

    private List<Song> songList;
    private ViewGroup parent;
    private Toast toast;

    private OneSongMoreOperateClickCallback clickCallback;

    public void setClickCallback(OneSongMoreOperateClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public NewSongRecommendAdapter(List<Song> songList) {
        this.songList = new ArrayList<>(songList);;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        toast = Toast.makeText(parent.getContext(), "正在准备播放", Toast.LENGTH_SHORT);
        this.parent=parent;
        ItemNewsongRecommendRvBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_newsong_recommend_rv,
                parent,
                false);
        ViewHolder holder= new ViewHolder(binding);
        binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song =songList.get(position);
        holder.binding.setSvm(song);
        RoundedCorners roundedCorners= new RoundedCorners(15);//设置图片圆角角度
        RequestOptions options= RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(parent.getContext()).load(song.getPicUrl()).apply(options).placeholder(R.drawable.ic_cd_default).into(holder.binding.imageView6);
        if(song.getSongId().startsWith("000"))
            Glide.with(parent.getContext()).load(R.drawable.ic_cd_default).apply(options).placeholder(R.drawable.ic_cd_default).into(holder.binding.imageView6);
    }

    @Override
    public int getItemCount() {
        return songList==null?0: songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNewsongRecommendRvBinding binding;
        public ViewHolder(@NonNull ItemNewsongRecommendRvBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public class ClickClass{
        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void onItemClick(View view){
            if(clickCallback==null)return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Song song= songList.get(position);
            if(CloudMusic.isGettingSongUrl||CloudMusic.isReset){
                toast.show();
                return;
            }else {
                toast.cancel();
            }
            clickCallback.onClick(song);
        }
    }
}
