package com.example.cloudmusic.adapter.banner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.entity.BannerData;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class RecommendBannerAdapter extends BannerAdapter<BannerData, RecommendBannerAdapter.BannerViewHolder> {

    private ViewGroup parent;

    public RecommendBannerAdapter(List<BannerData> datas) {
        super(datas);
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerData data, int position, int size) {
        Glide.with(parent.getContext()).load(data.getUrl()).centerCrop().into(holder.imageView);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}
