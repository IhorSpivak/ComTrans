package ru.comtrans.singlets;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropHelper {
    private static PropHelper instance;
    private Gson gson;
    private JsonArray prop;
    private JsonArray screens;


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
                        screen.addAll(getProtectorItems(entry.getValue().getAsJsonArray()));
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
            try {


                JsonObject object = array.get(i).getAsJsonObject();


                if (i == 0) {
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_HEADER);
                    newObject.addProperty(MainItem.JSON_NAME, object.get("group").getAsString());
                    newArray.add(newObject);
                }

                JsonObject newObject = new JsonObject();


                switch (object.get("prop_type").getAsString()) {
                    case "S":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_STRING);
                        break;
                    case "N":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_NUMBER);
                        break;
                    case "L":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_FLAG);
                        break;
                    case "DR":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_LIST);
                        break;
                    case "CAL":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_CALENDAR);
                        break;
                    case "EMAIL":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_EMAIL);
                        break;
                    case "PHONE":
                        newObject.addProperty(MainItem.JSON_TYPE, MainItem.TYPE_PHONE);
                        break;

                }
                newObject.addProperty(MainItem.JSON_NAME, object.get("name").getAsString());
                newObject.addProperty(MainItem.JSON_CODE, object.get("code").getAsString());
//                if(object.get("code").getAsString().equals("shas_wheel_formula"))
//                    Log.e("WTF","PROP_HELPER code="+object.get("code").getAsString());


                if (object.has("val") && !object.get("val").isJsonNull()) {
                    JsonArray newVal = new JsonArray();
                    JsonArray val = object.get("val").getAsJsonArray();
                    for (int j = 0; j < val.size(); j++) {

                        JsonObject valueObject = val.get(j).getAsJsonObject();
                        JsonObject newValueObject = new JsonObject();
                        newValueObject.addProperty(ListItem.JSON_VALUE_ID, valueObject.get("id").getAsLong());
                        newValueObject.addProperty(ListItem.JSON_VALUE_NAME, valueObject.get("name").getAsString());
                        if (valueObject.has("mark")&&!valueObject.get("mark").isJsonNull()) {
                            newValueObject.addProperty(ListItem.JSON_VALUE_MARK, valueObject.get("mark").getAsInt());
                        }

                        if (valueObject.has("axis_code")&&!valueObject.get("axis_code").isJsonNull()) {
                            newValueObject.addProperty(ListItem.JSON_TIRE_SCHEME_ID, valueObject.get("axis_code").getAsInt());
                        }

                        if (valueObject.has("axis")&&!valueObject.get("axis").isJsonNull()) {
                            newValueObject.add(ListItem.JSON_PROTECTOR_VALUES, valueObject.get("axis").getAsJsonArray());
                        }
                        newVal.add(newValueObject);

                        if (j == 0) {
                            newObject.add(MainItem.JSON_LIST_VALUE, newValueObject);
                        }
                    }
                    newObject.add(MainItem.JSON_LIST_VALUES, newVal);


                }
                newArray.add(newObject);
            }catch (Exception ignored){}
        }

        return newArray;
    }

    private static JsonArray getProtectorItems(JsonArray array){
        JsonArray newArray = new JsonArray();
        JsonArray protectorArray = new JsonArray();

        JsonObject image = new JsonObject();
        image.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_TIRE_SCHEME);

        newArray.add(image);

        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            String name = object.get("name").getAsString();


//            if(i==0){
//                JsonObject headerObject = new JsonObject();
//                headerObject.addProperty(ProtectorItem.JSON_TYPE,ProtectorItem.TYPE_HEADER);
//                headerObject.addProperty(ProtectorItem.JSON_TITLE,object.get("group").getAsString());
//                protectorArray.add(headerObject);
//            }



            JsonObject newObject = new JsonObject();

            newObject.addProperty(ProtectorItem.JSON_TITLE,name);
            newObject.addProperty(ProtectorItem.JSON_CODE,object.get("code").getAsString());
            newObject.addProperty(ProtectorItem.JSON_GROUP_NAME,object.get("group").getAsString());
            newObject.addProperty(ProtectorItem.JSON_TYPE,ProtectorItem.TYPE_PROTECTOR);
            protectorArray.add(newObject);
        }

        JsonObject protectorObject = new JsonObject();

        protectorObject.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_PROTECTOR);
        protectorObject.add(MainItem.JSON_PROTECTOR_VALUES,protectorArray);

        newArray.add(protectorObject);

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
            newObject.addProperty(PhotoItem.JSON_CODE,object.get("code").getAsString());
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
                if(object.has("duration")&&!object.get("duration").isJsonNull()) {
                    newObject.addProperty(PhotoItem.JSON_DURATION, object.get("duration").getAsInt());
                }
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
