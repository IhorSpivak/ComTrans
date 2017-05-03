package ru.comtrans.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import ru.comtrans.items.ListItem;
import ru.comtrans.singlets.AppController;


/**
 * Created by Artco on 21.02.2016.
 */
public class Utility {

    private static List<Map<String, List<String>>> categories;

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

    public static boolean containsData(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.contains(key);

    }

    public static Set<String> getStringSet(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getStringSet(key,null);

    }

    public static void saveStringSet(String key,
                                     Set<String> set) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public static void saveData(String key,
                                String data) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static void saveVehicleTypes(ArrayList<ListItem> items) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        JsonArray typeArray = new JsonArray();
        for (ListItem item :
                items) {
            JsonObject typeObject = new JsonObject();
            typeObject.addProperty(ListItem.JSON_VALUE_ID,item.getId());
            typeObject.addProperty(ListItem.JSON_VALUE_NAME,item.getName());
            typeArray.add(typeObject);
        }
        SharedPreferences.Editor editor = appData.edit();
        editor.putString(Const.VEHICLE_TYPE, typeArray.toString());
        editor.apply();
    }


    public static void saveInspectionTypes(ArrayList<ListItem> items) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        JsonArray typeArray = new JsonArray();
        for (ListItem item :
                items) {
            JsonObject typeObject = new JsonObject();
            typeObject.addProperty(ListItem.JSON_VALUE_ID,item.getId());
            typeObject.addProperty(ListItem.JSON_VALUE_NAME,item.getName());
            typeArray.add(typeObject);
        }
        SharedPreferences.Editor editor = appData.edit();
        editor.putString(Const.INSPECTION_TYPE, typeArray.toString());
        editor.apply();
    }

    public static ArrayList<ListItem> getVehicleTypes() {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);

        String vehicleTypesString  = appData.getString(Const.VEHICLE_TYPE, null);
        if(vehicleTypesString!=null){
            Gson gson = new Gson();
            JsonArray typeArray = gson.fromJson(vehicleTypesString,JsonArray.class);
            ArrayList<ListItem> items = new ArrayList<>();
            for (int i = 0; i < typeArray.size(); i++) {
                JsonObject object = typeArray.get(i).getAsJsonObject();
                ListItem item = new ListItem(object.get(ListItem.JSON_VALUE_ID).getAsInt(),
                        object.get(ListItem.JSON_VALUE_NAME).getAsString());
                items.add(item);

            }
            return items;
        }

        return null;
    }

    public static ArrayList<ListItem> getInspectionTypes() {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);

        String vehicleTypesString  = appData.getString(Const.INSPECTION_TYPE, null);
        if(vehicleTypesString!=null){
            Gson gson = new Gson();
            JsonArray typeArray = gson.fromJson(vehicleTypesString,JsonArray.class);
            ArrayList<ListItem> items = new ArrayList<>();
            for (int i = 0; i < typeArray.size(); i++) {
                JsonObject object = typeArray.get(i).getAsJsonObject();
                ListItem item = new ListItem(object.get(ListItem.JSON_VALUE_ID).getAsInt(),
                        object.get(ListItem.JSON_VALUE_NAME).getAsString());
                items.add(item);

            }
            return items;
        }

        return null;
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

    public static void saveLong(String key,
                               long data) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putLong(key, data);
        editor.apply();
    }

    public static long getSavedLong(String key) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getLong(key, 0);

    }

    public static String getToken() {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getString(Const.TOKEN, "").trim();
    }

    public static void saveToken(String token) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putString(Const.TOKEN, token);
        editor.apply();
    }

    public static boolean isAllowBigData() {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getBoolean(Const.SETTINGS_ALLOWS_BIG_DATA, false);
    }

    public static void setIsAllowBigData(boolean isAllowBigData) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean(Const.SETTINGS_ALLOWS_BIG_DATA, isAllowBigData);
        editor.apply();
    }

    public static boolean isAllowMobileConnection() {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        return appData.getBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN, false);
    }

    public static void setIsAllowMobileConnection(boolean isAllowMobileConnection) {
        final SharedPreferences appData = AppController.getInstance().getSharedPreferences(
                Const.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean(Const.SETTINGS_ALLOWS_MOBILE_CONN, isAllowMobileConnection);
        editor.apply();
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


    public static boolean isConnectingToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getType()==ConnectivityManager.TYPE_WIFI&&networkInfo.isConnected()) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isConnectingToFastNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getType()==ConnectivityManager.TYPE_MOBILE&&networkInfo.isConnected()) {
                    return isConnectionFast(networkInfo.getType(),networkInfo.getSubtype());
                }
            }
        }else {
            if (connectivityManager != null) {
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (mobile.isConnected()) {
                    return isConnectionFast(mobile.getType(),mobile.getSubtype());
                }
            }
        }
        return false;
    }


    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
           /*
            * Above API level 7, make sure to set android:targetSdkVersion
            * to appropriate level to use these
            */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    public static boolean isEmailValid(CharSequence email) {
        boolean isEnglish = true;
        for (char c : email.toString().toCharArray()) {
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                isEnglish = false;
                break;
            }
        }
        return isEnglish && email.toString().endsWith("@100fur.ru") && email.toString().length() > 10 || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean isFieldValid(String mask, CharSequence src) {
        Pattern mPattern = Pattern.compile(mask);
        return mPattern.matcher(src).matches();
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


    public static void saveCategories(List<Map<String, List<String>>> cat) {
        categories = cat;
    }

    public static List<Map<String, List<String>>> getCategories() {
        return categories;
    }
}

