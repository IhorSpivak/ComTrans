package ru.comtrans.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.comtrans.items.User;

public interface ApiInterface {



    @Headers({"Content-Type: application/json", "Connection: keep-alive"})
    @POST("sign-up/")
    Call<User> signUp(@Body User user);

    @Headers({"Content-Type: application/json", "Connection: keep-alive"})
    @POST("sign-in/")
    Call<User> signIn(@Body User user);

    @Headers({"Content-Type: application/json", "Connection: keep-alive"})
    @POST("profile/")
    Call<User> saveProfile(@Header("token") String token, @Body User user);

    @GET("profile/")
    Call<User> getUser(@Header("token") String token);

    @POST("forgot-password/")
    Call<User> forgotPassword(@Body User user);

}