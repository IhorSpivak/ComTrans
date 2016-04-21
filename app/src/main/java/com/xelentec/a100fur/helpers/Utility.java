package com.xelentec.a100fur.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xelentec.a100fur.singlets.AppController;


/**
 * Created by Artco on 21.02.2016.
 */
public class Utility {

    public static boolean isValidResponse(int responseCode) {

        return (responseCode >= 200 && responseCode <= 299);
    }

    public static void saveBoolean(String key, boolean value) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public static boolean getBoolean(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getBoolean(key, false);
    }

    public static String getSavedData(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getString(key, "").trim();

    }

    public static void saveData(String key,
                                String data) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static void saveInt(String key,
                                int data) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putInt(key, data);
        editor.apply();
    }

    public static int getSavedInt(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getInt(key, 0);

    }








    public static void hideKeyboard(Context c,View v){
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;

        }



    };


}
