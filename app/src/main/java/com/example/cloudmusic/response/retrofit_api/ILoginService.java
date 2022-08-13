package com.example.cloudmusic.response.retrofit_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ILoginService {

    /**
     * 登陆
     * 使用 /captcha/sent接口传入手机号获取验证码,调用此接口传入验证码,可使用验证码登录,传入后 password 参数将失效
     * @param phone
     * @param password
     * @param captcha
     * @return
     */
    @POST("login/cellphone")
    Call<ResponseBody> login(@Query("phone") String phone,@Query("password") String password,@Query("captcha") String captcha);
    @POST("login/cellphone")
    Call<ResponseBody> login(@Query("phone") String phone,@Query("password") String password);

    /**
     * 获取登录状态
     * @return
     */
    @GET("login/status")
    Call<ResponseBody> loginState();

    /**
     * 刷新登录状态,返回内容包含新的cookie
     */
    @POST("login/refresh")
    Call<ResponseBody> loginRefresh();
}
