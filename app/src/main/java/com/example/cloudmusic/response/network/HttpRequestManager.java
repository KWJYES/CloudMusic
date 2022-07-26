package com.example.cloudmusic.response.network;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.retrofit_api.IRecommendService;
import com.example.cloudmusic.utils.CloudMusic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HttpRequestManager implements INetworkRequest{

    /**
     * 使用单例模式
     */
    private HttpRequestManager() {}
    private static HttpRequestManager httpRequestManager;
    public static HttpRequestManager getInstance(){
        if(httpRequestManager==null){
            synchronized (HttpRequestManager.class){
                if(null==httpRequestManager){
                    httpRequestManager=new HttpRequestManager();
                }
            }
        }
        return httpRequestManager;
    }

    /**
     * 获取banner
     * @param bannerRequestResult 处理结果
     * @param bannerRequestState 请求状态
     */
    @Override
    public void getBannerData(MutableLiveData<List<Banner>> bannerRequestResult, MutableLiveData<String> bannerRequestState) {
        Retrofit retrofit=new Retrofit.Builder().baseUrl(CloudMusic.baseUrl).build();
        IRecommendService recommendService=retrofit.create(IRecommendService.class);
        recommendService.getBanners(1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    Log.d("TAG","banner数据请求成功");
                    try {
                        List<Banner> bannerList=new ArrayList<>();
                        bannerRequestState.setValue(CloudMusic.SUCCEED);
                        String jsonData=response.body().string();
                        JSONObject jsonObject=new JSONObject(jsonData);
                        JSONArray banners= jsonObject.getJSONArray("banners");
                        for(int i=0;i<banners.length();i++){
                            Banner bannerData=new Banner();
                            Song songData=new Song();
                            JSONObject banner=banners.getJSONObject(i);
                            //用getString()方法取出数据
                            String pic=banner.getString("pic");
                            String url=banner.getString("url");
                            String typeTitle=banner.getString("typeTitle");
                            try {//捕捉song为null异常
                                JSONObject song=banner.getJSONObject("song");
                                String name=song.getString("name");
                                songData.setName(name);
                                int id=song.getInt("id");
                                songData.setId(id);
                                JSONArray artists= song.getJSONArray("ar");
                                for (int j = 0; j < artists.length(); j++) {
                                    JSONObject artist=artists.getJSONObject(j);
                                    String artistId=artist.getString("id");
                                    songData.setArtistId(artistId);
                                    String artistName=artist.getString("name");
                                    songData.setArtist(artistName);
                                }
                                JSONObject album=song.getJSONObject("al");
                                String albumId= album.getString("id");
                                songData.setAlbumId(albumId);
                                String albumName=album.getString("name");
                                songData.setAlbum(albumName);
                            }catch (JSONException e) {
                                Log.d("TAG","该banner song为null");
                                e.printStackTrace();
                            }
                            bannerData.setPic(pic);
                            bannerData.setUrl(url);
                            bannerData.setTypeTitle(typeTitle);
                            bannerData.setSong(songData);
                            bannerList.add(bannerData);
                        }
                        bannerRequestResult.setValue(bannerList);
                    } catch (IOException e) {
                        Log.d("TAG","获取jsonData异常");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("TAG","banner jsonData解析异常");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG","banner数据请求失败");
                bannerRequestState.setValue(CloudMusic.FAILURE);
            }
        });
    }

    @Override
    public void getLoginState(MutableLiveData<Boolean> loginState) {

    }
}
