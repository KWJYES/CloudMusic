package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IPlayListService {

    /**
     * 获取歌单详细，用来拿没有描述歌单的描述
     * @param id
     * @return
     */
    @GET("playlist/detail")
    Call<ResponseBody> playlistDetail(@Query("id") String id);

    /**
     * 获取歌单歌曲
     * @param id
     * @return
     */
    @GET("playlist/track/all")
    Call<ResponseBody> playlistTrackAll(@Query("id") String id);

    /**
     * 调用此接口 , 可获取网友精选碟歌单
     *
     * 可选参数 : order: 可选值为 'new' 和 'hot', 分别对应最新和最热 , 默认为 'hot'
     *
     * cat: tag, 比如 " 华语 "、" 古风 " 、" 欧美 "、" 流行 ", 默认为 "全部",可从歌单分类接口获取(/playlist/catlist)
     *
     * limit: 取出歌单数量 , 默认为 50
     *
     * offset: 偏移数量 , 用于分页 , 如 :( 评论页数 -1)*50, 其中 50 为 limit 的值
     *
     * 接口地址 : /top/playlist
     *
     * 调用例子 : /top/playlist?limit=10&order=new
     * @return
     */
    @GET("top/playlist")
    Call<ResponseBody> getPlayList(@Query("cat") String cat,@Query("limit") int limit,@Query("offset") int offset);
}
