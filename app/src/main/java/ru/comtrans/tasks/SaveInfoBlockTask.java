package ru.comtrans.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 03.08.2016.
 */
public class SaveInfoBlockTask {
    private Context context;
    private InfoBlockHelper helper;
    private InfoBlocksStorage storage;
    private String id;
    private OnPostExecuteListener listener;

    public interface OnPostExecuteListener {
        void onPostExecute();
    }


    public SaveInfoBlockTask(String id,Context context, OnPostExecuteListener listener){
        this.context = context;
        this.listener = listener;
        this.id = id;
        helper = InfoBlockHelper.getInstance();
        storage = InfoBlocksStorage.getInstance();
        new AsyncTaskForSaveAndExit().execute();
    }

    private class AsyncTaskForSaveAndExit extends AsyncTask<Void,Void,Void> {
        ConnectionProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            try {
                dialog = new ConnectionProgressDialog(context,"Сохранение инфоблока");
                dialog.show();
            }catch (Exception ignored){}

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helper.saveAll();
            SystemClock.sleep(800);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                dialog.dismiss();
            }catch (Exception ignored){}

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            listener.onPostExecute();

        }
    }
}
