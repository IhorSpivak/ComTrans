package ru.comtrans.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropHelper {
    private static PropHelper instance;
    Gson gson;
    JsonArray prop;
    JsonArray screens;


    public static PropHelper getInstance() {
        if(instance==null){
            instance = new PropHelper();
        }
        return instance;
    }

    public JsonArray getScreens() {
        return screens;
    }

    private PropHelper(){
        gson = new Gson();
        screens = new JsonArray();
        parseJson();
    }

    public void parseJson(){
        prop = gson.fromJson(Utility.getSavedData(Const.JSON_PROP), JsonArray.class);
        for(int i=0; i<prop.size();i++){
            JsonArray screen = new JsonArray();
            JsonObject object = prop.get(i).getAsJsonObject();
            JsonArray val = object.get("val").getAsJsonArray();
            for (int j = 0; j < val.size(); j++) {
                JsonObject valObject = val.get(j).getAsJsonObject();
                for(Map.Entry<String,JsonElement> entry:valObject.entrySet()){
                    if(entry.getKey().startsWith("photo")){
                    screen.addAll(getPhotoItems(entry.getValue().getAsJsonArray(),false));

                }else if(entry.getKey().startsWith("video")){
                        screen.addAll(getPhotoItems(entry.getValue().getAsJsonArray(),true));
                }else if(entry.getKey().startsWith("protector")){
                        JsonObject image = new JsonObject();
                        image.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_TIRE_SCHEME);
                        screen.add(image);
                        screen.addAll(getItems(entry.getValue().getAsJsonArray()));
                }else {
                        screen.addAll(getItems(entry.getValue().getAsJsonArray()));
                    }
                }
            }

            JsonObject bottomBar = new JsonObject();
            bottomBar.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_BOTTOM_BAR);
            screen.add(bottomBar);

            screens.add(screen);
        }

    }

    private static JsonArray getItems(JsonArray array){
        JsonArray newArray = new JsonArray();
        for (int i = 0; i < array.size(); i++) {

            JsonObject object = array.get(i).getAsJsonObject();


            if(i==0){
                JsonObject newObject = new JsonObject();
                newObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_HEADER);
                newObject.addProperty(MainItem.JSON_NAME,object.get("group").getAsString());
                newArray.add(newObject);
            }

            JsonObject newObject = new JsonObject();



            switch (object.get("prop_type").getAsString()){
                case "S":
                    newObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_STRING);
                    break;
                case "N":
                    newObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_NUMBER);
                    break;
                case "L":
                    newObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_FLAG);
                    break;
                case "DR":
                    newObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_LIST);
                    break;

            }
            newObject.addProperty(MainItem.JSON_NAME,object.get("name").getAsString());
            newObject.addProperty(MainItem.JSON_CODE,object.get("code").getAsString());


            if(object.has("val")&&!object.get("val").isJsonNull()){
                JsonArray newVal = new JsonArray();
                JsonArray val = object.get("val").getAsJsonArray();
                for (int j = 0; j < val.size(); j++) {

                    JsonObject valueObject = val.get(j).getAsJsonObject();
                    JsonObject newValueObject = new JsonObject();
                    newValueObject.addProperty(ListItem.JSON_VALUE_ID,valueObject.get("id").getAsLong());
                    newValueObject.addProperty(ListItem.JSON_VALUE_NAME,valueObject.get("name").getAsString());
                    if(valueObject.has("mark")){
                        newValueObject.addProperty(ListItem.JSON_VALUE_MARK,valueObject.get("mark").getAsInt());
                    }
                    newVal.add(newValueObject);

                    if(j==0){
                        newObject.add(MainItem.JSON_LIST_VALUE,newValueObject);
                    }
                }
                newObject.add(MainItem.JSON_LIST_VALUES,newVal);


            }
        newArray.add(newObject);
        }

        return newArray;
    }

    private static JsonArray getPhotoItems(JsonArray array, boolean isVideo){
        JsonArray newArray = new JsonArray();

        JsonObject header = new JsonObject();
        header.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_HEADER);
        header.addProperty(MainItem.JSON_NAME,array.get(0).getAsJsonObject().get("group").getAsString());
        newArray.add(header);

        JsonArray photoArray = new JsonArray();
        for (int i = 0; i < array.size(); i++) {

            JsonObject object = array.get(i).getAsJsonObject();
            JsonObject newObject = new JsonObject();
            newObject.addProperty(PhotoItem.JSON_TITLE,object.get("name").getAsString());

            if(i == array.size()-1){
                if(object.has("is_defect")&&!object.get("is_defect").isJsonNull()&&object.get("is_defect").getAsBoolean()){
                    newObject.addProperty(PhotoItem.JSON_IS_DEFECT,true);
                    Utility.saveData(Const.DEFAULT_DEFECT_NAME,object.get("name").getAsString());
                    newObject.addProperty(PhotoItem.JSON_TITLE,object.get("name").getAsString()+" 1");
                }else {
                    newObject.addProperty(PhotoItem.JSON_IS_DEFECT,false);
                }


            }else {
                newObject.addProperty(PhotoItem.JSON_IS_DEFECT,false);
            }
            if(isVideo){
                newObject.addProperty(PhotoItem.JSON_DURATION,15);
            }
            photoArray.add(newObject);



        }
        JsonObject photoObject = new JsonObject();

        if(isVideo){
            photoObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_VIDEO);
        }else {
            photoObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_PHOTO);
        }

        photoObject.add(MainItem.JSON_PHOTO_VALUES,photoArray);
        newArray.add(photoObject);


        return newArray;
    }



}
