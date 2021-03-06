package ru.comtrans.singlets;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.MyInfoblockItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlocksStorage {
    private Set<String> infoBlockIds;
    private static final String INFO_BLOCK_IDS = "ids";
    private String LOG_TAG = "myLogs";
    private double mb = 1024;
    private boolean shouldStopSaving;

    private static InfoBlocksStorage instance;

    public static InfoBlocksStorage getInstance() {
        if (instance == null)
            instance = new InfoBlocksStorage();
        return instance;
    }

    private InfoBlocksStorage() {
        infoBlockIds = Utility.getStringSet(INFO_BLOCK_IDS);
        if (infoBlockIds == null)
            infoBlockIds = new HashSet<>();
    }

    public String saveInfoBlock(String id, JsonArray block) {
        if (!infoBlockIds.contains(id)) {
            infoBlockIds.add(id);
            Utility.saveStringSet(INFO_BLOCK_IDS, infoBlockIds);
        }
        // createSendObject(id,block);
        Utility.saveData(id, block.toString());
        saveInfoBlockPreview(id, block);
        return id;
    }

    public void removeInfoBlock(String id) {
        if (infoBlockIds.contains(id)) {
            infoBlockIds.remove(id);
            Utility.saveStringSet(INFO_BLOCK_IDS, infoBlockIds);
        }
        Gson gson = new Gson();
        JsonArray array = gson.fromJson(Utility.getSavedData(id), JsonArray.class);
        for (int i = 0; i < array.size(); i++) {
            JsonArray screenArray = array.get(i).getAsJsonArray();
            for (int j = 0; j < screenArray.size(); j++) {
                JsonObject object = screenArray.get(j).getAsJsonObject();
                if (object.has(MainItem.JSON_PHOTO_VALUES)) {
                    JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
                    for (int k = 0; k < valuesArray.size(); k++) {
                        JsonObject valueObject = valuesArray.get(k).getAsJsonObject();


                        if (valueObject.has(PhotoItem.JSON_IMAGE_PATH) && !valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()) {
                            try {
                                File file = new File(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                                file.delete();
                            } catch (Exception ignored) {
                            }
                        }

                    }
                }

            }


        }
        try {
            File file = new File(getInfoBlockAudio(id));
            file.delete();
        } catch (Exception ignored) {
        }

        Utility.saveData(id, null);


    }

    public static void setInfoBlockStatus(String id, int status) {
        Utility.saveInt("status" + id, status);
    }

    public static void setInfoBlockProgress(String id, int progress) {
        Utility.saveInt("progress" + id, progress);
    }

    public static int getInfoBlockProgress(String id) {
        return Utility.getSavedInt("progress" + id);
    }

    public static int getInfoBlockStatus(String id) {
        return Utility.getSavedInt("status" + id);
    }

    public void saveInfoBlockAudio(String id, String path) {
        Utility.saveData("audio" + id, path);
    }

    public String getInfoBlockAudio(String id) {
        return Utility.getSavedData("audio" + id);
    }


    public static void setInfoBlockCategoryCode(String id, long code) {
        Utility.saveLong("category_code_" + id, code);
    }

    public static void setInfoBlockInspectionCode(String id, long code) {
        Utility.saveLong("inspection_code_" + id, code);
    }

    public static long getInfoBlockCategoryCode(String id) {
        return Utility.getSavedLong("category_code_" + id);
    }

    public static long getInfoBlockInspectionCode(String id) {
        return Utility.getSavedLong("inspection_code_" + id);
    }

    public ArrayList<MyInfoblockItem> getPreviewItems() {
        ArrayList<MyInfoblockItem> items = new ArrayList<>();


        for (String s :
                infoBlockIds) {
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(Utility.getSavedData("preview" + s), JsonObject.class);

            if (object != null) {

                MyInfoblockItem item = new MyInfoblockItem();

                if (object.has(MyInfoblockItem.JSON_MARK) && !object.get(MyInfoblockItem.JSON_MARK).isJsonNull())
                    item.setMark(object.get(MyInfoblockItem.JSON_MARK).getAsString());

                if (object.has(MyInfoblockItem.JSON_MODEL) && !object.get(MyInfoblockItem.JSON_MODEL).isJsonNull())
                    item.setModel(object.get(MyInfoblockItem.JSON_MODEL).getAsString());

                if (object.has(MyInfoblockItem.JSON_YEAR) && !object.get(MyInfoblockItem.JSON_YEAR).isJsonNull())
                    item.setYear(object.get(MyInfoblockItem.JSON_YEAR).getAsString());

                if (object.has(MyInfoblockItem.JSON_ID) && !object.get(MyInfoblockItem.JSON_ID).isJsonNull())
                    item.setId(object.get(MyInfoblockItem.JSON_ID).getAsString());

                if (object.has(MyInfoblockItem.JSON_PHOTO_PATH) && !object.get(MyInfoblockItem.JSON_PHOTO_PATH).isJsonNull())
                    item.setPhotoPath(object.get(MyInfoblockItem.JSON_PHOTO_PATH).getAsString());

                if (object.has(MyInfoblockItem.JSON_DATE) && !object.get(MyInfoblockItem.JSON_DATE).isJsonNull())
                    item.setDate(object.get(MyInfoblockItem.JSON_DATE).getAsString());

                if (object.has(MyInfoblockItem.JSON_LAST_POSITION) && !object.get(MyInfoblockItem.JSON_LAST_POSITION).isJsonNull())
                    item.setLastPosition(object.get(MyInfoblockItem.JSON_LAST_POSITION).getAsInt());


                if (object.has(MyInfoblockItem.JSON_SIZE) && !object.get(MyInfoblockItem.JSON_SIZE).isJsonNull()) {
                    item.setSize(object.get(MyInfoblockItem.JSON_SIZE).getAsDouble());
                }


                item.setStatus(getInfoBlockStatus(item.getId()));
                item.setProgress(getInfoBlockProgress(item.getId()));

                items.add(item);
            }


        }

        Collections.sort(items, new Comparator<MyInfoblockItem>() {
            @Override
            public int compare(MyInfoblockItem i1, MyInfoblockItem i2) {


                int returnValue = 0;


                SimpleDateFormat sdf = new SimpleDateFormat(Const.INFO_BLOCK_FULL_DATE_FORMAT, Locale.getDefault());
                Date date1;
                Date date2;
                try {
                    date1 = sdf.parse(i1.getDate());
                    date2 = sdf.parse(i2.getDate());

                    if (date1 != null && date2 != null) {
                        if (i1.getStatus() == i2.getStatus() && i1.getStatus() == MyInfoblockItem.STATUS_DRAFT) {
                            if (date1.after(date2)) {
                                returnValue = -1;
                            } else if (date2.after(date1)) {
                                returnValue = 1;
                            } else {
                                returnValue = 0;
                            }
                        } else if (i1.getStatus() == MyInfoblockItem.STATUS_DRAFT) {
                            returnValue = -1;
                        } else if (i2.getStatus() == MyInfoblockItem.STATUS_DRAFT) {
                            returnValue = 1;
                        } else if (i1.getStatus() == i2.getStatus() && i1.getStatus() == MyInfoblockItem.STATUS_SENDING) {
                            if (date1.after(date2)) {
                                returnValue = -1;
                            } else if (date2.after(date1)) {
                                returnValue = 1;
                            } else {
                                returnValue = 0;
                            }
                        } else if (i1.getStatus() == MyInfoblockItem.STATUS_SENDING) {
                            returnValue = -1;
                        } else if (i2.getStatus() == MyInfoblockItem.STATUS_SENDING) {
                            returnValue = 1;
                        } else if (i1.getStatus() == i2.getStatus() && i1.getStatus() == MyInfoblockItem.STATUS_SENT) {
                            if (date1.after(date2)) {
                                returnValue = -1;
                            } else if (date2.after(date1)) {
                                returnValue = 1;
                            } else {
                                returnValue = 0;
                            }
                        } else if (i1.getStatus() == MyInfoblockItem.STATUS_SENT) {
                            returnValue = -1;
                        } else if (i2.getStatus() == MyInfoblockItem.STATUS_SENT) {
                            returnValue = 1;
                        } else {
                            returnValue = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return returnValue;


            }
        });


        return items;
    }

    public void updateInfoBlockPage(String id, int page) {
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(Utility.getSavedData("preview" + id), JsonObject.class);

        if (object != null) {
            object.addProperty(MyInfoblockItem.JSON_LAST_POSITION, page);
            Utility.saveData("preview" + id, object.toString());
        }
    }


    public void saveInfoBlockPreview(String id, JsonArray block) {
        Gson gson = new Gson();
        JsonObject previewObject = gson.fromJson(Utility.getSavedData("preview" + id), JsonObject.class);
        if (previewObject == null) {
            previewObject = new JsonObject();
        }
        Calendar c = Calendar.getInstance();

        float size = 0;

        for (int i = 0; i < block.size(); i++) {
            for (int j = 0; j < block.get(i).getAsJsonArray().size(); j++) {
                JsonObject object = block.get(i).getAsJsonArray().get(j).getAsJsonObject();
                if (object.has(MainItem.JSON_CODE) && !object.get(MainItem.JSON_CODE).isJsonNull()) {
                    if (object.get(MainItem.JSON_CODE).getAsString().equals("general_marka")) {
                        previewObject.addProperty(MyInfoblockItem.JSON_MARK,
                                object.get(MainItem.JSON_LIST_VALUE).
                                        getAsJsonObject().get(ListItem.JSON_VALUE_NAME).getAsString());
                    }
                    if (object.get(MainItem.JSON_CODE).getAsString().equals("general_model")) {
                        previewObject.addProperty(MyInfoblockItem.JSON_MODEL,
                                object.get(MainItem.JSON_LIST_VALUE).
                                        getAsJsonObject().get(ListItem.JSON_VALUE_NAME).getAsString());
                    }
                    if (object.get(MainItem.JSON_CODE).getAsString().equals("general_year")) {
                        previewObject.addProperty(MyInfoblockItem.JSON_YEAR,
                                object.get(MainItem.JSON_LIST_VALUE).
                                        getAsJsonObject().get(ListItem.JSON_VALUE_NAME).getAsString());
                    }


                }


                if (object.has(MainItem.JSON_TYPE) && !object.get(MainItem.JSON_TYPE).isJsonNull() && (object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_PHOTO ||
                        object.get(MainItem.JSON_TYPE).getAsInt() == MainItem.TYPE_VIDEO)) {
                    if (object.has(MainItem.JSON_PHOTO_VALUES) && !object.get(MainItem.JSON_PHOTO_VALUES).isJsonNull()) {
                        JsonArray photoArray = object.get(MainItem.JSON_PHOTO_VALUES).getAsJsonArray();

                        for (int k = 0; k < photoArray.size(); k++) {
                            JsonObject photoObject = photoArray.get(k).getAsJsonObject();

                            if (photoObject.has(PhotoItem.JSON_SIZE) && !photoObject.get(PhotoItem.JSON_SIZE).isJsonNull()) {
                                if (photoObject.get(PhotoItem.JSON_SIZE).getAsDouble() != 0.0) {
                                    size = size + photoObject.get(PhotoItem.JSON_SIZE).getAsFloat();
                                    Log.d("TAG", "file size " + photoObject.get(PhotoItem.JSON_SIZE).getAsFloat());
                                    Log.d("TAG", "total size " + size);
                                }

                            }

                            if (photoObject.has(PhotoItem.JSON_IMAGE_PATH) && !photoObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull()
                                    && photoObject.has(PhotoItem.JSON_IS_VIDEO) && !photoObject.get(PhotoItem.JSON_IS_VIDEO).getAsBoolean()) {
                                previewObject.addProperty(MyInfoblockItem.JSON_PHOTO_PATH, photoObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());

                            }
                        }


                    }

                }
            }
        }

        File audioFile = new File(getInfoBlockAudio(id));
        if (audioFile.exists()) {
            double file_size = audioFile.length() / mb / mb;
            size = (float) (size + file_size);
        }
        SimpleDateFormat df = new SimpleDateFormat(Const.INFO_BLOCK_FULL_DATE_FORMAT, Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        previewObject.addProperty(MyInfoblockItem.JSON_SIZE, size);
        previewObject.addProperty(MyInfoblockItem.JSON_DATE, formattedDate);
        previewObject.addProperty(MyInfoblockItem.JSON_ID, id);

        Utility.saveData("preview" + id, previewObject.toString());
    }

    private JsonArray saveInfoBlockIntItem(ArrayList<Integer> list) {
        if (list != null && list.size() > 0) {
            JsonArray values = new JsonArray();
            for (Integer integer :
                    list) {
                values.add(integer);
            }
            return values;
        }
        return new JsonArray();
    }

    private JsonArray saveInfoBlockStrItem(ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            JsonArray values = new JsonArray();
            for (String str :
                    list) {
                values.add(str);
            }
            return values;
        }
        return new JsonArray();
    }

    public String saveInfoBlock(String id, ArrayList<ArrayList<MainItem>> block) {
        synchronized (this) {
            JsonArray array = new JsonArray();

            for (int i = 0; i < block.size(); i++) {
                if (!shouldStopSaving) {
                    Log.e(LOG_TAG, "AsyncTask exist. Closing process" + shouldStopSaving);
                    shouldStopSaving = false;
                    return id;
                }
                JsonArray screenArray = new JsonArray();
                for (int j = 0; j < block.get(i).size(); j++) {
                    if (!shouldStopSaving) {
                        Log.e(LOG_TAG, "AsyncTask exist. Closing process" + shouldStopSaving);
                        return id;
                    }
                    JsonObject object = new JsonObject();

                    MainItem item = block.get(i).get(j);

                    object.addProperty(MainItem.JSON_CODE, item.getCode());
                    object.addProperty(MainItem.JSON_ID, item.getId());
                    object.addProperty(MainItem.JSON_NAME, item.getName());
                    object.addProperty(MainItem.JSON_VALUE, item.getValue());
                    object.addProperty(MainItem.JSON_DEFAULT_VALUE, item.getDefaultValue());
                    object.addProperty(MainItem.JSON_TYPE, item.getType());
                    object.addProperty(MainItem.JSON_IS_CHECKED, item.isChecked());
                    object.addProperty(MainItem.JSON_IS_REQUIRED, item.isRequired());
                    object.addProperty(MainItem.JSON_CAN_ADD, item.canAdd());
                    object.addProperty(MainItem.JSON_CAPITALIZE, item.isCapitalize());
                    object.addProperty(MainItem.IS_NEVER_MODIFIED, item.isNeverModified());

                    if (item.getListValue() != null) {
                        JsonObject listObject = new JsonObject();
                        ListItem listItem = item.getListValue();
                        listObject.addProperty(ListItem.JSON_VALUE_ID, listItem.getId());
                        listObject.addProperty(ListItem.JSON_VALUE_NAME, listItem.getName());

                        listObject.add(ListItem.JSON_VALUE_MARK, saveInfoBlockIntItem(listItem.getMark()));
                        listObject.add(ListItem.JSON_VALUE_ENGINE_MARK, saveInfoBlockIntItem(listItem.getEngineMark()));
                        listObject.add(ListItem.JSON_TIRE_SCHEME_ID, saveInfoBlockIntItem(listItem.getTireSchemeId()));
                        listObject.add(ListItem.JSON_VALUE_MODEL, saveInfoBlockIntItem(listItem.getModel()));
                        listObject.add(ListItem.JSON_VALUE_ENGINE_MODEL, saveInfoBlockIntItem(listItem.getEngineModel()));
                        listObject.add(ListItem.JSON_VALUE_KPP_MODEL, saveInfoBlockIntItem(listItem.getKppModel()));
                        listObject.add(ListItem.JSON_VALUE_VEHICLE_OWNER, saveInfoBlockIntItem(listItem.getVehicleOwner()));
                        listObject.add(ListItem.JSON_VALUE_KPP_MARK, saveInfoBlockIntItem(listItem.getKppMark()));
                        listObject.add(ListItem.JSON_VALUE_KMU_MARK, saveInfoBlockIntItem(listItem.getKmuMark()));
                        listObject.add(ListItem.JSON_VALUE_KHOU_MARK, saveInfoBlockIntItem(listItem.getKhouMark()));
                        listObject.add(ListItem.JSON_VALUE_SECTIONS, saveInfoBlockIntItem(listItem.getSections()));
                        listObject.add(ListItem.JSON_PROTECTOR_VALUES, saveInfoBlockStrItem(listItem.getProtectorValues()));
                        listObject.add(ListItem.JSON_REVEAL_OS, saveInfoBlockIntItem(listItem.getRevealOs()));

//                        if (listItem.getRevealOs() != null && listItem.getRevealOs().size() > 0) {
//                            JsonArray revealOsValues = new JsonArray();
//                            for (Integer integer :
//                                    listItem.getRevealOs()) {
//                                revealOsValues.add(integer);
//                            }
//                            listObject.add(ListItem.JSON_REVEAL_OS, revealOsValues);
//                        }

                        object.add(MainItem.JSON_LIST_VALUE, listObject);
                    }

                    if (item.getListValues() != null && item.getListValues().size() > 0) {
                        JsonArray listArray = new JsonArray();
                        for (int k = 0; k < item.getListValues().size(); k++) {
                            JsonObject listObject = new JsonObject();
                            ListItem listItem = item.getListValues().get(k);
                            listObject.addProperty(ListItem.JSON_VALUE_ID, listItem.getId());
                            listObject.addProperty(ListItem.JSON_VALUE_NAME, listItem.getName());

                            listObject.add(ListItem.JSON_VALUE_MARK, saveInfoBlockIntItem(listItem.getMark()));
                            listObject.add(ListItem.JSON_VALUE_ENGINE_MARK, saveInfoBlockIntItem(listItem.getEngineMark()));
                            listObject.add(ListItem.JSON_TIRE_SCHEME_ID, saveInfoBlockIntItem(listItem.getTireSchemeId()));
                            listObject.add(ListItem.JSON_VALUE_MODEL, saveInfoBlockIntItem(listItem.getModel()));
                            listObject.add(ListItem.JSON_VALUE_ENGINE_MODEL, saveInfoBlockIntItem(listItem.getEngineModel()));
                            listObject.add(ListItem.JSON_VALUE_KPP_MODEL, saveInfoBlockIntItem(listItem.getKppModel()));
                            listObject.add(ListItem.JSON_VALUE_VEHICLE_OWNER, saveInfoBlockIntItem(listItem.getVehicleOwner()));
                            listObject.add(ListItem.JSON_VALUE_KPP_MARK, saveInfoBlockIntItem(listItem.getKppMark()));
                            listObject.add(ListItem.JSON_VALUE_KMU_MARK, saveInfoBlockIntItem(listItem.getKmuMark()));
                            listObject.add(ListItem.JSON_VALUE_KHOU_MARK, saveInfoBlockIntItem(listItem.getKhouMark()));
                            listObject.add(ListItem.JSON_VALUE_SECTIONS, saveInfoBlockIntItem(listItem.getSections()));
                            listObject.add(ListItem.JSON_PROTECTOR_VALUES, saveInfoBlockStrItem(listItem.getProtectorValues()));
                            listObject.add(ListItem.JSON_REVEAL_OS, saveInfoBlockIntItem(listItem.getRevealOs()));

                            listArray.add(listObject);
                        }
                        object.add(MainItem.JSON_LIST_VALUES, listArray);
                    }

                    if (item.getProtectorItems() != null && item.getProtectorItems().size() > 0) {
                        JsonArray protectorArray = new JsonArray();
                        for (int k = 0; k < item.getProtectorItems().size(); k++) {
                            JsonObject protectorObject = new JsonObject();
                            ProtectorItem protectorItem = item.getProtectorItems().get(k);
                            protectorObject.addProperty(ProtectorItem.JSON_TITLE, protectorItem.getTitle());
                            protectorObject.addProperty(ProtectorItem.JSON_CODE, protectorItem.getCode());
                            protectorObject.addProperty(ProtectorItem.JSON_GROUP_NAME, protectorItem.getGroupName());
                            protectorObject.addProperty(ProtectorItem.JSON_VALUE, protectorItem.getValue());
                            protectorObject.addProperty(ProtectorItem.JSON_TYPE, protectorItem.getType());

                            protectorArray.add(protectorObject);
                        }
                        object.add(MainItem.JSON_PROTECTOR_VALUES, protectorArray);
                    }

                    if (item.getPhotoItems() != null && item.getPhotoItems().size() > 0) {
                        JsonArray photoArray = new JsonArray();
                        for (int k = 0; k < item.getPhotoItems().size(); k++) {
                            JsonObject photoObject = new JsonObject();
                            PhotoItem photoItem = item.getPhotoItems().get(k);
                            photoObject.addProperty(PhotoItem.JSON_DURATION, photoItem.getDuration());

                            if (photoItem.getId() != 0)
                                photoObject.addProperty(PhotoItem.JSON_ID, photoItem.getId());

                            if (photoItem.getImagePath() != null && !photoItem.getImagePath().equals("")) {
                                File file = new File(Uri.parse(photoItem.getImagePath()).getPath());

                                // long file_size = Long.parseLong(String.valueOf(file.length() / 1024 / 1024));
                                double file_size = file.length() / mb / mb;
                                Log.d("TAG", "file size " + file_size);

                                photoObject.addProperty(PhotoItem.JSON_SIZE, file_size);
                            }


                            if (photoItem.getIsOs() != 0) {
                                photoObject.addProperty(PhotoItem.JSON_IS_OS, photoItem.getIsOs());
                            }
                            photoObject.addProperty(PhotoItem.JSON_IMAGE_PATH, photoItem.getImagePath());
                            photoObject.addProperty(PhotoItem.JSON_CODE, photoItem.getCode());
                            photoObject.addProperty(PhotoItem.JSON_IS_DEFECT, photoItem.isDefect());
                            photoObject.addProperty(PhotoItem.JSON_IS_VIDEO, photoItem.isVideo());
                            photoObject.addProperty(PhotoItem.JSON_TITLE, photoItem.getTitle());
                            photoObject.addProperty(PhotoItem.JSON_IS_EDITED, photoItem.isEdited());
                            photoObject.addProperty(PhotoItem.JSON_IS_SEND, photoItem.isSend());
                            photoObject.addProperty(PhotoItem.JSON_IS_REQUIRED,photoItem.isRequired());
                            photoObject.addProperty(PhotoItem.JSON_RE_PHOTO_COUNT, photoItem.getRePhotoCount());
                            photoArray.add(photoObject);
                        }
                        object.add(MainItem.JSON_PHOTO_VALUES, photoArray);
                    }
                    screenArray.add(object);
                }
                array.add(screenArray);
            }

            saveInfoBlock(id, array);

            return id;
        }
    }


    public JsonArray getInfoBlockArray(String id) {
        Gson gson = new Gson();
        return gson.fromJson(Utility.getSavedData(id), JsonArray.class);
    }

    public ArrayList<Uri> getPhotoAndVideo(String id, Context context) {
        ArrayList<Uri> uris = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray array = gson.fromJson(Utility.getSavedData(id), JsonArray.class);
        for (int i = 0; i < array.size(); i++) {
            JsonArray screenArray = array.get(i).getAsJsonArray();
            for (int j = 0; j < screenArray.size(); j++) {
                JsonObject object = screenArray.get(j).getAsJsonObject();
                if (object.has(MainItem.JSON_PHOTO_VALUES)) {
                    JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);

                    for (int k = 0; k < valuesArray.size(); k++) {
                        JsonObject valueObject = valuesArray.get(k).getAsJsonObject();

                        if (valueObject.has(PhotoItem.JSON_IMAGE_PATH) && !valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull() &&
                                valueObject.has(PhotoItem.JSON_IS_VIDEO) && !valueObject.get(PhotoItem.JSON_IS_VIDEO).isJsonNull() &&
                                !valueObject.get(PhotoItem.JSON_IS_VIDEO).getAsBoolean() && uris.size() == 0) {
                            Log.d("TAG", valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                            File file = new File(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                            uris.add(FileProvider.getUriForFile(context, context.getString(R.string.provider_authority), file));
                        }


                        if (valueObject.has(PhotoItem.JSON_IMAGE_PATH) && !valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull() &&
                                valueObject.has(PhotoItem.JSON_IS_VIDEO) && !valueObject.get(PhotoItem.JSON_IS_VIDEO).isJsonNull() &&
                                valueObject.get(PhotoItem.JSON_IS_VIDEO).getAsBoolean() && uris.size() == 1) {
                            Log.d("TAG", valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                            File file = new File(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());
                            uris.add(FileProvider.getUriForFile(context, context.getString(R.string.provider_authority), file));

                        }

                    }

                }
            }
        }
        return uris;

    }

    private ArrayList<Integer> getInfoBlockIntItem(String key, JsonObject valueObject) {
        if (valueObject.has(key) && !valueObject.get(key).isJsonNull()) {
            ArrayList<Integer> val = new ArrayList<>();
            if (valueObject.get(key).isJsonArray()) {
                JsonArray arr = valueObject.getAsJsonArray(key);
                for (int k = 0; k < arr.size(); k++) {
                    val.add(Integer.parseInt(arr.get(k).getAsString()));
                }
            } else {
                val.add(valueObject.get(key).getAsInt());
            }
            return val;
        }
        return null;
    }

    private ArrayList<String> getInfoBlockStringItem(String key, JsonObject valueObject) {
        if (valueObject.has(key) && !valueObject.get(key).isJsonNull()) {
            ArrayList<String> val = new ArrayList<>();
            if (valueObject.get(key).isJsonArray()) {
                JsonArray arr = valueObject.getAsJsonArray(key);
                for (int k = 0; k < arr.size(); k++) {
                    val.add(arr.get(k).getAsString());
                }
            } else {
                val.add(valueObject.get(key).getAsString());
            }
            return val;
        }
        return null;
    }

    public ArrayList<ArrayList<MainItem>> getInfoBlock(String id) {
        Gson gson = new Gson();
        JsonArray array = gson.fromJson(Utility.getSavedData(id), JsonArray.class);
        ArrayList<ArrayList<MainItem>> arrayOfItems = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            try {
                JsonArray screenArray = array.get(i).getAsJsonArray();
                ArrayList<MainItem> mainItems = new ArrayList<>();
                for (int j = 0; j < screenArray.size(); j++) {
                    JsonObject object = screenArray.get(j).getAsJsonObject();
                    Log.d("TAG", "object to string " + object.toString());
                    MainItem item = new MainItem(object.get(MainItem.JSON_TYPE).getAsInt());


                    if (object.has(MainItem.JSON_NAME) && !object.get(MainItem.JSON_NAME).isJsonNull())
                        item.setName(object.get(MainItem.JSON_NAME).getAsString());

                    if (object.has(MainItem.IS_NEVER_MODIFIED) && !object.get(MainItem.IS_NEVER_MODIFIED).isJsonNull())
                        item.setNeverModified(object.get(MainItem.IS_NEVER_MODIFIED).getAsBoolean());

                    if (object.has(MainItem.JSON_IS_CHECKED) && !object.get(MainItem.JSON_IS_CHECKED).isJsonNull())
                        item.setChecked(object.get(MainItem.JSON_IS_CHECKED).getAsBoolean());

                    if (object.has(MainItem.JSON_IS_REQUIRED) && !object.get(MainItem.JSON_IS_REQUIRED).isJsonNull())
                        item.setRequired(object.get(MainItem.JSON_IS_REQUIRED).getAsBoolean());

                    if (object.has(MainItem.JSON_ID) && !object.get(MainItem.JSON_ID).isJsonNull())
                        item.setId(object.get(MainItem.JSON_ID).getAsString());

                    if (object.has(MainItem.JSON_CODE) && !object.get(MainItem.JSON_CODE).isJsonNull()) {
//                    if(object.get(MainItem.JSON_CODE).getAsString().equals("shas_wheel_formula"))
//                        Log.e("WTF","INFO_BLOCK_STORAGE code="+object.get(MainItem.JSON_CODE).getAsString());
                        item.setCode(object.get(MainItem.JSON_CODE).getAsString());
                    }

                    if (object.has(MainItem.JSON_VALUE) && !object.get(MainItem.JSON_VALUE).isJsonNull())
                        item.setValue(object.get(MainItem.JSON_VALUE).getAsString());

                    if (object.has(MainItem.JSON_CAN_ADD) && !object.get(MainItem.JSON_CAN_ADD).isJsonNull())
                        item.setCanAdd(object.get(MainItem.JSON_CAN_ADD).getAsBoolean());

                    if (object.has(MainItem.JSON_CAPITALIZE) && !object.get(MainItem.JSON_CAPITALIZE).isJsonNull())
                        item.setCapitalize(object.get(MainItem.JSON_CAPITALIZE).getAsBoolean());

                    if (object.has(MainItem.JSON_DEFAULT_VALUE) && !object.get(MainItem.JSON_DEFAULT_VALUE).isJsonNull())
                        item.setDefaultValue(object.get(MainItem.JSON_DEFAULT_VALUE).getAsString());

                    if (object.has(MainItem.JSON_LIST_VALUE) && !object.get(MainItem.JSON_LIST_VALUE).isJsonNull()) {
                        JsonObject valueObject = object.getAsJsonObject(MainItem.JSON_LIST_VALUE);
                        ListItem listItem = new ListItem(valueObject.get(ListItem.JSON_VALUE_ID).getAsLong(), valueObject.get(ListItem.JSON_VALUE_NAME).getAsString());

                        listItem.setMark(getInfoBlockIntItem(ListItem.JSON_VALUE_MARK,valueObject));
                        listItem.setEngineMark(getInfoBlockIntItem(ListItem.JSON_VALUE_ENGINE_MARK,valueObject));
                        listItem.setTireSchemeId(getInfoBlockIntItem(ListItem.JSON_TIRE_SCHEME_ID,valueObject));
                        listItem.setModel(getInfoBlockIntItem(ListItem.JSON_VALUE_MODEL,valueObject));
                        listItem.setEngineModel(getInfoBlockIntItem(ListItem.JSON_VALUE_ENGINE_MODEL,valueObject));
                        listItem.setKppModel(getInfoBlockIntItem(ListItem.JSON_VALUE_KPP_MODEL,valueObject));
                        listItem.setVehicleOwner(getInfoBlockIntItem(ListItem.JSON_VALUE_VEHICLE_OWNER,valueObject));
                        listItem.setKppMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KPP_MARK,valueObject));
                        listItem.setKmuMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KMU_MARK,valueObject));
                        listItem.setKhouMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KHOU_MARK,valueObject));
                        listItem.setSections(getInfoBlockIntItem(ListItem.JSON_VALUE_SECTIONS,valueObject));
                        listItem.setProtectorValues(getInfoBlockStringItem(ListItem.JSON_PROTECTOR_VALUES,valueObject));
                        listItem.setRevealOs(getInfoBlockIntItem(ListItem.JSON_REVEAL_OS,valueObject));

                        item.setListValue(listItem);

                    }
                    if (object.has(MainItem.JSON_LIST_VALUES)) {
                        JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_LIST_VALUES);
                        ArrayList<ListItem> listItems = new ArrayList<>();
                        for (int k = 0; k < valuesArray.size(); k++) {
                            JsonObject valueObject = valuesArray.get(k).getAsJsonObject();
                            ListItem listItem = new ListItem(valueObject.get(ListItem.JSON_VALUE_ID).getAsLong()
                                    , valueObject.get(ListItem.JSON_VALUE_NAME).getAsString());

                            listItem.setMark(getInfoBlockIntItem(ListItem.JSON_VALUE_MARK,valueObject));
                            listItem.setEngineMark(getInfoBlockIntItem(ListItem.JSON_VALUE_ENGINE_MARK,valueObject));
                            listItem.setTireSchemeId(getInfoBlockIntItem(ListItem.JSON_TIRE_SCHEME_ID,valueObject));
                            listItem.setModel(getInfoBlockIntItem(ListItem.JSON_VALUE_MODEL,valueObject));
                            listItem.setEngineModel(getInfoBlockIntItem(ListItem.JSON_VALUE_ENGINE_MODEL,valueObject));
                            listItem.setKppModel(getInfoBlockIntItem(ListItem.JSON_VALUE_KPP_MODEL,valueObject));
                            listItem.setVehicleOwner(getInfoBlockIntItem(ListItem.JSON_VALUE_VEHICLE_OWNER,valueObject));
                            listItem.setKppMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KPP_MARK,valueObject));
                            listItem.setKmuMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KMU_MARK,valueObject));
                            listItem.setKhouMark(getInfoBlockIntItem(ListItem.JSON_VALUE_KHOU_MARK,valueObject));
                            listItem.setSections(getInfoBlockIntItem(ListItem.JSON_VALUE_SECTIONS,valueObject));
                            listItem.setProtectorValues(getInfoBlockStringItem(ListItem.JSON_PROTECTOR_VALUES,valueObject));
                            listItem.setRevealOs(getInfoBlockIntItem(ListItem.JSON_REVEAL_OS,valueObject));

                            listItems.add(listItem);
                        }
                        item.setListValues(listItems);
                    }

                    if (object.has(MainItem.JSON_PROTECTOR_VALUES)) {
                        JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_PROTECTOR_VALUES);
                        ArrayList<ProtectorItem> protectorItems = new ArrayList<>();
                        for (int k = 0; k < valuesArray.size(); k++) {
                            JsonObject valueObject = valuesArray.get(k).getAsJsonObject();
                            ProtectorItem protectorItem = new ProtectorItem();

                            if (valueObject.has(ProtectorItem.JSON_GROUP_NAME) && !valueObject.get(ProtectorItem.JSON_GROUP_NAME).isJsonNull()) {
                                protectorItem.setGroupName(valueObject.get(ProtectorItem.JSON_GROUP_NAME).getAsString());
                            }

                            if (valueObject.has(ProtectorItem.JSON_CODE) && !valueObject.get(ProtectorItem.JSON_CODE).isJsonNull()) {
                                protectorItem.setCode(valueObject.get(ProtectorItem.JSON_CODE).getAsString());
                            }

                            if (valueObject.has(ProtectorItem.JSON_TITLE) && !valueObject.get(ProtectorItem.JSON_TITLE).isJsonNull()) {
                                protectorItem.setTitle(valueObject.get(ProtectorItem.JSON_TITLE).getAsString());
                            }

                            if (valueObject.has(ProtectorItem.JSON_TYPE) && !valueObject.get(ProtectorItem.JSON_TYPE).isJsonNull()) {
                                protectorItem.setType(valueObject.get(ProtectorItem.JSON_TYPE).getAsInt());
                            }

                            if (valueObject.has(ProtectorItem.JSON_VALUE) && !valueObject.get(ProtectorItem.JSON_VALUE).isJsonNull()) {
                                protectorItem.setValue(valueObject.get(ProtectorItem.JSON_VALUE).getAsString());
                            }


                            protectorItems.add(protectorItem);
                        }
                        item.setProtectorItems(protectorItems);
                    }


                    if (object.has(MainItem.JSON_PHOTO_VALUES)) {
                        JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
                        ArrayList<PhotoItem> photoItems = new ArrayList<>();
                        for (int k = 0; k < valuesArray.size(); k++) {
                            JsonObject valueObject = valuesArray.get(k).getAsJsonObject();
                            PhotoItem photoItem = new PhotoItem();

                            if (valueObject.has(PhotoItem.JSON_IS_DEFECT) && !valueObject.get(PhotoItem.JSON_IS_DEFECT).isJsonNull())
                                photoItem.setDefect(valueObject.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean());

                            if (valueObject.has(PhotoItem.JSON_SIZE) && !valueObject.get(PhotoItem.JSON_SIZE).isJsonNull())
                                photoItem.setSize(valueObject.get(PhotoItem.JSON_SIZE).getAsLong());

                            if (valueObject.has(PhotoItem.JSON_TITLE) && !valueObject.get(PhotoItem.JSON_TITLE).isJsonNull())
                                photoItem.setTitle(valueObject.get(PhotoItem.JSON_TITLE).getAsString());

                            if (valueObject.has(PhotoItem.JSON_DURATION) && !valueObject.get(PhotoItem.JSON_DURATION).isJsonNull())
                                photoItem.setDuration(valueObject.get(PhotoItem.JSON_DURATION).getAsInt());

                            if (valueObject.has(PhotoItem.JSON_IMAGE_PATH) && !valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull())
                                photoItem.setImagePath(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());

                            if (valueObject.has(PhotoItem.JSON_IMAGE_PATH) && !valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull())
                                photoItem.setImagePath(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());

                            if (valueObject.has(PhotoItem.JSON_IS_EDITED) && !valueObject.get(PhotoItem.JSON_IS_EDITED).isJsonNull())
                                photoItem.setEdited(valueObject.get(PhotoItem.JSON_IS_EDITED).getAsBoolean());

                            if (valueObject.has(PhotoItem.JSON_RE_PHOTO_COUNT) && !valueObject.get(PhotoItem.JSON_RE_PHOTO_COUNT).isJsonNull())
                                photoItem.setRePhotoCount(valueObject.get(PhotoItem.JSON_RE_PHOTO_COUNT).getAsInt());

                            if (valueObject.has(PhotoItem.JSON_ID) && !valueObject.get(PhotoItem.JSON_ID).isJsonNull()) {
                                photoItem.setId(valueObject.get(PhotoItem.JSON_ID).getAsLong());
                            }

                            if (valueObject.has(PhotoItem.JSON_CODE) && !valueObject.get(PhotoItem.JSON_CODE).isJsonNull())
                                photoItem.setCode(valueObject.get(PhotoItem.JSON_CODE).getAsString());

                            if (valueObject.has(PhotoItem.JSON_IS_SEND) && !valueObject.get(PhotoItem.JSON_IS_SEND).isJsonNull())
                                photoItem.setSend(valueObject.get(PhotoItem.JSON_IS_SEND).getAsBoolean());

                            if (valueObject.has(PhotoItem.JSON_IS_REQUIRED) && !valueObject.get(PhotoItem.JSON_IS_REQUIRED).isJsonNull())
                                photoItem.setRequired(valueObject.get(PhotoItem.JSON_IS_REQUIRED).getAsBoolean());

                            if (valueObject.has(PhotoItem.JSON_IS_VIDEO) && !valueObject.get(PhotoItem.JSON_IS_VIDEO).isJsonNull())
                                photoItem.setVideo(valueObject.get(PhotoItem.JSON_IS_VIDEO).getAsBoolean());

                            if (valueObject.has(PhotoItem.JSON_IS_OS) && !valueObject.get(PhotoItem.JSON_IS_OS).isJsonNull())
                                photoItem.setIsOs(valueObject.get(PhotoItem.JSON_IS_OS).getAsInt());

                            photoItems.add(photoItem);

                        }
                        item.setPhotoItems(photoItems);
                    }
                    mainItems.add(item);
                }
                arrayOfItems.add(mainItems);
            } catch (Exception ignored) {
            }

        }


        return arrayOfItems;
    }


    public void cancelSaving() {
        shouldStopSaving = true;
    }
}


