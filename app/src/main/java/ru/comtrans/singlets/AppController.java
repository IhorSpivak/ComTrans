package ru.comtrans.singlets;

import android.app.Application;
import android.content.Intent;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.comtrans.helpers.Const;
import ru.comtrans.interfaces.ApiInterface;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.tasks.SendingService;


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
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


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
