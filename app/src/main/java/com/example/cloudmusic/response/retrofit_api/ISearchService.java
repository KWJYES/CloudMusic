package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    /**
     * 传入搜索关键词可以搜索该音乐 / 专辑 / 歌手 / 歌单 / 用户
     * 关键词可以多个以空格隔开
     * @param keywords 关键词
     * @param limit 返回数量 30
     * @param offset 偏移量 0
     * @param type 类型  1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合, 2000:声音(搜索声音返回字段格式会不一样)
     * @return
     */
    @GET("cloudsearch")
    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("limit") int limit,@Query("offset") int offset,@Query("type") int type);

    /**
     * 获取音乐url
     * @param id 歌曲id
     * @return
     */
    @GET("song/url")
    Call<ResponseBody> getSongUrl(@Query("id")String id);


    /**
     * 获取歌词
     * @param songId
     * @return
     */
    @GET("lyric")
    Call<ResponseBody> getSongLrc(@Query("id") String songId);
}
