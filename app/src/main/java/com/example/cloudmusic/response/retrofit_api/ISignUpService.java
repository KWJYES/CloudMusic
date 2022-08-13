package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ISignUpService {

    /**
     * 检测手机号码是否已注册
     * @param phone 手机号
     * @return
     */
    @GET("cellphone/existence/check")
    Call<ResponseBody> checkPhone(@Query("phone") String phone);


    /**
     * 检测昵称是否重复,并提供备用昵称
     * @param nickname
     * @return
     */
    @GET("nickname/check")
    Call<ResponseBody> checkNickname(@Query("nickname") String nickname);

    /**
     * 刚注册的账号(需登录),调用此接口 ,可初始化昵称
     * @param nickname
     * @return
     */
    @GET("activate/init/profile")
    Call<ResponseBody> initNickname(@Query("nickname") String nickname);

    /**
     * 传入手机号码, 可发送验证码
     * @param phone
     * @return
     */
    @GET("captcha/sent")
    Call<ResponseBody> captchaSent(@Query("phone") String phone);

    /**
     * 传入手机号码和验证码, 可校验验证码是否正确
     * @param phone
     * @param captcha
     * @return
     */
    @GET("captcha/verify")
    Call<ResponseBody> checkCaptcha(@Query("phone") String phone,@Query("captcha") String captcha);

    /**
     * 传入手机号码和验证码,密码,昵称, 可注册网易云音乐账号(同时可修改密码)
     * @param captcha
     * @param phone
     * @param password
     * @param nickname
     * @return
     */
    @POST("register/cellphone")
    Call<ResponseBody> signUp(@Query("captcha") String captcha,@Query("phone")String phone,@Query("password")String password,@Query("nickname") String nickname);
}
