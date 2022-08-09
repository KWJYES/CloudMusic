package com.example.cloudmusic.response.network;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.utils.callback.GetSongUrlCallback;

import java.util.List;

import retrofit2.http.Query;

public interface INetworkRequest {
    void getBannerData(MutableLiveData<List<Banner>> bannerRequestResult, MutableLiveData<String> bannerRequestState);

    void getLoginState(MutableLiveData<Boolean> loginState);

    void getRecommendMusicList(MutableLiveData<List<MusicList>> recommendMusicListResult, MutableLiveData<String> recommendMusicListRequestState);

    void getDefaultSearchWord(MutableLiveData<String> defaultSearchWord, MutableLiveData<String> defaultSearchWordState);

    void getHotList(MutableLiveData<List<SearchWord>> hotList, MutableLiveData<String> hotListState);

    void searchOneSongs(String keywords, int limit, MutableLiveData<String> oneSongListRequestState, MutableLiveData<List<Song>> oneSongList);

    void loadMoreOneSong(String keywords, int limit, int offset, MutableLiveData<String> loadMoreRequestState,MutableLiveData<List<Song>> loadMoreList);

    //void getSongUrl(Song song,MutableLiveData<Song> songLD);

    void getSongUrl(Song song, GetSongUrlCallback callback);
}
