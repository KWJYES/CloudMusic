package com.example.cloudmusic.utils.callback;

import com.example.cloudmusic.entity.Comment;

public interface CommentClickCallback {
    void onClick(Comment comment,boolean isLike);
}
