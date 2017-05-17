package ru.comtrans.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.add.AddPropertiesListFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 03.08.2016.
 */
public class SaveInfoBlockTask {
    private Context context;
    private final String LOG_TAG = "myLogs";
    private InfoBlockHelper helper;
    private static SaveInfoBlockTask instance;
    private String id;
    private OnPostExecuteListener listener;
    private boolean withDialog;
    AsyncTaskForSave asyncTaskForSave;
    AsyncTaskForSaveAndExit asyncTaskForSaveAndExit;

    private SaveInfoBlockTask() {}

    public static SaveInfoBlockTask getInstance(String id, Context context, OnPostExecuteListener listener) {
        if (instance == null) {
            instance = new SaveInfoBlockTask();
        }

        instance.init(id,context,listener,true);

        return instance;
    }

    public static SaveInfoBlockTask getInstance(String id,Context context) {
        if (instance == null) {
            instance = new SaveInfoBlockTask();
        }

        instance.init(id,context,null,false);

        return instance;
    }

    public static SaveInfoBlockTask getInstance(String id,Context context, OnPostExecuteListener listener, boolean withDialog) {
        if (instance == null) {
            instance = new SaveInfoBlockTask();
        }

        instance.init(id,context,listener,withDialog);

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



    private void init(String id, Context context, OnPostExecuteListener listener, boolean withDialog){

        this.context = context;
        this.listener = listener;
        this.id = id;
        this.withDialog = withDialog;
        helper = InfoBlockHelper.getInstance();

        if(withDialog) {
            if (asyncTaskForSaveAndExit == null || asyncTaskForSaveAndExit.getStatus() != AsyncTask.Status.RUNNING) {
                asyncTaskForSaveAndExit = (AsyncTaskForSaveAndExit) new AsyncTaskForSaveAndExit().execute();
            } else {
                asyncTaskForSaveAndExit.cancel(true);
                asyncTaskForSaveAndExit = (AsyncTaskForSaveAndExit) new AsyncTaskForSaveAndExit().execute();
            }

        }else {
            if (asyncTaskForSave == null || asyncTaskForSave.getStatus() != AsyncTask.Status.RUNNING) {
                asyncTaskForSave = (AsyncTaskForSave) new AsyncTaskForSave().execute();
                Log.e(LOG_TAG, "AsyncTask не существует, создаем AsyncTaskForSave");
            } else {
                asyncTaskForSave.cancel(true);
                asyncTaskForSave = (AsyncTaskForSave) new AsyncTaskForSave().execute();
                Log.e(LOG_TAG, "AsyncTask существует. Закрываем его, создаем новый AsyncTaskForSave");

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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            helper.cancelSaving();
        }
    }

    private class AsyncTaskForSave extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isCancelled();
            Log.d(LOG_TAG, "isCancelled: " + isCancelled());
            helper.saveAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            if(listener!=null)
                listener.onPostExecute();

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            helper.cancelSaving();
            Log.e(LOG_TAG, "AsyncTask exist. Closing process helper.cancelSaving()");

        }
    }

}

