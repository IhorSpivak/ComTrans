package ru.comtrans.singlets;

import android.app.Application;


/**
 * Created by Artco on 21.02.2016.
 */
public class AppController extends Application {
    private static AppController instance;

    public AppController() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static AppController getInstance() {
        if (instance == null)
            instance = new AppController();
        return instance;
    }
}
