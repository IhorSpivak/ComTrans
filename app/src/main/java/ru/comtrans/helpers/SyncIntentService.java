package ru.comtrans.helpers;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Artco on 25.07.2016.
 */
public class SyncIntentService  extends IntentService {


    public SyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
