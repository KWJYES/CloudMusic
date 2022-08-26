package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IArtistService {
    /**
     * 收藏/取消收藏歌手
     * 说明 : 调用此接口,可收藏歌手
     *
     * 必选参数 :
     *
     * id : 歌手 id
     *
     * t:操作,1 为收藏,其他为取消收藏 6
     *
     * 接口地址 : /artist/sub
     *
     * 调用例子 : /artist/sub?id=6452&t=1
     * @param arId
     * @param t
     * @return
     */
    @POST("artist/sub")
    Call<ResponseBody> likeArtist(@Query("id") String arId, @Query("t") int t);

//    /**
//     * 歌手热门 50 首歌曲
//     * 说明 : 调用此接口,可获取歌手热门 50 首歌曲
//     *
//     * 必选参数 :
//     *
//     * id : 歌手 id
//     *
//     * 接口地址 : /artist/top/song
//     *
//     * 调用例子 : /artist/top/song?id=6452
//     * @param arId
//     * @return
//     */
//    @GET("artist/top/song")
//    Call<ResponseBody> artistTopSong(@Query("id") String arId);

    /**
     * 收藏的歌手列表
     * 说明 : 调用此接口,可获取收藏的歌手列表
     *
     * 接口地址 : /artist/sublist
     *
     * 调用例子 : /artist/sublist
     * @return
     */
    @GET("artist/sublist")
    Call<ResponseBody> artistSublist();

    /**
     * 歌手详细
     * 部分热门歌曲
     * @return
     */
    @GET("artists")
    Call<ResponseBody> artists(@Query("id") String arId);

    /**
     * 歌手分类列表
     * 说明 : 调用此接口,可获取歌手分类列表
     *
     * 可选参数 :
     *
     * limit : 返回数量 , 默认为 30
     *
     * offset : 偏移数量，用于分页 , 如 : 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0 initial: 按首字母索引查找参数,如 /artist/list?type=1&area=96&initial=b 返回内容将以 name 字段开头为 b 或者拼音开头为 b 为顺序排列, 热门传-1,#传 0
     *
     * type 取值:
     * -1:全部
     * 1:男歌手
     * 2:女歌手
     * 3:乐队
     *
     * area 取值:
     * -1:全部
     * 7华语
     * 96欧美
     * 8:日本
     * 16韩国
     * 0:其他
     * 接口地址 : /artist/list
     *
     * 调用例子 : /artist/list?type=1&area=96&initial=b /artist/list?type=2&area=2&initial=b
     * @param type
     * @param area
     * @return
     */
    @GET("artist/list")
    Call<ResponseBody> artistList(@Query("type") int type,@Query("area") int area,@Query("offset") int offset,@Query("limit") int limit);
}
