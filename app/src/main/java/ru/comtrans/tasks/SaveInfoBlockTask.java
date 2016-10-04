package ru.comtrans.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import ru.comtrans.R;
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
    private boolean withDialog;

    public interface OnPostExecuteListener {
        void onPostExecute();
    }


    public SaveInfoBlockTask(String id,Context context, OnPostExecuteListener listener){
        init(id,context,listener,true);
    }

    public SaveInfoBlockTask(String id,Context context, OnPostExecuteListener listener, boolean withDialog){
        init(id,context,listener,withDialog);
    }

    private void init(String id,Context context, OnPostExecuteListener listener, boolean withDialog){
        this.context = context;
        this.listener = listener;
        this.id = id;
        this.withDialog = withDialog;
        helper = InfoBlockHelper.getInstance();
        storage = InfoBlocksStorage.getInstance();
        if(withDialog) {
            new AsyncTaskForSaveAndExit().execute();
        }else{
            new AsyncTaskForSave().execute();
        }
    }

    private class AsyncTaskForSaveAndExit extends AsyncTask<Void,Void,Void> {
        ConnectionProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if(withDialog) {
                try {
                    dialog = new ConnectionProgressDialog(context, R.string.infoblock_saving);
                    dialog.show();
                } catch (Exception ignored) {
                }
            }
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
            if(withDialog) {
                try {
                    dialog.dismiss();
                } catch (Exception ignored) {
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            listener.onPostExecute();

        }
    }

    private class AsyncTaskForSave extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helper.saveAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            listener.onPostExecute();

        }
    }

}
