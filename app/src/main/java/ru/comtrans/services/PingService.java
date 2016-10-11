package ru.comtrans.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;

/**
 * Created by Artco on 07.10.2016.
 */

public class PingService extends IntentService {

    public PingService(String name) {
        super(name);
    }

    public PingService() {
        super("ping service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("TAG","service started");
        final String id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        boolean isReachable;

            new CountDownTimer(30000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    stopSelf();
                }
            };

        for(int i=0; i<300; i++){
            try {
                Thread.sleep(1000);
                isReachable =
                        isURLReachable(getApplicationContext());

                if(!Utility.isConnectingToInternet(getApplicationContext())) {
                }else if(Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getApplicationContext())&&!Utility.isConnectingToFastNetwork(getApplicationContext())){
                }else if(!Utility.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN)&&!Utility.isConnectingToWifi(getApplicationContext())){
                }else {
                    if(isReachable){
                        Intent intentMyIntentService = new Intent(getApplicationContext(), SendingService.class);
                        intentMyIntentService.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                        startService(intentMyIntentService);
                        stopSelf();
                        break;
                    }
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }






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
