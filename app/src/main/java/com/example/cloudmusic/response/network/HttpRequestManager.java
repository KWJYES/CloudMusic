package com.example.cloudmusic.response.network;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.Comment;
import com.example.cloudmusic.entity.Lyrics;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongLrc;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.retrofit_api.IArtistService;
import com.example.cloudmusic.response.retrofit_api.ILikeService;
import com.example.cloudmusic.response.retrofit_api.ILoginService;
import com.example.cloudmusic.response.retrofit_api.IMvService;
import com.example.cloudmusic.response.retrofit_api.IPlayListService;
import com.example.cloudmusic.response.retrofit_api.IRecommendService;
import com.example.cloudmusic.response.retrofit_api.ISearchService;
import com.example.cloudmusic.response.retrofit_api.ISignUpService;
import com.example.cloudmusic.response.retrofit_api.IUserDataService;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.utils.callback.GetSongUrlCallback;

import com.example.cloudmusic.utils.cookies.ReadCookiesInterceptor;
import com.example.cloudmusic.utils.cookies.SaveCookiesInterceptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HttpRequestManager implements INetworkRequest {

    private static Retrofit retrofit;

    /**
     * 使用单例模式
     */
    private HttpRequestManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new SaveCookiesInterceptor())
                .addInterceptor(new ReadCookiesInterceptor())
                .build();
        retrofit = new Retrofit.Builder().client(okHttpClient).baseUrl(CloudMusic.baseUrl).build();
    }

    private static HttpRequestManager httpRequestManager;

    public static HttpRequestManager getInstance() {
        if (httpRequestManager == null) {
            synchronized (HttpRequestManager.class) {
                if (null == httpRequestManager) {
                    httpRequestManager = new HttpRequestManager();
                }
            }
        }
        return httpRequestManager;
    }

    /**
     * 获取banner
     *
     * @param bannerRequestResult 处理结果
     * @param bannerRequestState  请求状态
     */
    @Override
    public void getBannerData(MutableLiveData<List<Banner>> bannerRequestResult, MutableLiveData<String> bannerRequestState) {
        IRecommendService recommendService = retrofit.create(IRecommendService.class);
        recommendService.getBanners(1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        List<Banner> bannerList = new ArrayList<>();
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray banners = jsonObject.getJSONArray("banners");
                        for (int i = 0; i < banners.length(); i++) {
                            Banner bannerData = new Banner();
                            Song songData = new Song();
                            JSONObject banner = banners.getJSONObject(i);
                            //用getString()方法取出数据
                            String pic = banner.getString("pic");
                            String url = banner.getString("url");
                            String typeTitle = banner.getString("typeTitle");
                            try {//捕捉song为null异常
                                JSONObject song = banner.getJSONObject("song");
                                String name = song.getString("name");
                                songData.setName(name);
                                String id = song.getString("id");
                                songData.setSongId(id);
                                JSONArray artists = song.getJSONArray("ar");
                                for (int j = 0; j < artists.length(); j++) {
                                    JSONObject artist = artists.getJSONObject(j);
                                    String artistId = artist.getString("id");
                                    songData.setArtistId(artistId);
                                    String artistName = artist.getString("name");
                                    songData.setArtist(artistName);
                                }
                                JSONObject album = song.getJSONObject("al");
                                String albumId = album.getString("id");
                                songData.setAlbumId(albumId);
                                String albumName = album.getString("name");
                                songData.setAlbum(albumName);
                                String picUrl = album.getString("picUrl");
                                songData.setPicUrl(picUrl);
                            } catch (JSONException e) {
                                //Log.d("TAG", "该banner song为null");
                                e.printStackTrace();
                            }
                            bannerData.setPic(pic);
                            bannerData.setUrl(url);
                            bannerData.setTypeTitle(typeTitle);
                            bannerData.setSong(songData);
                            if (!bannerData.getTypeTitle().equals("新碟首发") && !bannerData.getTypeTitle().startsWith("MV") && !bannerData.getTypeTitle().endsWith("MV") && !bannerData.getTypeTitle().equals("歌单推荐"))
                                bannerList.add(bannerData);
                        }
                        bannerRequestResult.setValue(bannerList);
                        bannerRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (IOException e) {
                        Log.d("TAG", "获取jsonData异常");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("TAG", "banner jsonData解析异常");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "banner数据请求失败");
                bannerRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getLoginState(MutableLiveData<Boolean> isLogin, MutableLiveData<String> isLoginRequestState) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.loginState().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("TAG", "loginState--->" + jsonObject.toString());
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONObject account = data.getJSONObject("account");
                        if (account != null && !account.toString().equals("null")) {
                            CloudMusic.userId = account.getString("id");
                            Log.d("TAG", "CloudMusic.userId=" + CloudMusic.userId);
                            JSONObject profile = data.getJSONObject("profile");
                            CloudMusic.nickname = profile.getString("nickname");
                            CloudMusic.avatarUrl = profile.getString("avatarUrl");
                            isLogin.setValue(true);
                        } else {
                            isLogin.setValue(false);
                        }
                    } catch (JSONException | IOException e) {
                        isLogin.setValue(false);
                        e.printStackTrace();
                    }
                } else {
                    Log.d("HttpRequestManager", "getLoginState : response.code() != 200");
                    isLoginRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("HttpRequestManager", "getLoginState : onFailure");
                isLoginRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loginRefresh(MutableLiveData<Boolean> loginRefresh) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.loginRefresh().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            Log.d("TAG", "loginRefresh body--->" + response.body().string());
                        } else {
                            Log.d("TAG", "loginRefresh--->body is null");
                            Log.d("TAG", "loginRefresh headers--->" + response.headers().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loginRefresh.setValue(true);
                } else
                    loginRefresh.setValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginRefresh.setValue(false);
            }
        });
    }

    @Override
    public void login(String phone, String password, MutableLiveData<String> loginState) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.login(phone, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        CloudMusic.isLogin=true;
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.d("TAG", "Login json body--->" + jsonObject.toString());
                            JSONObject account = jsonObject.getJSONObject("account");
                            CloudMusic.userId = account.getString("id");
                            JSONObject profile = jsonObject.getJSONObject("profile");
                            CloudMusic.nickname = profile.getString("nickname");
                            CloudMusic.avatarUrl = profile.getString("avatarUrl");
                            Log.d("TAG", "CloudMusic.userId=" + CloudMusic.userId);
                        } else {
                            Log.d("TAG", "Login body is null,Headers--->" + response.headers().toString());
                        }
                        loginState.setValue(CloudMusic.SUCCEED);
                    } else {
                        loginState.setValue(CloudMusic.FAILURE);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getRecommendMusicList(MutableLiveData<List<MusicList>> recommendMusicListResult, MutableLiveData<String> recommendMusicListRequestState) {
        IRecommendService recommendService = retrofit.create(IRecommendService.class);
        recommendService.getRecommendMusic(15).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray musicLists = jsonObject.getJSONArray("result");
                        Gson gson = new Gson();
                        List<MusicList> musicListList = gson.fromJson(musicLists.toString(), new TypeToken<List<MusicList>>() {
                        }.getType());
                        recommendMusicListResult.setValue(musicListList);
                        recommendMusicListRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException e) {
                        Log.d("TAG", "musicLists JSONException 错误...");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.d("TAG", "musicLists response.body().string()错误...");
                        e.printStackTrace();
                    }
                } else {
                    recommendMusicListRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                recommendMusicListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getDefaultSearchWord(MutableLiveData<String> defaultSearchWord, MutableLiveData<String> defaultSearchWordState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.getDefaultSearchWord().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONObject data = jsonObject.getJSONObject("data");
                        String keyWord = data.getString("realkeyword");
                        defaultSearchWord.setValue(keyWord);
                    } catch (IOException e) {
                        Log.d("TAG", " getDefaultSearchWord response.body().string()异常");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("TAG", " getDefaultSearchWord new JSONObject(jsonData)异常");
                        e.printStackTrace();
                    }
                    defaultSearchWordState.setValue(CloudMusic.SUCCEED);
                } else {
                    defaultSearchWordState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                defaultSearchWordState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getHotList(MutableLiveData<List<SearchWord>> hotList, MutableLiveData<String> hotListState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.getHotList().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray data = jsonObject.getJSONArray("data");
                        Gson gson = new Gson();
                        List<SearchWord> list = gson.fromJson(data.toString(), new TypeToken<List<SearchWord>>() {
                        }.getType());
                        hotList.setValue(list);
                    } catch (IOException e) {
                        Log.d("TAG", " getHotList response.body().string()异常");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("TAG", " getHotList new JSONObject(jsonData)异常");
                        e.printStackTrace();
                    }
                    hotListState.setValue(CloudMusic.SUCCEED);
                } else {
                    hotListState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hotListState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void searchOneSongs(String keywords, int limit, MutableLiveData<String> oneSongListRequestState, MutableLiveData<List<Song>> oneSongList) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keywords, limit, 0, 1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray songs = result.getJSONArray("songs");
                        List<Song> resultSongList = new ArrayList<>();
                        for (int i = 0; i < songs.length(); i++) {
                            Song resultSong = new Song();
                            JSONObject song = songs.getJSONObject(i);
                            resultSong.setName(song.getString("name"));
                            resultSong.setSongId(song.getString("id"));
                            JSONArray ar = song.getJSONArray("ar");
                            StringBuilder arName = new StringBuilder();
                            for (int j = 0; j < ar.length(); j++) {
                                arName.append(ar.getJSONObject(j).getString("name"));
                                if (j < ar.length() - 1)
                                    arName.append("/");
                            }
                            resultSong.setArtist(arName.toString());
                            resultSong.setArtistId(ar.getJSONObject(0).getString("id"));
                            JSONObject al = song.getJSONObject("al");
                            resultSong.setPicUrl(al.getString("picUrl"));
                            resultSong.setAlbum(al.getString("name"));
                            resultSong.setAlbumId(al.getString("id"));
                            resultSongList.add(resultSong);
                        }
                        oneSongList.setValue(resultSongList);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    oneSongListRequestState.setValue(CloudMusic.SUCCEED);
                } else {
                    oneSongListRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                oneSongListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loadMoreOneSong(String keywords, int limit, int offset, MutableLiveData<String> loadMoreRequestState, MutableLiveData<List<Song>> loadMoreList) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keywords, limit, offset, 1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = null;
                        if (response.body() != null) {
                            jsonObject = new JSONObject(response.body().string());
                            JSONObject result = jsonObject.getJSONObject("result");
                            JSONArray songs = result.getJSONArray("songs");
                            List<Song> resultSongList = new ArrayList<>();
                            for (int i = 0; i < songs.length(); i++) {
                                Song resultSong = new Song();
                                JSONObject song = songs.getJSONObject(i);
                                resultSong.setName(song.getString("name"));
                                resultSong.setSongId(song.getString("id"));
                                JSONArray ar = song.getJSONArray("ar");
                                StringBuilder arName = new StringBuilder();
                                for (int j = 0; j < ar.length(); j++) {
                                    arName.append(ar.getJSONObject(j).getString("name"));
                                    if (j < ar.length() - 1)
                                        arName.append("/");
                                }
                                resultSong.setArtist(arName.toString());
                                resultSong.setArtistId(ar.getJSONObject(0).getString("id"));
                                JSONObject al = song.getJSONObject("al");
                                resultSong.setPicUrl(al.getString("picUrl"));
                                resultSong.setAlbum(al.getString("name"));
                                resultSong.setAlbumId(al.getString("id"));
                                resultSongList.add(resultSong);
                            }
                            loadMoreList.setValue(resultSongList);
                        } else {
                            loadMoreRequestState.setValue(CloudMusic.FAILURE);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    loadMoreRequestState.setValue(CloudMusic.SUCCEED);
                } else {
                    loadMoreRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadMoreRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getSongUrl(Song song, GetSongUrlCallback callback) {
        CloudMusic.isGettingSongUrl = true;
        if (song.getSongId().startsWith("000")) {
            callback.onResult(song);
            CloudMusic.isGettingSongUrl = false;
            Log.d("TAG", "getSongUrl:本地音乐");
            return;//本地
        }
        //一定时间内不重复获取
        List<Song> songList = LitePalManager.getInstance().getSongList("historyList");
        for (Song song1 : songList) {
            if (song1.getSongId().equals(song.getSongId())) {
                // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
                // 计算结束时间 - 开始时间的时间差
                String starTime = song1.getUrlStartTime();
                if (starTime == null) break;
                Calendar calendar = Calendar.getInstance();
                String endTime = calendar.get(Calendar.YEAR) + "-" +
                        (calendar.get(Calendar.MONTH) + 1) + "-" +
                        calendar.get(Calendar.DAY_OF_MONTH) + " " +
                        calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                        calendar.get(Calendar.MINUTE) + ":" +
                        calendar.get(Calendar.SECOND);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date end = formatter.parse(endTime);//结束时间
                    Date start = formatter.parse(starTime);//开始时间
                    // 这样得到的差值是微秒级别
                    long diff = end.getTime() - start.getTime();
                    long days = diff / (1000 * 60 * 60 * 24);
                    long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
                    Log.d("TAG", "《" + song.getName() + "》url已存在时间：" + days + "天" + hours + "时" + minutes + "分");
                    if (days == 0 && hours == 0 && minutes <= 19) {
                        Log.d("TAG", "《" + song.getName() + "》url复用");
                        callback.onResult(song1);
                        CloudMusic.isGettingSongUrl = false;
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.getSongUrl(song.getSongId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray data = jsonObject.getJSONArray("data");
                            String url = data.getJSONObject(0).getString("url");
                            Calendar calendar = Calendar.getInstance();
                            String urlStartTime = calendar.get(Calendar.YEAR) + "-" +
                                    (calendar.get(Calendar.MONTH) + 1) + "-" +
                                    calendar.get(Calendar.DAY_OF_MONTH) + " " +
                                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                                    calendar.get(Calendar.MINUTE) + ":" +
                                    calendar.get(Calendar.SECOND);
                            song.setUrlStartTime(urlStartTime);
                            song.setUrl(url);
                            callback.onResult(song);
                            CloudMusic.isGettingSongUrl = false;
                        } else {
                            callback.onResult(song);
                            CloudMusic.isGettingSongUrl = false;
                        }
                        CloudMusic.requestUrlTime = 0;
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CloudMusic.isGettingSongUrl = false;
                    callback.onResult(null);
                    MyEvent myEvent = new MyEvent();
                    myEvent.setMsg(7);
                    myEvent.setSong(song);
                    EventBus.getDefault().post(myEvent);//网络繁忙
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CloudMusic.isGettingSongUrl = false;
                callback.onResult(null);
                MyEvent myEvent = new MyEvent();
                myEvent.setMsg(7);
                myEvent.setSong(song);
                EventBus.getDefault().post(myEvent);//网络繁忙
            }
        });
    }

    @Override
    public void checkPhone(String phone, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState) {
        ISignUpService signUpService = retrofit.create(ISignUpService.class);
        signUpService.checkPhone(phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int exist = jsonObject.getInt("exist");
                        if (exist == 1) {
                            enable.setValue(false);
                        } else {
                            enable.setValue(true);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void checkNickname(String nickname, MutableLiveData<Boolean> enable, MutableLiveData<String> requestState, MutableLiveData<String> candidateNickname) {
        ISignUpService signUpService = retrofit.create(ISignUpService.class);
        signUpService.checkNickname(nickname).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean duplicated = jsonObject.getBoolean("duplicated");
                        if (duplicated) {
                            enable.setValue(false);
                            JSONArray candidateNicknames = jsonObject.getJSONArray("candidateNicknames");
                            String name = candidateNicknames.get(0).toString();
                            candidateNickname.setValue(name);
                        } else {
                            enable.setValue(true);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void captchaSent(String phone, MutableLiveData<String> sentRequestState) {
        ISignUpService signUpService = retrofit.create(ISignUpService.class);
        signUpService.captchaSent(phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("TAG", "captchaSent--->" + jsonObject.toString());
                        int code = jsonObject.getInt("code");
                        if (code == 400) {
                            sentRequestState.setValue(CloudMusic.FAILURE);
                        } else {
                            sentRequestState.setValue(CloudMusic.SUCCEED);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sentRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sentRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void checkCaptcha(String phone, String captcha, MutableLiveData<String> checkRequestState, MutableLiveData<Boolean> correct) {
        ISignUpService signUpService = retrofit.create(ISignUpService.class);
        signUpService.checkCaptcha(phone, captcha).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
//                        Log.d("TAG","phone: "+phone+"captcha: "+captcha);
//                        Log.d("TAG","checkCaptcha"+jsonObject.toString());
                        boolean data = jsonObject.getBoolean("data");
                        if (data) {
                            correct.setValue(true);
                        } else {
                            correct.setValue(false);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    checkRequestState.setValue(CloudMusic.SUCCEED);
                } else {
                    correct.setValue(false);
                    checkRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                correct.setValue(false);
                checkRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void signUp(String phone, String captcha, String nickname, String password, MutableLiveData<String> signupRequestState) {
        ISignUpService signUpService = retrofit.create(ISignUpService.class);
        signUpService.signUp(captcha, phone, password, nickname).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    signupRequestState.setValue(CloudMusic.SUCCEED);
                } else {
                    signupRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                signupRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void login(String phone, String password, String captcha, MutableLiveData<String> loginState) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.login(phone, "password", captcha).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        Log.d("TAG", "Login json body--->" + response.body().string());
                    } else {
                        Log.d("TAG", "Login body is null,Headers--->" + response.headers().toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response.code() == 200) {
                    loginState.setValue(CloudMusic.SUCCEED);
                } else {
                    loginState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void newSongRecommend(MutableLiveData<List<Song>> newSongList, MutableLiveData<String> newSongListRequestState) {
        IRecommendService recommendService = retrofit.create(IRecommendService.class);
        recommendService.getNewSong(12).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray result = jsonObject.getJSONArray("result");
                            List<Song> songList = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject1 = result.getJSONObject(i);
                                JSONObject song = jsonObject1.getJSONObject("song");
                                Song song1 = new Song();
                                song1.setSongId(song.getString("id"));
                                song1.setName(song.getString("name"));
                                song1.setPicUrl(jsonObject1.getString("picUrl"));
                                JSONArray artists = song.getJSONArray("artists");
                                song1.setArtist(artists.getJSONObject(0).getString("name"));
                                song1.setArtistId(artists.getJSONObject(0).getString("id"));
                                JSONArray alias = song.getJSONArray("alias");
                                if (alias.length() != 0) {
                                    String aliasString = alias.get(0).toString();
                                    song1.setAlias(aliasString);
                                } else {
                                    String disc = song.getString("disc");
                                    if (!disc.equals("")) {
                                        song1.setAlias(disc);
                                    } else {
                                        song1.setAlias("");
                                    }
                                }
                                songList.add(song1);
                            }
                            newSongList.setValue(songList);
                            newSongListRequestState.setValue(CloudMusic.SUCCEED);
                        } else {
                            newSongListRequestState.setValue(CloudMusic.FAILURE);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    newSongListRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                newSongListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void lyric(String songId, MutableLiveData<SongLrc> songLrc) {
        if (songId.startsWith("000")) return;
        List<SongLrc> songLrcList = LitePalManager.getInstance().searchSongLrc(songId);
        if (songLrcList.size() != 0) {
            songLrc.setValue(songLrcList.get(0));
            return;
        }
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.getSongLrc(songId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject lrc = jsonObject.getJSONObject("lrc");
                        String lyric = lrc.getString("lyric").replace("\"", "");
                        SongLrc songLrc1 = new SongLrc();
                        songLrc1.setSongId(songId);
                        songLrc1.setLrc(lyric);
                        songLrc.setValue(songLrc1);
                        songLrc1.save();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void personalizedMv(MutableLiveData<List<MV>> mvList, MutableLiveData<String> mvRequestState) {
        IRecommendService recommendService = retrofit.create(IRecommendService.class);
        recommendService.getRecommendMv().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray result = jsonObject.getJSONArray("result");
                        List<MV> ml = new ArrayList<>();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject m = result.getJSONObject(i);
                            MV mv = new MV();
                            mv.setMvId(m.getString("id"));
                            mv.setName(m.getString("name"));
                            mv.setPicUrl(m.getString("picUrl"));
                            mv.setDuration(m.getString("duration"));
                            mv.setPlayCount(m.getString("playCount"));
                            ml.add(mv);
                        }
                        mvList.setValue(ml);
                        mvRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        mvRequestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    mvRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mvRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getMvUrl(String mvId, MutableLiveData<String> mvUrl, MutableLiveData<String> mvUrlState) {
        IMvService mvService = retrofit.create(IMvService.class);
        mvService.mvUrl(mvId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("TAG", response.body().string());
                        JSONObject data = jsonObject.getJSONObject("data");
                        mvUrl.setValue(data.getString("url"));
                        mvUrlState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        mvUrlState.setValue(CloudMusic.FAILURE);
                        Log.d("TAG", "mv url解析异常");
                    }
                } else {
                    mvUrlState.setValue(CloudMusic.FAILURE);
                    Log.d("TAG", "mv url请求异常");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mvUrlState.setValue(CloudMusic.FAILURE);
                Log.d("TAG", "mv url网络异常");
            }
        });
    }

    @Override
    public void getAllMv(String area, String type, int offset, MutableLiveData<List<MV>> mvList, MutableLiveData<String> mvRequestState) {
        IMvService mvService = retrofit.create(IMvService.class);
        mvService.mvAll(area, type, offset).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<MV> mMvList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MV mMv = new MV();
                            JSONObject mv = jsonArray.getJSONObject(i);
                            mMv.setMvId(mv.getString("id"));
                            mMv.setName(mv.getString("name"));
                            mMv.setPicUrl(mv.getString("cover"));
                            mMv.setArtName(mv.getJSONArray("artists").getJSONObject(0).getString("name"));
                            mMv.setPlayCount(mv.getString("playCount"));
                            mMv.setDuration(mv.getString("duration"));
                            mMvList.add(mMv);
                        }
                        mvList.setValue(mMvList);
                        mvRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        mvRequestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    mvRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mvRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getPlayListDec(String id, MutableLiveData<String> dec) {
        IPlayListService playListService = retrofit.create(IPlayListService.class);
        playListService.playlistDetail(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject playlist = jsonObject.getJSONObject("playlist");
                        String d = playlist.getString("description");
                        dec.setValue(d);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        dec.setValue("暂无描述~");
                    }
                } else {
                    dec.setValue("暂无描述");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dec.setValue("暂无描述");
            }
        });
    }

    @Override
    public void getPlayListSongs(String id, MutableLiveData<String> songListRequestState, MutableLiveData<List<Song>> songList) {
        IPlayListService playListService = retrofit.create(IPlayListService.class);
        playListService.playlistTrackAll(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray songs = jsonObject.getJSONArray("songs");
                        List<Song> mSongList = new ArrayList<>();
                        for (int i = 0; i < songs.length(); i++) {
                            Song mSong = new Song();
                            JSONObject song = songs.getJSONObject(i);
                            mSong.setSongId(song.getString("id"));
                            mSong.setName(song.getString("name"));
                            JSONObject al = song.getJSONObject("al");
                            mSong.setPicUrl(al.getString("picUrl"));
                            mSong.setAlbum(al.getString("name"));
                            mSong.setAlbumId(al.getString("id"));
                            JSONObject ar = song.getJSONArray("ar").getJSONObject(0);
                            mSong.setArtist(ar.getString("name"));
                            mSong.setArtistId(ar.getString("id"));
                            mSongList.add(mSong);
                        }
                        songList.setValue(mSongList);
                        songListRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        songListRequestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    songListRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                songListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getMvComment(String mvId, int limit, int offset, MutableLiveData<List<Comment>> commentList, MutableLiveData<String> commentListState) {
        IMvService mvService = retrofit.create(IMvService.class);
        mvService.mvComment(mvId, limit, offset).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray comments;
                        try {
                            comments = jsonObject.getJSONArray("hotComments");
                            if (comments.length() == 0)
                                comments = jsonObject.getJSONArray("comments");
                            Log.d("TAG", "comments:" + comments.length() + "  " + comments.toString());
                        } catch (JSONException e) {
                            comments = jsonObject.getJSONArray("comments");
                            Log.d("TAG", "comments:" + comments.length() + "  " + comments.toString());
                        }
                        List<Comment> mCommendList = new ArrayList<>();
                        for (int i = 0; i < comments.length(); i++) {
                            Comment mComment = new Comment();
                            JSONObject comment = comments.getJSONObject(i);
                            JSONObject user = comment.getJSONObject("user");
                            mComment.setUserId(user.getString("userId"));
                            mComment.setAvatarUrl(user.getString("avatarUrl"));
                            mComment.setNickname(user.getString("nickname"));
                            mComment.setCommentId(comment.getString("commentId"));
                            mComment.setLiked(comment.getBoolean("liked"));
                            mComment.setContent(comment.getString("content"));
                            mComment.setLikedCount(comment.getInt("likedCount"));
                            mComment.setTimeStr(comment.getString("timeStr"));
                            mCommendList.add(mComment);
                            Log.d("TAG", "一条评论:" + mComment.getContent());
                        }
                        commentList.setValue(mCommendList);
                        commentListState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        commentListState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    commentListState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                commentListState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getPlayList(String cat, int offset, MutableLiveData<List<PlayList>> playLists, MutableLiveData<String> playListRequestState) {
        IPlayListService playListService = retrofit.create(IPlayListService.class);
        playListService.getPlayList(cat, 30, offset).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray playlists = jsonObject.getJSONArray("playlists");
                        List<PlayList> mPlayLists = new ArrayList<>();
                        for (int i = 0; i < playlists.length(); i++) {
                            PlayList mPlayList = new PlayList();
                            JSONObject pl = playlists.getJSONObject(i);
                            mPlayList.setId(pl.getString("id"));
                            mPlayList.setName(pl.getString("name"));
                            mPlayList.setCoverImgUrl(pl.getString("coverImgUrl"));
                            mPlayList.setDescription(pl.getString("description"));
                            mPlayLists.add(mPlayList);
                        }
                        playLists.setValue(mPlayLists);
                        playListRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    playListRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                playListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void likeSong(boolean like, String songId, MutableLiveData<String> likeState) {
        if (songId.startsWith("000")) {
            likeState.setValue(CloudMusic.FAILURE);
            return;
        }
        ILikeService likeSongService = retrofit.create(ILikeService.class);
        likeSongService.like(songId, like).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (like) {
                        CloudMusic.likeSongIdSet.add(songId);
                    } else {
                        CloudMusic.likeSongIdSet.remove(songId);
                    }
                    likeState.setValue(CloudMusic.SUCCEED);
                } else likeState.setValue(CloudMusic.FAILURE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                likeState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getLikeSongIdList(String uid, MutableLiveData<List<String>> likeSongIdList, MutableLiveData<String> likeListRequestState) {
        ILikeService likeSongService = retrofit.create(ILikeService.class);
        likeSongService.likeList(uid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("ids");
                        List<String> mIds = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.get(i).toString();
                            mIds.add(id);
                            Log.d("TAG", "喜欢音乐id:" + id);
                        }
                        Log.d("TAG", "喜欢音乐id共:" + mIds.size() + "个");
                        likeSongIdList.setValue(mIds);
                        likeListRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else likeListRequestState.setValue(CloudMusic.FAILURE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                likeListRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getSongDetail(List<String> idList, MutableLiveData<List<Song>> songList, MutableLiveData<String> songDetailRequestState) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            if (i != idList.size() - 1) {
                stringBuilder.append(idList.get(i)).append(",");
            } else {
                stringBuilder.append(idList.get(i));
            }
        }
        ILikeService likeSongService = retrofit.create(ILikeService.class);
        likeSongService.getSongDetail(stringBuilder.toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray songs = jsonObject.getJSONArray("songs");
                        List<Song> mSongList = new ArrayList<>();
                        for (int i = 0; i < songs.length(); i++) {
                            Song mSong = new Song();
                            JSONObject song = songs.getJSONObject(i);
                            mSong.setName(song.getString("name"));
                            mSong.setSongId(song.getString("id"));
                            JSONObject ar = song.getJSONArray("ar").getJSONObject(0);
                            mSong.setArtist(ar.getString("name"));
                            mSong.setArtistId(ar.getString("id"));
                            JSONObject al = song.getJSONObject("al");
                            mSong.setAlbum(al.getString("name"));
                            mSong.setAlbumId(al.getString("id"));
                            mSong.setPicUrl(al.getString("picUrl"));
                            mSongList.add(mSong);
                            Log.d("TAG", "喜欢的音乐:" + mSong.getName());
                        }
                        songList.setValue(mSongList);
                        Log.d("TAG", "喜欢的音乐共：" + mSongList.size() + "个");
                        songDetailRequestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        songDetailRequestState.setValue(CloudMusic.FAILURE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                songDetailRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getUserLevel() {
        IUserDataService userDataService = retrofit.create(IUserDataService.class);
        userDataService.level().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        CloudMusic.level = jsonObject.getJSONObject("data").getString("level");
                        Log.d("TAG", "用户等级:" + CloudMusic.level);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void signIn(MutableLiveData<String> signInState) {
        IUserDataService userDataService = retrofit.create(IUserDataService.class);
        userDataService.dailySignIn().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    signInState.setValue(CloudMusic.SUCCEED);
                } else {
                    signInState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                signInState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void artistList(String typeS, String areaS, int offset, MutableLiveData<String> requestState, MutableLiveData<List<Artist>> artistList) {
        int type = -1;
        int area = -1;
        switch (typeS) {
            case "男歌手":
                type = 1;
                break;
            case "女歌手":
                type = 2;
                break;
            case "乐队":
                type = 3;
                break;
        }
        switch (areaS) {
            case "华语":
                area = 7;
                break;
            case "欧美":
                area = 96;
                break;
            case "日本":
                area = 8;
                break;
            case "韩国":
                area = 16;
                break;
            case "其他":
                area = 0;
                break;
        }
        IArtistService artistService = retrofit.create(IArtistService.class);
        artistService.artistList(type, area, offset, 28).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray artists = jsonObject.getJSONArray("artists");
                        List<Artist> mArtistList = new ArrayList<>();
                        for (int i = 0; i < artists.length(); i++) {
                            Artist mArtist = new Artist();
                            JSONObject artist = artists.getJSONObject(i);
                            mArtist.setName(artist.getString("name"));
                            mArtist.setPicUrl(artist.getString("picUrl"));
                            mArtist.setArId(artist.getString("id"));
                            mArtistList.add(mArtist);
                        }
                        artistList.setValue(mArtistList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void artists(String arId, MutableLiveData<String> dec, MutableLiveData<List<Song>> songList, MutableLiveData<String> requestState) {
        IArtistService artistService = retrofit.create(IArtistService.class);
        artistService.artists(arId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject artist = jsonObject.getJSONObject("artist");
                        String briefDesc = artist.getString("briefDesc");
                        dec.setValue(briefDesc);
                        JSONArray hotSongs = jsonObject.getJSONArray("hotSongs");
                        List<Song> mSongList = new ArrayList<>();
                        for (int i = 0; i < hotSongs.length(); i++) {
                            Song mSong = new Song();
                            JSONObject song = hotSongs.getJSONObject(i);
                            JSONArray ar = song.getJSONArray("ar");
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int j = 0; j < ar.length(); j++) {
                                String ar1 = ar.getJSONObject(j).getString("name");
                                if (j != ar.length() - 1)
                                    stringBuilder.append(ar1).append("/");
                                else stringBuilder.append(ar1);
                            }
                            mSong.setArtistId(arId);
                            mSong.setArtist(stringBuilder.toString());
                            mSong.setSongId(song.getString("id"));
                            mSong.setName(song.getString("name"));
                            JSONObject al = song.getJSONObject("al");
                            mSong.setAlbum(al.getString("name"));
                            mSong.setArtistId(al.getString("id"));
                            mSong.setPicUrl(al.getString("picUrl"));
                            mSongList.add(mSong);
                        }
                        songList.setValue(mSongList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getLikeArtist(MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState) {
        IArtistService artistService = retrofit.create(IArtistService.class);
        artistService.artistSublist().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray data = jsonObject.getJSONArray("data");
                        List<Artist> mArtistList = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject artist = data.getJSONObject(i);
                            Artist mArtist = new Artist();
                            mArtist.setName(artist.getString("name"));
                            mArtist.setArId(artist.getString("id"));
                            mArtist.setPicUrl(artist.getString("picUrl"));
                            mArtistList.add(mArtist);
                            Log.d("TAG", "收藏歌手:" + mArtist.getName());
                        }
                        artistList.setValue(mArtistList);
                        Log.d("TAG", "收藏歌手共" + mArtistList.size() + "个");
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Log.d("TAG", "获取收藏歌手解析异常");
                    }
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void likeArtist(String arId, boolean isLike, MutableLiveData<String> requestState) {
        int t = 1;
        if (!isLike) {
            t = 6;
        }
        IArtistService artistService = retrofit.create(IArtistService.class);
        artistService.likeArtist(arId, t).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (isLike) {
                        CloudMusic.likeArtistIdSet.add(arId);
                    } else {
                        CloudMusic.likeArtistIdSet.remove(arId);
                    }
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void sendComment(String content, String id, MutableLiveData<String> requestState, MutableLiveData<Comment> sendComment) {
        IMvService mvService = retrofit.create(IMvService.class);
        mvService.sendComment(1, 1, id, content).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject comment = jsonObject.getJSONObject("comment");
                        Comment mComment = new Comment();
                        mComment.setCommentId(comment.getString("commentId"));
                        mComment.setContent(comment.getString("content"));
                        mComment.setLiked(false);
                        mComment.setTimeStr("刚刚");
                        mComment.setLikedCount(0);
                        JSONObject user = comment.getJSONObject("user");
                        mComment.setNickname(user.getString("nickname"));
                        mComment.setAvatarUrl(user.getString("avatarUrl"));
                        mComment.setUserId(user.getString("userId"));
                        sendComment.setValue(mComment);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void likeComment(String id, boolean isLike, String cid, MutableLiveData<String> requestState) {
        int t = 1;
        if (!isLike)
            t = 0;
        IMvService mvService = retrofit.create(IMvService.class);
        mvService.commentLike(id, cid, t, 1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    requestState.setValue(CloudMusic.SUCCEED);
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void searchArtist(String keyword, MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 28, 0, 100).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray artists = result.getJSONArray("artists");
                        List<Artist> mArtistList = new ArrayList<>();
                        for (int i = 0; i < artists.length(); i++) {
                            Artist mArtist = new Artist();
                            JSONObject artist = artists.getJSONObject(i);
                            mArtist.setArId(artist.getString("id"));
                            mArtist.setName(artist.getString("name"));
                            mArtist.setPicUrl(artist.getString("picUrl"));
                            mArtistList.add(mArtist);
                        }
                        artistList.setValue(mArtistList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loadMoreSearchArtist(String keyword, int offset, MutableLiveData<List<Artist>> artistList, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 28, offset, 100).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray artists = result.getJSONArray("artists");
                        List<Artist> mArtistList = new ArrayList<>();
                        for (int i = 0; i < artists.length(); i++) {
                            Artist mArtist = new Artist();
                            JSONObject artist = artists.getJSONObject(i);
                            mArtist.setArId(artist.getString("id"));
                            mArtist.setName(artist.getString("name"));
                            mArtist.setPicUrl(artist.getString("picUrl"));
                            mArtistList.add(mArtist);
                        }
                        artistList.setValue(mArtistList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void searchMusicList(String keyword, MutableLiveData<List<PlayList>> playLists, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 30, 0, 1000).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray playlists = result.getJSONArray("playlists");
                        List<PlayList> mPlayLists = new ArrayList<>();
                        for (int i = 0; i < playlists.length(); i++) {
                            JSONObject playlist = playlists.getJSONObject(i);
                            PlayList mPlayList = new PlayList();
                            mPlayList.setId(playlist.getString("id"));
                            mPlayList.setName(playlist.getString("name"));
                            mPlayList.setCoverImgUrl(playlist.getString("coverImgUrl"));
                            mPlayList.setDescription(playlist.getString("description"));
                            mPlayLists.add(mPlayList);
                        }
                        playLists.setValue(mPlayLists);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loadMoreMusicList(String keyword, int offset, MutableLiveData<List<PlayList>> playLists, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 30, offset, 1000).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray playlists = result.getJSONArray("playlists");
                        List<PlayList> mPlayLists = new ArrayList<>();
                        for (int i = 0; i < playlists.length(); i++) {
                            JSONObject playlist = playlists.getJSONObject(i);
                            PlayList mPlayList = new PlayList();
                            mPlayList.setId(playlist.getString("id"));
                            mPlayList.setName(playlist.getString("name"));
                            mPlayList.setCoverImgUrl(playlist.getString("coverImgUrl"));
                            mPlayList.setDescription(playlist.getString("description"));
                            mPlayLists.add(mPlayList);
                        }
                        playLists.setValue(mPlayLists);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void searchLyrics(String keyword, MutableLiveData<List<Lyrics>> lyricsList, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 20, 0, 1006).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray songs = result.getJSONArray("songs");
                        List<Lyrics> mLyricsList = new ArrayList<>();
                        for (int i = 0; i < songs.length(); i++) {
                            JSONObject song = songs.getJSONObject(i);
                            Lyrics mLyrics = new Lyrics();
                            mLyrics.setLyrId(song.getString("id"));
                            mLyrics.setName(song.getString("name"));
                            mLyrics.setPicUrl(song.getJSONObject("al").getString("picUrl"));
                            JSONArray lyrics = song.getJSONArray("lyrics");
                            StringBuilder stringBuilder=new StringBuilder();
                            for (int j = 0; j <lyrics.length(); j++) {
                                    stringBuilder.append(lyrics.get(j).toString()).append("\n");
                            }
                            mLyrics.setLyrics(stringBuilder.toString());
                            mLyricsList.add(mLyrics);
                        }
                        lyricsList.setValue(mLyricsList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loadMoreLyrics(String keyword, int offset, MutableLiveData<List<Lyrics>> lyricsList, MutableLiveData<String> requestState) {
        ISearchService searchService = retrofit.create(ISearchService.class);
        searchService.search(keyword, 20, offset, 1006).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray songs = result.getJSONArray("songs");
                        List<Lyrics> mLyricsList = new ArrayList<>();
                        for (int i = 0; i < songs.length(); i++) {
                            JSONObject song = songs.getJSONObject(i);
                            Lyrics mLyrics = new Lyrics();
                            mLyrics.setLyrId(song.getString("id"));
                            mLyrics.setName(song.getString("name"));
                            mLyrics.setPicUrl(song.getJSONObject("al").getString("picUrl"));
                            JSONArray lyrics = song.getJSONArray("lyrics");
                            StringBuilder stringBuilder=new StringBuilder();
                            for (int j = 0; j <lyrics.length(); j++) {
                                stringBuilder.append(lyrics.get(j).toString()).append("\n");
                            }
                            mLyrics.setLyrics(stringBuilder.toString());
                            mLyricsList.add(mLyrics);
                        }
                        lyricsList.setValue(mLyricsList);
                        requestState.setValue(CloudMusic.SUCCEED);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        requestState.setValue(CloudMusic.FAILURE);
                    }
                } else {
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void loginOut(MutableLiveData<String> requestState) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.loginOut().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if(requestState!=null)
                    requestState.setValue(CloudMusic.SUCCEED);
                }
                else {
                    if(requestState!=null)
                    requestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(requestState!=null)
                requestState.setValue(CloudMusic.FAILURE);
            }
        });
    }
}