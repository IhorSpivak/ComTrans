package ru.comtrans.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 03.08.2016.
 */
public class SaveInfoBlockTask {
    private Context context;
    private InfoBlockHelper helper;
    private static SaveInfoBlockTask instance;
    private String id;
    private OnPostExecuteListener listener;
    private boolean withDialog;
    AsyncTaskForSave asyncTaskForSave;
//
    private SaveInfoBlockTask() {

    }

    public static SaveInfoBlockTask getInstance() {
        if (instance == null)
            instance = new SaveInfoBlockTask();
        return instance;
    }


    public interface OnPostExecuteListener {
        void onPostExecute();
    }


    public SaveInfoBlockTask(String id,Context context, OnPostExecuteListener listener){
        init(id,context,listener,true);
    }

    public SaveInfoBlockTask(String id,Context context){
        init(id,context,null,false);
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

        if(withDialog) {
            new AsyncTaskForSaveAndExit().execute();
        }else {
            if (asyncTaskForSave == null || asyncTaskForSave.getStatus() != AsyncTask.Status.RUNNING) {
                asyncTaskForSave = (AsyncTaskForSave) new AsyncTaskForSave().execute();
            } else {
                asyncTaskForSave.cancel(true);
                asyncTaskForSave = (AsyncTaskForSave) new AsyncTaskForSave().execute();
            }
        }


//        AsyncTaskForSave asyncTaskForSave = new AsyncTaskForSave();
//        if(withDialog) {
//            new AsyncTaskForSaveAndExit().execute();
//        }else{
//            if(asyncTaskForSave.getStatus() != AsyncTask.Status.RUNNING){
//                asyncTaskForSave = new AsyncTaskForSave();
//                asyncTaskForSave.execute();
//            } else {
//                if (asyncTaskForSave.getStatus() == AsyncTask.Status.RUNNING)
//                    asyncTaskForSave.cancel(true);
//                new AsyncTaskForSave().execute();
//            }
//        }

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
                if (listener != null)
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
            if (isCancelled() == false) {

                helper.saveAll();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            if(listener!=null)
            listener.onPostExecute();

        }
    }

}
