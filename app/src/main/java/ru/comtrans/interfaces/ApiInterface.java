package ru.comtrans.interfaces;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("prop/22")
    Call<JsonObject> getProp();

    @POST("forgot-password/")
    Call<User> forgotPassword(@Body User user);

    @Headers({"Connection: keep-alive"})
    @POST("put-file/")
    @Multipart
    Call<JsonObject> postFile(@Header("token") String token,@Part MultipartBody.Part file);

    @Headers({"Content-Type: application/json", "Connection: keep-alive"})
    @POST("auto/")
    Call<JsonObject> sendAuto(@Header("token") String token, @Body JsonObject object);

}