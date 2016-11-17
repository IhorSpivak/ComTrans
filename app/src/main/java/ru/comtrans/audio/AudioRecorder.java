package ru.comtrans.audio;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Created by Artco on 16.11.2016.
 */

public class AudioRecorder {

    public AudioRecorder(){

    }
    private static File audioFile = null;
    private MediaRecorder mRecorder = null;
    private static final String LOG_TAG = "AudioRecord";

    private void startRecording() {
        File directory = new File(Environment.getExternalStorageDirectory(),"Android/data/ru.comtrans/files/Audios");
        if(directory.mkdirs()||directory.exists()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String prefix = "audio";
            File file = new File(directory, prefix + timeStamp + ".mp4");
            audioFile = file;

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(96000);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile(file.getAbsolutePath());


            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            mRecorder.start();
        }

    }

    public String getFilePath(){
        return audioFile.getAbsolutePath();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onDestroy(){
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
