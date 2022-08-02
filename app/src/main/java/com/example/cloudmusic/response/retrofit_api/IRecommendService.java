package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 首页推荐页面api
 */
public interface IRecommendService {
    /**
     * 获取Banner资源
     * @param type 1 是Android资源
     * @return ResponseBody
     */
    @GET("banner")
    Call<ResponseBody> getBanners(@Query("type") int type);

    /**
     * 获取推荐歌单
     * @param limit
     * @return
     */
    @GET("personalized")
    Call<ResponseBody> getRecommendMusic(@Query("limit") int limit);
}
