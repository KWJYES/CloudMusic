package com.example.cloudmusic.response.network;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.retrofit_api.ILoginService;
import com.example.cloudmusic.response.retrofit_api.IRecommendService;
import com.example.cloudmusic.response.retrofit_api.ISearchService;
import com.example.cloudmusic.response.retrofit_api.ISignUpService;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.cookies.LocalCookieJar;
import com.example.cloudmusic.utils.callback.GetSongUrlCallback;

import com.example.cloudmusic.utils.cookies.ReadCookiesInterceptor;
import com.example.cloudmusic.utils.cookies.SaveCookiesInterceptor;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        if (CloudMusic.getContext() != null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new SaveCookiesInterceptor())
                    .addInterceptor(new ReadCookiesInterceptor())
                    .build();
            retrofit = new Retrofit.Builder().client(okHttpClient).baseUrl(CloudMusic.baseUrl).build();
        }else {
            Log.d("TAG","CloudMusic.getContext() is null!!");
        }
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
                            } catch (JSONException e) {
                                //Log.d("TAG", "该banner song为null");
                                e.printStackTrace();
                            }
                            bannerData.setPic(pic);
                            bannerData.setUrl(url);
                            bannerData.setTypeTitle(typeTitle);
                            bannerData.setSong(songData);
//                            if ((bannerData.getSong() != null && bannerData.getSong().toString().equals("null")) || (bannerData.getUrl() != null && !bannerData.getUrl().equals("null")))
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
    public void getLoginState(MutableLiveData<Boolean> isLogin,MutableLiveData<String> isLoginRequestState) {
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
                            isLogin.setValue(true);
                        } else {
                            isLogin.setValue(false);
                        }
                    } catch (JSONException e) {
                        isLogin.setValue(false);
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("HttpRequestManager","getLoginState : response.code() != 200");
                    isLoginRequestState.setValue(CloudMusic.FAILURE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("HttpRequestManager","getLoginState : onFailure");
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
                if (response.code() == 200){
                    try {
                        if (response.body() != null) {
                            Log.d("TAG","loginRefresh body--->"+response.body().string());
                        }else {
                            Log.d("TAG","loginRefresh--->body is null");
                            Log.d("TAG","loginRefresh headers--->"+response.headers().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loginRefresh.setValue(true);
                }
                else
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
                    if (response.body() != null) {
                        Log.d("TAG","Login json body--->"+response.body().string());
                    }else {
                        Log.d("TAG","Login body is null,Headers--->"+response.headers().toString());
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

//    @Override
//    public void getSongUrl(Song song, MutableLiveData<Song> songLD) {
//        CloudMusic.isGettingSongUrl=true;
//        if (song.getSongId().startsWith("000")) {
//            songLD.setValue(song);
//            CloudMusic.isGettingSongUrl=false;
//            return;//本地
//        }
//        //15min内不重复获取
//        List<Song> songList = MediaManager.getInstance().getSongList();
//        for (Song song1 : songList) {
//            if (song1.getSongId().equals(song.getSongId())) {
//                // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
//                // 计算结束时间 - 开始时间的时间差
//                String starTime=song1.getUrlStartTime();
//                if(starTime==null) break;
//                Calendar calendar = Calendar.getInstance();
//                String endTime = calendar.get(Calendar.YEAR) + "-" +
//                        calendar.get(Calendar.MONTH)+1 + "-" +
//                        calendar.get(Calendar.DAY_OF_MONTH) + " " +
//                        calendar.get(Calendar.HOUR_OF_DAY) + ":" +
//                        calendar.get(Calendar.MINUTE) + ":" +
//                        calendar.get(Calendar.SECOND);
//                @SuppressLint("SimpleDateFormat")
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                try {
//                    Date end = formatter.parse(endTime);//结束时间
//                    Date start = formatter.parse(starTime);//开始时间
//                    // 这样得到的差值是微秒级别
//                    long diff = end.getTime() - start.getTime();
//                    long days = diff / (1000 * 60 * 60 * 24);
//                    long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//                    long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
//                    Log.d("TAG", song.getName() + "url有效时间剩余：" + (-days < 0 ? 0 : -days) + "天" + (-hours < 0 ? 0 : -hours) + "时" + ((20 - minutes) < 0 ? 0 : (20 - minutes)) + "分");
//                    if (days == 0 && hours == 0 && minutes <= 20) {
//                        songLD.setValue(song1);
//                        CloudMusic.isGettingSongUrl=false;
//                        return;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//        ISearchService searchService = retrofit.create(ISearchService.class);
//        searchService.getSongUrl(song.getSongId()).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                if (response.code() == 200) {
//                    try {
//                        if (response.body() != null) {
//                            JSONObject jsonObject = new JSONObject(response.body().string());
//                            JSONArray data = jsonObject.getJSONArray("data");
//                            String url = data.getJSONObject(0).getString("url");
//                            Calendar calendar = Calendar.getInstance();
//                            String urlStartTime = calendar.get(Calendar.YEAR) + "-" +
//                                    calendar.get(Calendar.MONTH) + "-" +
//                                    calendar.get(Calendar.DAY_OF_MONTH)+1 + " " +
//                                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
//                                    calendar.get(Calendar.MINUTE) + ":" +
//                                    calendar.get(Calendar.SECOND);
//                            song.setUrlStartTime(urlStartTime);
//                            song.setUrl(url);
//                            songLD.setValue(song);
//                            CloudMusic.isGettingSongUrl=false;
//                        } else {
//                            songLD.setValue(song);
//                            CloudMusic.isGettingSongUrl=false;
//                        }
//                    } catch (JSONException | IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    songLD.setValue(song);
//                    CloudMusic.isGettingSongUrl=false;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                songLD.setValue(song);
//                CloudMusic.isGettingSongUrl=false;
//            }
//        });
//    }

    @Override
    public void getSongUrl(Song song, GetSongUrlCallback callback) {
        CloudMusic.isGettingSongUrl = true;
        if (song.getSongId().startsWith("000")) {
            callback.onResult(song);
            CloudMusic.isGettingSongUrl = false;
            return;//本地
        }
        //15min内不重复获取
        List<Song> songList = MediaManager.getInstance().getSongList();
        for (Song song1 : songList) {
            if (song1.getSongId().equals(song.getSongId())) {
                // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
                // 计算结束时间 - 开始时间的时间差
                String starTime = song1.getUrlStartTime();
                if (starTime == null) break;
                Calendar calendar = Calendar.getInstance();
                String endTime = calendar.get(Calendar.YEAR) + "-" +
                        calendar.get(Calendar.MONTH) + 1 + "-" +
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
                    Log.d("TAG", song.getName() + "url有效时间剩余：" + (-days < 0 ? 0 : -days) + "天" + (-hours < 0 ? 0 : -hours) + "时" + ((20 - minutes) < 0 ? 0 : (20 - minutes)) + "分");
                    if (days == 0 && hours == 0 && minutes <= 20) {
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
                                    calendar.get(Calendar.MONTH) + "-" +
                                    calendar.get(Calendar.DAY_OF_MONTH) + 1 + " " +
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
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onResult(song);
                    CloudMusic.isGettingSongUrl = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onResult(song);
                CloudMusic.isGettingSongUrl = false;
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
                        Log.d("TAG","captchaSent--->"+jsonObject.toString());
                        int code= jsonObject.getInt("code");
                        if(code==400){
                            sentRequestState.setValue(CloudMusic.FAILURE);
                        }else {
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
    public void login(String phone,String password, String captcha, MutableLiveData<String> loginState) {
        ILoginService loginService = retrofit.create(ILoginService.class);
        loginService.login(phone, "password", captcha).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        Log.d("TAG","Login json body--->"+response.body().string());
                    }else {
                        Log.d("TAG","Login body is null,Headers--->"+response.headers().toString());
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
}
