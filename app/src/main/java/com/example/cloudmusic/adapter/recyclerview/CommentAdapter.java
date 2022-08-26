package com.example.cloudmusic.adapter.recyclerview;

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
import com.example.cloudmusic.databinding.ItemCommentRvBinding;
import com.example.cloudmusic.entity.Comment;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.utils.callback.CommentClickCallback;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> comments;
    private CommentClickCallback clickCallback;
    private ViewGroup parent;

    public void setClickCallback(CommentClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        ItemCommentRvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_comment_rv,
                parent,
                false);
        ViewHolder holder = new ViewHolder(binding);
        holder.binding.setClick(new ClickClass(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.setSvm(comment);
        Glide.with(parent.getContext()).load(comment.getAvatarUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.binding.picHeard);
        if (comment.isLiked())
            holder.binding.likeBtn.setBackgroundResource(R.drawable.ic_like3);
        else holder.binding.likeBtn.setBackgroundResource(R.drawable.ic_lick2);
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCommentRvBinding binding;

        public ViewHolder(@NonNull ItemCommentRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ClickClass {
        private ViewHolder holder;

        public ClickClass(ViewHolder holder) {
            this.holder = holder;
        }

        public void like(View view) {
            if (clickCallback == null) return;
            int position = holder.getAdapterPosition();
            if (position == -1) return;
            Comment comment = comments.get(position);
            comment.setLiked(!comment.isLiked());
            if(comment.isLiked()){
                comment.setLikedCount(comment.getLikedCount()+1);
            }else {
                comment.setLikedCount(Math.max((comment.getLikedCount() - 1), 0));
            }
            comments.set(position,comment);
            notifyItemChanged(position);
            clickCallback.onClick(comment,(comment.isLiked()));
        }
    }
}
