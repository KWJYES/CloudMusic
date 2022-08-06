package com.example.cloudmusic.request.fragment.search.searched;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestOneSongViewModel extends ViewModel {
    public MutableLiveData<List<Song>> oneSongList = new MutableLiveData<>();
    public MutableLiveData<List<Song>> loadMoreList = new MutableLiveData<>();
    public MutableLiveData<String> oneSongListRequestState = new MutableLiveData<>();
    public MutableLiveData<String> loadMoreRequestState = new MutableLiveData<>();
    public MutableLiveData<Song> songLD=new MutableLiveData<>();

    /**
     * 搜索单曲
     */
    public void requestSearchOneSong(String keywords) {
        HttpRequestManager.getInstance().searchOneSongs(keywords,20,oneSongListRequestState,oneSongList);
    }

    /**
     * 加载更多
     * @param offset 偏移量
     */
    public void loadMore(String keywords,int offset){
        HttpRequestManager.getInstance().loadMoreOneSong(keywords,20,offset,loadMoreRequestState,loadMoreList);
    }

    public void playSong(Song song){
        MediaManager.getInstance().play(null,null,song);
    }

    public void getSongUrl(Song song){
        HttpRequestManager.getInstance().getSongUrl(song,songLD);
    }
}
