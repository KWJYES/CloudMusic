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

    void getRecommendMusicList(MutableLiveData<List<MusicList>> recommendMusicListResult, MutableLiveData<String> recommendMusicListRequestState);

    void getDefaultSearchWord(MutableLiveData<String> defaultSearchWord, MutableLiveData<String> defaultSearchWordState);

    void getHotList(MutableLiveData<List<SearchWord>> hotList, MutableLiveData<String> hotListState);

    void searchOneSongs(String keywords, int limit, MutableLiveData<String> oneSongListRequestState, MutableLiveData<List<Song>> oneSongList);

    void loadMoreOneSong(String keywords, int limit, int offset, MutableLiveData<String> loadMoreRequestState, MutableLiveData<List<Song>> loadMoreList);

    //void getSongUrl(Song song,MutableLiveData<Song> songLD);

    void getSongUrl(Song song, GetSongUrlCallback callback);

    void checkPhone(String phone, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState);

    void checkNickname(String nickname, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState, MutableLiveData<String> candidateNicknames);

    void captchaSent(String phone, MutableLiveData<String> sentRequestState);

    void checkCaptcha(String phone, String captcha, MutableLiveData<String> checkRequestState, MutableLiveData<Boolean> correct);

    void signUp(String phone, String captcha, String nickname, String password, MutableLiveData<String> signupRequestState);

    void getLoginState(MutableLiveData<Boolean> isLogin,MutableLiveData<String> isLoginRequestState);

    void loginRefresh(MutableLiveData<Boolean> loginRefresh);

    void login(String phone, String password, MutableLiveData<String> loginState);

    void login(String phone,String password, String captcha, MutableLiveData<String> loginState);
}
