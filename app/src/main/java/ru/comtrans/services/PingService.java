package ru.comtrans.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;

/**
 * Created by Artco on 07.10.2016.
 */

public class PingService extends Service {
    private CountDownTimer timer;
    private String id;
    private Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG","ping service create");
        timer  = new CountDownTimer(30000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
            thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isReachable;
                        isReachable =
                                isURLReachable(getApplicationContext());

                        if(!Utility.isConnectingToInternet(getApplicationContext())) {
                            Log.d("TAG","first");
                        }else if(Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getApplicationContext())&&!Utility.isConnectingToFastNetwork(getApplicationContext())){
                            Log.d("TAG","second");
                        }else if(!Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getApplicationContext())){
                            Log.d("TAG","third");
                        }else {
                            Log.d("TAG","fourth");
                            if(isReachable){
                                Intent intentMyIntentService = new Intent(getApplicationContext(), SendingService.class);
                                intentMyIntentService.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                                startService(intentMyIntentService);
                                stopSelf();
                            }
                        }
                    }
                });
                thread.start();

            }

            @Override
            public void onFinish() {
                stopSelf();
            }


        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG","service started");
        id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);

        timer.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        Log.d("TAG","ping service destroy");
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static public boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://google.com");   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(300 * 1000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }


}
