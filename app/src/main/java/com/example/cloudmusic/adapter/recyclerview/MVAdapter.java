package com.example.cloudmusic.adapter.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.ItemMvRvBinding;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.utils.callback.MVItemClickCallback;

import java.util.List;

public class MVAdapter extends RecyclerView.Adapter<MVAdapter.ViewHolder> {

    private List<MV> mvList;
    private MVItemClickCallback itemClickCallback;
    private ViewGroup parent;

    public void setItemClickCallback(MVItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public MVAdapter(List<MV> mvList) {
        this.mvList = mvList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        ItemMvRvBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_mv_rv,
                parent,
                false);
        ViewHolder holder=new ViewHolder(binding);
        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MV mv=mvList.get(position);
        holder.binding.setSvm(mv);
        Glide.with(parent.getContext())
                .load(mv.getPicUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new CenterCrop(),new RoundedCorners(10))
                .into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return mvList==null?0:mvList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMvRvBinding binding;
        public ViewHolder(@NonNull ItemMvRvBinding binding) {
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
            if (itemClickCallback == null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            MV mv=mvList.get(position);
            itemClickCallback.onClick(mv);
        }
    }

}
