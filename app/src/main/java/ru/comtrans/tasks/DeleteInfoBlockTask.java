package ru.comtrans.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 03.10.2016.
 */

public class DeleteInfoBlockTask {
    private Context context;

    private InfoBlocksStorage storage;
    private String id;
    private SaveInfoBlockTask.OnPostExecuteListener listener;


    public interface OnPostExecuteListener {
        void onPostExecute();
    }


    public DeleteInfoBlockTask(String id,Context context, SaveInfoBlockTask.OnPostExecuteListener listener){
        init(id,context,listener);
    }


    private void init(String id, Context context, SaveInfoBlockTask.OnPostExecuteListener listener){
        this.context = context;
        this.listener = listener;
        this.id = id;
        storage = InfoBlocksStorage.getInstance();
        new AsyncTaskForDelete().execute();
    }

    private class AsyncTaskForDelete extends AsyncTask<Void,Void,Void> {
        ConnectionProgressDialog dialog;

        @Override
        protected void onPreExecute() {

                try {
                    dialog = new ConnectionProgressDialog(context, R.string.infoblock_deleting);
                    dialog.show();
                } catch (Exception ignored) {
                }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            storage.removeInfoBlock(id);
            SystemClock.sleep(800);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

                try {
                    dialog.dismiss();
                } catch (Exception ignored) {
                }

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            listener.onPostExecute();

        }
    }
}
