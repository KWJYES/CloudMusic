package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IUserDataService {
    /**
     * 登录后调用此接口 , 可以获取用户等级信息,包含当前登录天数,听歌次数,下一等级需要的登录天数和听歌次数,当前等级进度,对应
     * @return
     */
    @GET("user/level")
    Call<ResponseBody> level();


    /**
     *  调用此接口 , 传入签到类型 ( 可不传 , 默认安卓端签到 ), 可签到 ( 需要登录 ), 其中安卓端签到可获得 3 点经验 , web/PC 端签到可获得 2 点经验
     *
     * 可选参数 : type: 签到类型 , 默认 0, 其中 0 为安卓端签到 ,1 为 web/PC 签到
     * @return
     */
    @POST("daily_signin")
    Call<ResponseBody> dailySignIn();
}
