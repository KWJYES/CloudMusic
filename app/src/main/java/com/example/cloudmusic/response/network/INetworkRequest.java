package com.example.cloudmusic.response.network;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.Comment;
import com.example.cloudmusic.entity.Lyrics;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongLrc;
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

    void getSongUrl(Song song, GetSongUrlCallback callback);

    void checkPhone(String phone, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState);

    void checkNickname(String nickname, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState, MutableLiveData<String> candidateNicknames);

    void captchaSent(String phone, MutableLiveData<String> sentRequestState);

    void checkCaptcha(String phone, String captcha, MutableLiveData<String> checkRequestState, MutableLiveData<Boolean> correct);

    void signUp(String phone, String captcha, String nickname, String password, MutableLiveData<String> signupRequestState);

    void getLoginState(MutableLiveData<Boolean> isLogin, MutableLiveData<String> isLoginRequestState);

    void loginRefresh(MutableLiveData<Boolean> loginRefresh);

    void login(String phone, String password, MutableLiveData<String> loginState);

    void login(String phone, String password, String captcha, MutableLiveData<String> loginState);

    void newSongRecommend(MutableLiveData<List<Song>> newSongList, MutableLiveData<String> newSongListRequestState);

    void lyric(String songId, MutableLiveData<SongLrc> songLrc);

    void personalizedMv(MutableLiveData<List<MV>> mvList, MutableLiveData<String> mvRequestState);

    void getMvUrl(String mvId, MutableLiveData<String> mvUrl, MutableLiveData<String> mvUrlState);

    void getAllMv(String area, String type, int offset, MutableLiveData<List<MV>> mvList, MutableLiveData<String> mvRequestState);

    void getPlayListDec(String id, MutableLiveData<String> dec);

    void getPlayListSongs(String id, MutableLiveData<String> songListRequestState, MutableLiveData<List<Song>> songList);

    void getMvComment(String mvId, int limit, int offset, MutableLiveData<List<Comment>> commentList, MutableLiveData<String> commentListState);

    void getPlayList(String cat, int offset, MutableLiveData<List<PlayList>> playLists, MutableLiveData<String> playListRequestState);

    void likeSong(boolean like, String songId, MutableLiveData<String> likeState);

    void getLikeSongIdList(String uid, MutableLiveData<List<String>> likeSongIdList, MutableLiveData<String> likeListRequestState);

    void getSongDetail(List<String> idList, MutableLiveData<List<Song>> songList, MutableLiveData<String> songDetailRequestState);

    void getUserLevel();

    void signIn(MutableLiveData<String> signInState);

    void artistList(String type, String area, int offset, MutableLiveData<String> requestState, MutableLiveData<List<Artist>> artistList);

    void artists(String arId, MutableLiveData<String> dec, MutableLiveData<List<Song>> songList, MutableLiveData<String> requestState);

    void getLikeArtist(MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState);

    void likeArtist(String arId, boolean isLike, MutableLiveData<String> requestState);

    void sendComment(String content, String id, MutableLiveData<String> requestState, MutableLiveData<Comment> sendComment);

    void likeComment(String id, boolean isLike, String cid, MutableLiveData<String> requestState);

    void searchArtist(String keyword, MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState);

    void loadMoreSearchArtist(String keyword, int offset, MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState);

    void searchMusicList(String keyword, MutableLiveData<List<PlayList>> playList, MutableLiveData<String> requestState);

    void loadMoreMusicList(String keyword, int offset, MutableLiveData<List<PlayList>> playList, MutableLiveData<String> requestState);

    void searchLyrics(String keyword, MutableLiveData<List<Lyrics>> lyricsList, MutableLiveData<String> requestState);

    void loadMoreLyrics(String keyword, int offset, MutableLiveData<List<Lyrics>> lyricsList, MutableLiveData<String> requestState);

    void loginOut(MutableLiveData<String> requestState);

}
