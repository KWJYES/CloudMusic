package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMvService {

    /**
     * 传入 mv id,可获取 mv 播放地址
     * @param id
     * @return
     */
    @GET("mv/url")
    Call<ResponseBody> mvUrl(@Query("id") String id);

    /**
     * 传入 mv id,可获取 mv 评论
     * @return
     */
    @GET("comment/mv")
    Call<ResponseBody> mvComment(@Query("id") String id,@Query("limit") int limit,@Query("offset") int offset);

    /**
     * 调用例子 : /mv/all?area=港台
     * 可选参数 :
     * @param area 地区,可选值为全部,内地,港台,欧美,日本,韩国,不填则为全部
     * @param type type: 类型,可选值为全部,官方版,原生,现场版,网易出品,不填则为全部
     * @param offset 偏移数量 , 用于分页 , 如 :( 页数 -1)*50, 其中 50 为 limit 的值 , 默认 为 0
     * @return
     */
    @GET("mv/all")
    Call<ResponseBody> mvAll(@Query("area") String area,@Query("type") String type,@Query("offset") int offset);

    /**
     * 给评论点赞
     * 接口地址 : /comment/like
     * 调用例子 : /comment/like?id=29178366&cid=12840183&t=1&type=0 对应给 https://music.163.com/#/song?id=29178366 最热门的评论点赞
     * @param id 资源 id, 如歌曲 id,mv id
     * @param cid 评论 id
     * @param t 是否点赞 , 1 为点赞 ,0 为取消点赞
     * @param type 数字 , 资源类型 , 对应歌曲 , mv, 专辑 , 歌单 , 电台, 视频对应以下类型
     *             0: 歌曲
     *             1: mv
     *             2: 歌单
     *             3: 专辑
     *             4: 电台
     *             5: 视频
     *             6: 动态
     * @return
     */
    @POST("comment/like")
    Call<ResponseBody> commentLike(@Query("id") String id,@Query("cid") String cid,@Query("t") int t,@Query("type") int type);


    /**
     * 发表评论
     * @param t 1 发送, 2 回复
     * @param type 数字,资源类型,对应歌曲,mv,专辑,歌单,电台,视频对应以下类型
     * 0: 歌曲
     * 1: mv
     * 2: 歌单
     * 3: 专辑
     * 4: 电台
     * 5: 视频
     * 6: 动态
     * @param id 对应资源 id
     * @param content 要发送的内容
     * @return
     */
    @POST("comment")
    Call<ResponseBody> sendComment(@Query("t")int t,@Query("type") int type,@Query("id")String id,@Query("content") String content);

}
