package ru.comtrans.singlets;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.interfaces.ApiInterface;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.services.SendingService;


/**
 * Created by Artco on 21.02.2016.
 */
public class AppController extends Application {
    private static AppController instance;
    public static ApiInterface apiInterface;
    public   String BASE_URL;

    public AppController() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());



        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("OkHttp", message);
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Basic ZGV2bXVzdDplTk5vU3AjWFJ3MzhR"); // <-- this is the important line

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
        BASE_URL = getString(R.string.API_URL);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        InfoBlocksStorage storage = InfoBlocksStorage.getInstance();

        for (int i = 0; i < storage.getPreviewItems().size(); i++) {
            if (storage.getInfoBlockStatus(storage.getPreviewItems().get(i).getId()) == MyInfoBlockItem.STATUS_SENDING) {
                Intent intentMyIntentService = new Intent(this, SendingService.class);
                intentMyIntentService.putExtra(Const.EXTRA_INFO_BLOCK_ID, storage.getPreviewItems().get(i).getId());
                startService(intentMyIntentService);
            }
        }
    }

    public static AppController getInstance() {
        if (instance == null)
            instance = new AppController();
        return instance;
    }
}
