package com.example.cloudmusic.adapter.banner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.cloudmusic.R;

import com.example.cloudmusic.callback.BannerClickCallback;
import com.example.cloudmusic.databinding.RecommendBannerLayoutBinding;
import com.example.cloudmusic.entity.Banner;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class RecommendBannerAdapter extends BannerAdapter<Banner, RecommendBannerAdapter.BannerViewHolder> {

    private ViewGroup parent;
    private BannerClickCallback clickCallback;
    private List<Banner> dataList;


    public RecommendBannerAdapter(List<Banner> datas) {
        super(datas);
        dataList = datas;
    }

    public void setClickCallback(BannerClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecommendBannerLayoutBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.recommend_banner_layout,
                parent,
                false);
        BannerViewHolder holder = new BannerViewHolder(itemBinding);
        itemBinding.setClick(new BannerClickClass(holder));
        return holder;
    }

    @Override
    public void onBindView(BannerViewHolder holder, Banner data, int position, int size) {
        if (data.getPic().equals("isLoading"))
            Glide.with(parent.getContext()).load(R.drawable.bg_loading).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(holder.itemBinding.bannerImageView);
        else if (data.getPic().equals("isFail"))
            Glide.with(parent.getContext()).load(R.drawable.bg_loading_fail).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(holder.itemBinding.bannerImageView);
        else
            Glide.with(parent.getContext()).load(data.getPic()).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.itemBinding.bannerImageView);
        holder.itemBinding.bannerTittle.setText(data.getTypeTitle());
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        RecommendBannerLayoutBinding itemBinding;

        public BannerViewHolder(@NonNull RecommendBannerLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

    public class BannerClickClass {
        BannerViewHolder holder;

        public BannerClickClass(BannerViewHolder holder) {
            this.holder = holder;
        }

        public void onBannerClick(View view) {
            int position = holder.getAdapterPosition();
            Log.d("TAG", "Banner Position===" + position);
            if (position == -1 || position > dataList.size() - 1) return;//视图刷新时点击，position为-1
            Banner data = dataList.get(position);
            clickCallback.onClick(data);
        }
    }
}
