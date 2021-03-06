package ru.comtrans.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.MyInfoblockItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;
import ru.comtrans.singlets.AppController;
import ru.comtrans.singlets.InfoBlocksStorage;

/**
 * Created by Artco on 04.08.2016.
 */
public class SendingService extends IntentService {
    private Handler handler;
    int notificationId = 101;
    private InfoBlocksStorage storage;
    private HashMap<String,Boolean> ids = new HashMap<String,Boolean>();
    private double mb = 1024;
    private double overallUploadedSize = 0;
    private double overallSize = 0;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private String id;
    private  Intent broadcast;

    public SendingService() {
        super("upload service");
        storage = InfoBlocksStorage.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.sending_notification_title))
                .setContentText(getString(R.string.sending_notification_text))
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setAutoCancel(false);



        id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        storage.setInfoBlockStatus(id, MyInfoblockItem.STATUS_SENDING);
        broadcast =  new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
        broadcast.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);


        if (!ids.containsKey(id)) {
            ids.put(id, false);
            JsonArray array = storage.getInfoBlockArray(id);
            JsonObject sendObject = new JsonObject();
            sendObject.addProperty("method", "add");
            JsonArray fields = new JsonArray();
            if (array != null) {
                int factImages = 0;
                for (int i = 0; i < array.size(); i++) {
                    for (int j = 0; j < array.get(i).getAsJsonArray().size(); j++) {

                        JsonObject object = array.get(i).getAsJsonArray().get(j).getAsJsonObject();
                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHOTO ||
                                object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_VIDEO)) {
                            if (object.has(MainItem.JSON_PHOTO_VALUES) && !object.get(MainItem.JSON_PHOTO_VALUES).isJsonNull()) {
                                JsonArray photoValues = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
                                for (int k = 0; k < photoValues.size(); k++) {

                                    JsonObject photo = photoValues.get(k).getAsJsonObject();
                                    if (photo.has(PhotoItem.JSON_IMAGE_PATH) && !photo.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {
                                        File file = new File(photo.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                        if (file.exists()) {
                                            double file_size = file.length() / mb / mb;
                                            overallSize = (overallSize + file_size);

                                            if (photo.get(PhotoItem.JSON_IS_SEND).getAsBoolean()) {
                                                overallUploadedSize = overallUploadedSize + file_size;
                                            }
                                        }
                                        factImages++;

                                    }


                                }

                            }

                        }
                    }
                }

                Log.d("TAG", "overall size " + overallSize);
                Log.d("TAG", "overall uploaded size " + overallUploadedSize);

                File audioFile = new File(storage.getInfoBlockAudio(id));
                if (audioFile.exists()) {

                    double file_size = audioFile.length() / mb / mb;
                    overallSize = file_size + overallSize;
                    RequestBody requestFile;
                    requestFile = RequestBody.create(MediaType.parse("audio/mp4"), audioFile);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("multipart/form-data", audioFile.getName(), requestFile);
                    Call<JsonObject> call = AppController.apiInterface.postFile(Utility.getToken(), body);

                    try {
                        JsonObject result = call.execute().body();
                        if (result.has("result") && !result.get("result").isJsonNull()) {
                            overallUploadedSize = file_size + overallUploadedSize;
                            countAndPublishProgress();
                            long audioId = result.get("result").getAsJsonObject().get("id").getAsLong();
                            JsonObject audioObject = new JsonObject();
                            Log.d("TAG", "recorded audio id " + audioId);
                            audioObject.addProperty(MainItem.JSON_CODE, "recorded_audio");
                            audioObject.addProperty(MainItem.JSON_VALUE, audioId);
                            fields.add(audioObject);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


                for (int i = 0; i < array.size(); i++) {

                    for (int j = 0; j < array.get(i).getAsJsonArray().size(); j++) {
                        JsonObject object = array.get(i).getAsJsonArray().get(j).getAsJsonObject();

                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_FLAG) {
                            JsonObject checkedObject = new JsonObject();
                            checkedObject.addProperty(MainItem.JSON_CODE, object.get(MainItem.JSON_CODE).getAsString());
                            int val = object.get(MainItem.JSON_IS_CHECKED).getAsBoolean() ? 1 : 0;
                            checkedObject.addProperty(MainItem.JSON_VALUE, val);
                            fields.add(checkedObject);
                        }

                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_LIST) {

                            if (object.has(MainItem.JSON_LIST_VALUE) && !object.get(MainItem.JSON_LIST_VALUE).isJsonNull()) {
                                JsonObject listObject = new JsonObject();
                                listObject.addProperty(MainItem.JSON_CODE, object.get(MainItem.JSON_CODE).getAsString());
                                JsonObject savedListObject = object.get(MainItem.JSON_LIST_VALUE)
                                        .getAsJsonObject();
                                if (savedListObject.get(ListItem.JSON_VALUE_ID).getAsLong() != -2) {
                                    listObject.addProperty(MainItem.JSON_VALUE, savedListObject.get(ListItem.JSON_VALUE_ID).getAsLong());
                                } else {
                                    listObject.addProperty("add", savedListObject.get(ListItem.JSON_VALUE_NAME).getAsString());
                                }

                                fields.add(listObject);
                            }
                        }


                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_NUMBER
                                || object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_STRING
                                || object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_EMAIL
                                || object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHONE
                                || object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_CALENDAR)) {

                            if (object.has(MainItem.JSON_VALUE) && !object.get(MainItem.JSON_VALUE).isJsonNull()) {
                                String value = object.get(MainItem.JSON_VALUE).getAsString();
                                if (!value.equals("")) {
                                    if (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHONE && value.equals("+7(")) {

                                    }
                                    {
                                        JsonObject listObject = new JsonObject();
                                        listObject.addProperty(MainItem.JSON_CODE, object.get(MainItem.JSON_CODE).getAsString());
                                        listObject.addProperty(MainItem.JSON_VALUE, value);
                                        fields.add(listObject);
                                    }

                                }

                            }
                        }

                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_TIRE_SCHEME)) {


                            if (object.has(MainItem.JSON_PROTECTOR_VALUES) && !object.get(MainItem.JSON_PROTECTOR_VALUES).isJsonNull()) {
                                Log.d("TAG", "not null");
                                JsonArray protectorArray = object.getAsJsonArray(MainItem.JSON_PROTECTOR_VALUES);

                                for (int k = 0; k < protectorArray.size(); k++) {
                                    JsonObject protectorObject = protectorArray.get(k).getAsJsonObject();
                                    if (protectorObject.has(ProtectorItem.JSON_TYPE) && protectorObject.get(ProtectorItem.JSON_TYPE).getAsInt() == ProtectorItem.TYPE_PROTECTOR) {
                                        if (protectorObject.has(ProtectorItem.JSON_VALUE) && !protectorObject.get(ProtectorItem.JSON_VALUE).isJsonNull()) {
                                            String protectorValue = protectorObject.get(ProtectorItem.JSON_VALUE).getAsString();
                                            Log.d("TAG", "protector value");
                                            if (!protectorValue.equals("")) {
                                                JsonObject newProtectorObject = new JsonObject();
                                                newProtectorObject.addProperty(MainItem.JSON_CODE, protectorObject.get(ProtectorItem.JSON_CODE).getAsString());
                                                newProtectorObject.addProperty(MainItem.JSON_VALUE, protectorValue);
                                                fields.add(newProtectorObject);
                                            }
                                        }


                                    }
                                }

                            }
                        }


                        if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHOTO ||
                                object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_VIDEO)) {
                            if (object.has(MainItem.JSON_PHOTO_VALUES) && !object.get(MainItem.JSON_PHOTO_VALUES).isJsonNull()) {
                                JsonArray photoValues = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
//                        int factImages = 0;
                                boolean hasNotUploadedDefects = false;
                                boolean hasDefects = false;
                                String defectCode = null;
                                JsonArray defectArray = new JsonArray();
                                for (int k = 0; k < photoValues.size(); k++) {

                                    JsonObject photo = photoValues.get(k).getAsJsonObject();
                                    if (photo.has(PhotoItem.JSON_IS_DEFECT) && !photo.get(PhotoItem.JSON_IS_DEFECT).isJsonNull()) {
                                        if (photo.has(PhotoItem.JSON_CODE) && !photo.get(PhotoItem.JSON_CODE).isJsonNull()) {
                                            if (photo.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean()) {
                                                defectCode = photo.get(PhotoItem.JSON_CODE).getAsString();
                                            }
                                        }
                                    }


                                    if (photo.has(PhotoItem.JSON_IMAGE_PATH) && !photo.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {

                                        Log.d("TAG", "code " + photo.get(PhotoItem.JSON_CODE));
                                        if (photo.has(PhotoItem.JSON_IS_DEFECT) && !photo.get(PhotoItem.JSON_IS_DEFECT).isJsonNull()) {
                                            if (photo.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean()) {
                                                if (photo.has(PhotoItem.JSON_ID) && !photo.get(PhotoItem.JSON_ID).isJsonNull()) {
                                                    defectArray.add(photo.get(PhotoItem.JSON_ID).getAsLong());
                                                    hasDefects = true;
                                                } else {
                                                    hasNotUploadedDefects = true;
                                                }
                                            }
                                        }

                                    }
                                }


                                if (factImages != 0) {
                                    for (int k = 0; k < photoValues.size(); k++) {

                                        JsonObject photo = photoValues.get(k).getAsJsonObject();
                                        if (photo.has(PhotoItem.JSON_IMAGE_PATH) && !photo.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {


                                            File file = new File(photo.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                            RequestBody requestFile;
                                            if (photo.has(PhotoItem.JSON_IS_VIDEO) && !photo.get(PhotoItem.JSON_IS_VIDEO).isJsonNull() && photo.get(PhotoItem.JSON_IS_VIDEO).getAsBoolean()) {
                                                requestFile = RequestBody.create(MediaType.parse("video/mp4"), file);
                                            } else {
                                                requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                                            }


                                            MultipartBody.Part body = MultipartBody.Part.createFormData("multipart/form-data", file.getName(), requestFile);


                                            try {
                                                if (!photo.get(PhotoItem.JSON_IS_SEND).getAsBoolean()) {
                                                    Call<JsonObject> call = AppController.apiInterface.postFile(Utility.getToken(), body);
                                                    JsonObject result = call.execute().body();
                                                    if (result.has("result") && !result.get("result").isJsonNull()) {
                                                        double file_size = file.length() / mb / mb;
                                                        overallUploadedSize = overallUploadedSize + file_size;

                                                        long photoId = result.get("result").getAsJsonObject().get("id").getAsLong();

                                                        array.get(i).getAsJsonArray().get(j).getAsJsonObject().getAsJsonArray(MainItem.JSON_PHOTO_VALUES)
                                                                .get(k).getAsJsonObject().addProperty(PhotoItem.JSON_IS_SEND, true);
                                                        array.get(i).getAsJsonArray().get(j).getAsJsonObject().getAsJsonArray(MainItem.JSON_PHOTO_VALUES)
                                                                .get(k).getAsJsonObject().addProperty(PhotoItem.JSON_ID, photoId);

                                                        photo.addProperty(PhotoItem.JSON_ID, photoId);
                                                        photoValues.set(k, photo);
                                                        Log.e("TAG", "sent=" + photo.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                                        storage.saveInfoBlock(id, array);

                                                        if (photo.has(PhotoItem.JSON_IS_DEFECT) && !photo.get(PhotoItem.JSON_IS_DEFECT).isJsonNull()) {
                                                            if (photo.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean()) {
                                                                defectArray.add(photoId);
                                                            }
                                                        }


                                                        countAndPublishProgress();
                                                    }
                                                } else {
                                                    Log.e("TAG", "sent earlier=" + photo.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                                }


                                            } catch (IOException e) {
                                                Log.d("TAG", "error sending photo", e);
                                                break;
                                            }
//                                        }
                                        }
                                    }


                                    if (hasDefects || hasNotUploadedDefects) {
                                        JsonObject defectObject = new JsonObject();
                                        defectObject.addProperty(MainItem.JSON_CODE, defectCode);
                                        defectObject.add(MainItem.JSON_VALUE, defectArray);
                                        fields.add(defectObject);
                                    }
                                    for (int k = 0; k < photoValues.size(); k++) {
                                        JsonObject photo = photoValues.get(k).getAsJsonObject();
                                        if (photo.has(PhotoItem.JSON_IMAGE_PATH) && !photo.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {
                                            if (photo.has(PhotoItem.JSON_IS_DEFECT) && !photo.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean()) {
                                                if (photo.has(PhotoItem.JSON_ID) && !photo.get(PhotoItem.JSON_ID).isJsonNull()) {
                                                    if (photo.has(PhotoItem.JSON_CODE) && !photo.get(PhotoItem.JSON_CODE).isJsonNull()) {
                                                        JsonObject photoObject = new JsonObject();
                                                        photoObject.addProperty(MainItem.JSON_CODE, photo.get(PhotoItem.JSON_CODE).getAsString());
                                                        photoObject.addProperty(MainItem.JSON_VALUE, photo.get(PhotoItem.JSON_ID).getAsLong());
                                                        fields.add(photoObject);
                                                    }

                                                }
                                            }
                                        }
                                    }


                                }
                                object.add(MainItem.JSON_PHOTO_VALUES, photoValues);
                            }
                        }
                        array.get(i).getAsJsonArray().set(j, object);
                    }
                    storage.saveInfoBlock(id, array);

                }

                for (int i = 0; i < fields.size(); i++) {
                    Log.d("TAG", fields.get(i).toString());
                }
                sendObject.add("fields", fields);

//            FileWriter file=null;
//            try {
//                file = new FileWriter(Environment.getExternalStorageDirectory()+"/Comtrans/file.txt");
//                file.write(sendObject.toString());
//                file.flush();
//                file.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

                Call<JsonObject> call = AppController.apiInterface.sendAuto(Utility.getToken(), sendObject);
                try {

                    final JsonObject result = call.execute().body();


                    if (result.has("status") && result.get("status").getAsInt() == 1) {
                        Log.d("TAG", "data sent" + " " + result.toString());


                        mBuilder.setContentText(getString(R.string.sending_notification_done))
                                .setProgress(0, 0, false);
                        mBuilder.setAutoCancel(true);
                        mNotifyManager.notify(notificationId, mBuilder.build());

                        storage.setInfoBlockStatus(id, MyInfoblockItem.STATUS_SENT);
                        Intent i = new Intent(Const.UPDATE_STATUS_INFO_BLOCKS_FILTER);
                        i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                        ids.remove(id);
                    } else {
                        Log.d("TAG", "info block not sent");

                        if (result.has("message")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SendingService.this, result.get("message").getAsString(), Toast.LENGTH_LONG).show();
                                }
                            });
                            mBuilder.setContentText(getString(R.string.sending_notification_failed))
                                    .setProgress(0, 0, false);
                            mBuilder.setAutoCancel(true);
                            mNotifyManager.notify(notificationId, mBuilder.build());
                            storage.setInfoBlockStatus(id, MyInfoblockItem.STATUS_DRAFT);
                            Intent i = new Intent(Const.UPDATE_STATUS_INFO_BLOCKS_FILTER);
                            i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                            ids.remove(id);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "info block not sent");

                    storage.setInfoBlockStatus(id, MyInfoblockItem.STATUS_STOPPED);
                    Intent i = new Intent(Const.UPDATE_STATUS_INFO_BLOCKS_FILTER);
                    i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);

                    mBuilder.setContentText(getString(R.string.sending_notification_failed))
                            .setProgress(0, 0, false);
                    mBuilder.setAutoCancel(true);
                    mNotifyManager.notify(notificationId, mBuilder.build());
                    ids.remove(id);

                    Intent service = new Intent(SendingService.this, PingService.class);
                    service.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                    startService(service);
                }


            }
        }
    }

    private void countAndPublishProgress(){
        mBuilder.setProgress((int)overallSize,(int) overallUploadedSize, false);
        mNotifyManager.notify(notificationId, mBuilder.build());
        broadcast = new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
        broadcast.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
        storage.setInfoBlockProgress(id,(int) ((overallUploadedSize * 100.0f) / overallSize));

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }
}
