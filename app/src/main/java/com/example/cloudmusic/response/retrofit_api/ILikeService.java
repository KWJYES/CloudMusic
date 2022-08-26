package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ILikeService {

    /**
     * 喜欢音乐
     * 说明 : 调用此接口 , 传入音乐 id, 可喜欢该音乐
     *
     * 必选参数 : id: 歌曲 id
     *
     * 可选参数 : like: 布尔值 , 默认为 true 即喜欢 , 若传 false, 则取消喜欢
     *
     * 接口地址 : /like
     *
     * 调用例子 : /like?id=347230
     *
     * 喜欢成功则返回数据的 code 为 200, 其余为失败
     * @param like
     * @return
     */
    @POST("like")
    Call<ResponseBody> like(@Query("id") String id,@Query("like") boolean like);

    /**
     *  传入用户 id, 可获取已喜欢音乐 id 列表(id 数组)
     * @param uid 用户id
     * @return
     */
    @GET("likelist")
    Call<ResponseBody> likeList(@Query("uid") String uid);

    /**
     * 传入音乐 id(支持多个 id, 用 , 隔开), 可获得歌曲详情(dt为歌曲时长)
     * @param ids
     * @return
     */
    @GET("song/detail")
    Call<ResponseBody> getSongDetail(@Query("ids") String ids);

}
