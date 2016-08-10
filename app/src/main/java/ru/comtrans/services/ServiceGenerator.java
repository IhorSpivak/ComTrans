package ru.comtrans.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String API_BASE_URL = "http://www.rc.100fur.ru/ru/api/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();



    private static Retrofit.Builder builder =  new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = builder.client(httpClient.addInterceptor(interceptor).build()).build();
        return retrofit.create(serviceClass);
    }
}