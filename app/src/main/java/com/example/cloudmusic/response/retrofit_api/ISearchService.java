package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ISearchService {

    /**
     * 调用此接口 , 可获取默认搜索关键词
     * @return
     */
    @GET("search/default")
    Call<ResponseBody> getDefaultSearchWord();

    /**
     * 调用此接口,可获取热门搜索列表
     * @return
     */
    @GET("search/hot/detail")
    Call<ResponseBody> getHotList();
}
