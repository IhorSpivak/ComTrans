package ru.comtrans.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ru.comtrans.audio.AudioRecorder;
import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlocksStorage;

/**
 * Created by Artco on 16.11.2016.
 */

public class AudioRecordService extends Service {
    private AudioRecorder recorder;
    private String id;
    private InfoBlocksStorage storage;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        storage = InfoBlocksStorage.getInstance();
        recorder = new AudioRecorder();
        recorder.onRecord(true);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        storage.saveInfoBlockAudio(id,recorder.getFilePath());
        recorder.onRecord(false);
        recorder.onDestroy();
        super.onDestroy();

    }
}
