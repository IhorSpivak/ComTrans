package ru.comtrans.tasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.singlets.AppController;
import ru.comtrans.singlets.InfoBlocksStorage;

/**
 * Created by Artco on 04.08.2016.
 */
public class SendingService extends IntentService {
    String id;
    int notificationId = 101;
    InfoBlocksStorage storage;

    public SendingService(){
        super("upload service");
        storage = InfoBlocksStorage.getInstance();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.sending_notification_title))
                .setContentText(getString(R.string.sending_notification_text))
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setAutoCancel(false);

        id = intent.getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        try {
            JsonArray array = storage.getInfoBlockArray(id);
            JsonObject sendObject = new JsonObject();
            sendObject.addProperty("method", "add");
            JsonArray fields = new JsonArray();
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
                            listObject.addProperty(MainItem.JSON_VALUE, object.get(MainItem.JSON_LIST_VALUE)
                                    .getAsJsonObject().get(ListItem.JSON_VALUE_ID).getAsLong());
                            fields.add(listObject);
                        }
                    }

                    if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_NUMBER
                            || object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_STRING)) {

                        if (object.has(MainItem.JSON_VALUE) && !object.get(MainItem.JSON_VALUE).isJsonNull()) {
                            String value = object.get(MainItem.JSON_VALUE).getAsString();
                            if (!value.equals("")) {
                                JsonObject listObject = new JsonObject();
                                listObject.addProperty(MainItem.JSON_CODE, object.get(MainItem.JSON_CODE).getAsString());
                                listObject.addProperty(MainItem.JSON_VALUE, value);
                                fields.add(listObject);
                            }

                        }
                    }


                    if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHOTO ||
                            object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_VIDEO)) {
                        if (object.has(MainItem.JSON_PHOTO_VALUES) && !object.get(MainItem.JSON_PHOTO_VALUES).isJsonNull()) {
                            JsonArray photoValues = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
                            int factImages = 0;
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
                                    if (!photo.has(PhotoItem.JSON_ID) || photo.get(PhotoItem.JSON_ID).isJsonNull()) {
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
                                        factImages++;
                                    }
                                }
                            }


                            if (factImages != 0) {
                                int progress = 0;
                                for (int k = 0; k < photoValues.size(); k++) {

                                    JsonObject photo = photoValues.get(k).getAsJsonObject();
                                    if (photo.has(PhotoItem.JSON_IMAGE_PATH) && !photo.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {
                                        if (!photo.has(PhotoItem.JSON_ID) || !(photo.get(PhotoItem.JSON_ID)).isJsonNull()) {


                                            mBuilder.setProgress(factImages, progress++, false);
                                            mNotifyManager.notify(notificationId, mBuilder.build());
                                            Intent broadcast = new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
                                            broadcast.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                                            broadcast.putExtra(Const.EXTRA_PROGRESS, String.valueOf((int) ((progress * 100.0f) / factImages)) + " %");
                                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);


                                            File file = new File(photo.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                            RequestBody requestFile =
                                                    RequestBody.create(MediaType.parse("image/jpg"), file);

                                            MultipartBody.Part body =
                                                    MultipartBody.Part.createFormData("multipart/form-data", file.getName(), requestFile);

                                            Call<JsonObject> call = AppController.apiInterface.postFile(Utility.getToken(), body);
                                            try {
                                                JsonObject result = call.execute().body();
                                                if (result.has("result") && !result.get("result").isJsonNull()) {
                                                    if (hasNotUploadedDefects) {
                                                        if (photo.has(PhotoItem.JSON_IS_DEFECT) && !photo.get(PhotoItem.JSON_IS_DEFECT).isJsonNull()) {
                                                            if (photo.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean()) {
                                                                defectArray.add(result.get("result").getAsJsonObject().get("id").getAsLong());
                                                            }
                                                        }
                                                    }

                                                    photo.addProperty(PhotoItem.JSON_ID, result.get("result").getAsJsonObject().get("id").getAsLong());
                                                    photoValues.set(k, photo);
                                                }

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }


                                if (hasDefects) {
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

            Log.d("TAG", fields.toString());
            sendObject.add("fields", fields);

            Call<JsonObject> call = AppController.apiInterface.sendAuto(Utility.getToken(), sendObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("TAG", "data sent");

                    mBuilder.setContentText(getString(R.string.sending_notification_done))
                            .setProgress(0, 0, false);
                    mBuilder.setAutoCancel(true);
                    mNotifyManager.notify(notificationId, mBuilder.build());

                    storage.setInfoBlockStatus(id, MyInfoBlockItem.STATUS_SENT);
                    Intent i = new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
                    i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                    i.putExtra(Const.EXTRA_PROGRESS, "100%");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    storage.setInfoBlockStatus(id, MyInfoBlockItem.STATUS_DRAFT);
                    Intent i = new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
                    i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    mBuilder.setContentText(getString(R.string.sending_notification_failed))
                            .setProgress(0, 0, false);
                    mBuilder.setAutoCancel(true);
                    mNotifyManager.notify(notificationId, mBuilder.build());
                }
            });
        }catch (Exception e){
            storage.setInfoBlockStatus(id, MyInfoBlockItem.STATUS_DRAFT);
            Intent i = new Intent(Const.UPDATE_PROGRESS_INFO_BLOCKS_FILTER);
            i.putExtra(Const.EXTRA_INFO_BLOCK_ID, id);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
            Log.e("TAG","not sent error",e);
        }
    }
}
