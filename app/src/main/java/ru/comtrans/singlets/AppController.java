package ru.comtrans.singlets;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.comtrans.interfaces.ApiInterface;


/**
 * Created by Artco on 21.02.2016.
 */
public class AppController extends Application {
    private static AppController instance;
    public static ApiInterface apiInterface;
    public static final String BASE_URL = "http://www.rc.100fur.ru/ru/api/";

    public AppController() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();



       Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
       apiInterface =  retrofit.create(ApiInterface.class);
    }

    public static AppController getInstance() {
        if (instance == null)
            instance = new AppController();
        return instance;
    }
}
