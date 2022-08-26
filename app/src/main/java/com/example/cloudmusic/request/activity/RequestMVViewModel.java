package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Comment;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMVViewModel extends ViewModel {
    public MutableLiveData<String> mvUrl = new MutableLiveData<>();
    public MutableLiveData<String> mvUrlState = new MutableLiveData<>();
    public MutableLiveData<String> commentListState = new MutableLiveData<>();
    public MutableLiveData<List<Comment>> commentList = new MutableLiveData<>();
    public MutableLiveData<String> likeCommentRequestState = new MutableLiveData<>();
    public MutableLiveData<String> sendCommentRequestState = new MutableLiveData<>();
    public MutableLiveData<Comment> sendComment=new MutableLiveData<>();

    public void getMVUrl(String mvId) {
        HttpRequestManager.getInstance().getMvUrl(mvId, mvUrl, mvUrlState);
    }

    public void getMVComment(String mvId, int limit, int offset) {
        HttpRequestManager.getInstance().getMvComment(mvId, limit, offset, commentList, commentListState);
    }
    public void pausePlaySong() {
        MediaManager.getInstance().pause(null);
    }

    public void likeComment(Comment comment, boolean isLike, String mvId) {
        HttpRequestManager.getInstance().likeComment(mvId, isLike, comment.getCommentId(), likeCommentRequestState);
    }

    public void sendComment(String content, String mvId) {
        HttpRequestManager.getInstance().sendComment(content, mvId, sendCommentRequestState,sendComment);
    }
}
