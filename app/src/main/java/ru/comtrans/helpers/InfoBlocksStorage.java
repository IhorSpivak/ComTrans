package ru.comtrans.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlocksStorage {
    private Set<String> infoBlockIds;
    private static final String INFO_BLOCK_IDS = "ids";

    private static InfoBlocksStorage instance;
    public static InfoBlocksStorage getInstance() {
        if(instance==null)
            instance = new InfoBlocksStorage();
        return instance;
    }

    private InfoBlocksStorage(){
        infoBlockIds = Utility.getStringSet(INFO_BLOCK_IDS);
        if(infoBlockIds==null)
            infoBlockIds = new HashSet<>();
    }

    public String saveInfoBlock(JsonArray block,String id){
        if(id==null){
            id = UUID.randomUUID().toString();
            infoBlockIds.add(id);
            Utility.saveStringSet(INFO_BLOCK_IDS,infoBlockIds);
        }
        Utility.saveData(id,block.toString());
       // Utility.saveData(id+"preview");
        return id;
    }

    public String saveInfoBlock(ArrayList<ArrayList<MainItem>> block, String id){
        if(id==null){
            id = UUID.randomUUID().toString();
            infoBlockIds.add(id);
            Utility.saveStringSet(INFO_BLOCK_IDS,infoBlockIds);
        }

        JsonArray array = new JsonArray();

        for (int i = 0; i < block.size(); i++) {
            JsonArray screenArray = new JsonArray();
            for (int j = 0; j < block.get(i).size(); j++) {
                JsonObject object = new JsonObject();
                MainItem item = block.get(i).get(j);

                object.addProperty(MainItem.JSON_CODE,item.getCode());
                object.addProperty(MainItem.JSON_ID,item.getId());
                object.addProperty(MainItem.JSON_NAME,item.getName());
                object.addProperty(MainItem.JSON_VALUE,item.getValue());
                object.addProperty(MainItem.JSON_TYPE,item.getType());
                object.addProperty(MainItem.JSON_IS_CHECKED,item.isChecked());


                if(item.getListValue()!=null){
                    JsonObject listObject = new JsonObject();
                    ListItem listItem = item.getListValue();
                    listObject.addProperty(ListItem.JSON_VALUE_ID,listItem.getId());
                    listObject.addProperty(ListItem.JSON_VALUE_NAME,listItem.getName());
                    object.add(MainItem.JSON_LIST_VALUE,listObject);
                }

                if(item.getListValues()!=null&&item.getListValues().size()>0){
                    JsonArray listArray = new JsonArray();
                    for (int k = 0; k < item.getListValues().size(); k++) {
                        JsonObject listObject = new JsonObject();
                        ListItem listItem = item.getListValues().get(k);
                        listObject.addProperty(ListItem.JSON_VALUE_ID,listItem.getId());
                        listObject.addProperty(ListItem.JSON_VALUE_NAME,listItem.getName());
                        listArray.add(listObject);
                    }
                    object.add(MainItem.JSON_LIST_VALUES,listArray);
                }

                if(item.getPhotoItems()!=null&&item.getPhotoItems().size()>0){
                    JsonArray photoArray = new JsonArray();
                    for (int k = 0; k < item.getPhotoItems().size(); k++) {
                        JsonObject photoObject = new JsonObject();
                        PhotoItem photoItem = item.getPhotoItems().get(k);
                        photoObject.addProperty(PhotoItem.JSON_DURATION,photoItem.getDuration());
                        photoObject.addProperty(PhotoItem.JSON_ID,photoItem.getId());
                        photoObject.addProperty(PhotoItem.JSON_IMAGE_PATH,photoItem.getImagePath());
                        photoObject.addProperty(PhotoItem.JSON_IS_DEFECT,photoItem.isDefect());
                        photoObject.addProperty(PhotoItem.JSON_IS_VIDEO,photoItem.isVideo());
                        photoObject.addProperty(PhotoItem.JSON_TITLE,photoItem.getTitle());
                        photoArray.add(photoObject);
                    }
                    object.add(MainItem.JSON_PHOTO_VALUES,photoArray);
                }
                screenArray.add(object);
            }
          array.add(screenArray);
        }
        Utility.saveData(id,array.toString());

        return id;
    }

    public ArrayList<ArrayList<MainItem>> getInfoBlock(String id){
        Gson gson = new Gson();
        JsonArray array = gson.fromJson(Utility.getSavedData(id),JsonArray.class);
        ArrayList<ArrayList<MainItem>> arrayOfItems = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonArray screenArray = array.get(i).getAsJsonArray();
            ArrayList<MainItem> mainItems = new ArrayList<>();
            for (int j = 0; j < screenArray.size(); j++) {
                JsonObject object = screenArray.get(j).getAsJsonObject();
                MainItem item = new MainItem(object.get(MainItem.JSON_TYPE).getAsInt());

                if(object.has(MainItem.JSON_NAME)&&!object.get(MainItem.JSON_NAME).isJsonNull())
                item.setName(object.get(MainItem.JSON_NAME).getAsString());

                if(object.has(MainItem.JSON_IS_CHECKED)&&!object.get(MainItem.JSON_IS_CHECKED).isJsonNull())
                item.setChecked(object.get(MainItem.JSON_IS_CHECKED).getAsBoolean());

                if(object.has(MainItem.JSON_ID)&&!object.get(MainItem.JSON_ID).isJsonNull())
                item.setId(object.get(MainItem.JSON_ID).getAsLong());

                if(object.has(MainItem.JSON_CODE)&&!object.get(MainItem.JSON_CODE).isJsonNull())
                item.setCode(object.get(MainItem.JSON_CODE).getAsString());

                if(object.has(MainItem.JSON_VALUE)&&!object.get(MainItem.JSON_VALUE).isJsonNull())
                item.setValue(object.get(MainItem.JSON_VALUE).getAsString());

                if(object.has(MainItem.JSON_LIST_VALUE)&&!object.get(MainItem.JSON_LIST_VALUE).isJsonNull()){
                    JsonObject listValueObject = object.getAsJsonObject(MainItem.JSON_LIST_VALUE);
                    item.setListValue(new ListItem(listValueObject.get(ListItem.JSON_VALUE_ID).getAsLong(),listValueObject.get(ListItem.JSON_VALUE_NAME).getAsString()));
                }
                if(object.has(MainItem.JSON_LIST_VALUES)){
                    JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_LIST_VALUES);
                    ArrayList<ListItem> listItems = new ArrayList<>();
                    for (int k = 0; k < valuesArray.size(); k++) {
                        JsonObject valueObject = valuesArray.get(k).getAsJsonObject();
                        ListItem listItem = new ListItem(valueObject.get(ListItem.JSON_VALUE_ID).getAsLong(),valueObject.get(ListItem.JSON_VALUE_NAME).getAsString());

                        if(valueObject.has(ListItem.JSON_VALUE_MARK)){
                            listItem.setMark(valueObject.get(ListItem.JSON_VALUE_MARK).getAsInt());
                        }

                        listItems.add(listItem);
                    }
                    item.setListValues(listItems);
                }


                if(object.has(MainItem.JSON_PHOTO_VALUES)){
                    JsonArray valuesArray = object.getAsJsonArray(MainItem.JSON_PHOTO_VALUES);
                    ArrayList<PhotoItem> photoItems = new ArrayList<>();
                    for (int k = 0; k < valuesArray.size(); k++) {
                        JsonObject valueObject = valuesArray.get(k).getAsJsonObject();
                        PhotoItem photoItem = new PhotoItem();

                        if(valueObject.has(PhotoItem.JSON_IS_DEFECT)&&!valueObject.get(PhotoItem.JSON_IS_DEFECT).isJsonNull())
                            photoItem.setDefect(valueObject.get(PhotoItem.JSON_IS_DEFECT).getAsBoolean());

                        if(valueObject.has(PhotoItem.JSON_TITLE)&&!valueObject.get(PhotoItem.JSON_TITLE).isJsonNull())
                            photoItem.setTitle(valueObject.get(PhotoItem.JSON_TITLE).getAsString());

                        if(valueObject.has(PhotoItem.JSON_DURATION)&&!valueObject.get(PhotoItem.JSON_DURATION).isJsonNull())
                        photoItem.setDuration(valueObject.get(PhotoItem.JSON_DURATION).getAsInt());

                        if(valueObject.has(PhotoItem.JSON_IMAGE_PATH)&&!valueObject.get(PhotoItem.JSON_IMAGE_PATH).isJsonNull())
                        photoItem.setImagePath(valueObject.get(PhotoItem.JSON_IMAGE_PATH).getAsString());

                        if(valueObject.has(PhotoItem.JSON_ID)&&!valueObject.get(PhotoItem.JSON_ID).isJsonNull())
                        photoItem.setId(valueObject.get(PhotoItem.JSON_ID).getAsString());
                    photoItems.add(photoItem);

                    }
                    item.setPhotoItems(photoItems);
                }
                mainItems.add(item);
            }
            arrayOfItems.add(mainItems);

        }


        return arrayOfItems;
    }


}


