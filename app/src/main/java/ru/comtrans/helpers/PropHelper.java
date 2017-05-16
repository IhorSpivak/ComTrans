package ru.comtrans.helpers;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;

/**
 * Created by Artco on 28.11.2016.
 */

public class PropHelper {

    private long propCode;
    private long inspectionCode;
    private Gson gson;
    private JsonArray screens;
    private JsonArray dataArray;

    public PropHelper(long propCode){
        this.propCode = propCode;
        gson = new Gson();
        screens = new JsonArray();
        parseJson();
    }

    public PropHelper(JsonArray result, long propCode, long inspectionCode){
        this.propCode = propCode;
        this.inspectionCode = inspectionCode;
        gson = new Gson();
        screens = new JsonArray();
        dataArray = result;
        parseJson();
    }

    public JsonArray getScreens() {
        return screens;
    }

    private void parseJson() {

        if (dataArray == null){
            String jsonString = Utility.getSavedData(Const.JSON_PROP_CODE + propCode);
            dataArray = gson.fromJson(jsonString, JsonArray.class);
        }

        for(int i = 0; i< dataArray.size(); i++){

            JsonArray screen = new JsonArray();
            JsonObject object = dataArray.get(i).getAsJsonObject();
            JsonArray val = object.get("val").getAsJsonArray();
            for (int j = 0; j < val.size(); j++) {
                JsonObject valObject = val.get(j).getAsJsonObject();
                for(Map.Entry<String,JsonElement> entry:valObject.entrySet()){

                    if(!entry.getValue().getAsJsonArray().isJsonNull()){
                        JsonArray array = entry.getValue().getAsJsonArray();
                        if(array.size()>0){
                            JsonObject jsonObject = array.get(0).getAsJsonObject();
                            if(jsonObject.has("prop_type")){
                                String propType = jsonObject.get("prop_type").getAsString();
                                switch (propType){
                                    case "F":
                                        screen.addAll(getPhotoItems(array));
                                        break;
                                    case "T":
                                        screen.addAll(getProtectorItems(array));
                                        break;
                                    default:
                                        screen.addAll(getItems(array,propCode,inspectionCode));
                                        break;
                                }

                            }
                        }
                    }
//                    if(entry.getKey().startsWith("photo")){
//                        screen.addAll(getPhotoItems(entry.getValue().getAsJsonArray(),false));
//                    }else if(entry.getKey().startsWith("video")){
//                        screen.addAll(getPhotoItems(entry.getValue().getAsJsonArray(),true));
                    //   }else
//                    if(entry.getKey().startsWith("tyres_scheme")) {
//                        screen.addAll(getProtectorItems(entry.getValue().getAsJsonArray()));
//                    }
//                    }else {
//                        screen.addAll(getItems(entry.getValue().getAsJsonArray(),propCode,inspectionCode));
//                    }
                }
            }

            JsonObject bottomBar = new JsonObject();
            bottomBar.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_BOTTOM_BAR);
            screen.add(bottomBar);

            screens.add(screen);
        }
    }

    private static JsonArray getItems(JsonArray array, long propCode, long inspectionCode){
        JsonArray newArray = new JsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            if (object.has("prop_type") && !object.get("prop_type").isJsonNull()) {


                if (i == 0 && object.has("group") && !object.get("group").isJsonNull()) {
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
                    default:
                        break;

                }
                if(object.has("name")&&!object.get("name").isJsonNull())
                    newObject.addProperty(MainItem.JSON_NAME, object.get("name").getAsString());

                if(object.has("name")&&!object.get("name").isJsonNull())
                    newObject.addProperty(MainItem.JSON_CODE, object.get("code").getAsString());


                if (object.has("is_required") && !object.get("is_required").isJsonNull()) {
                    switch (object.get("is_required").getAsString()) {
                        case "Y":
                            newObject.addProperty(MainItem.JSON_IS_REQUIRED, true);
                            break;
                        case "N":
                            newObject.addProperty(MainItem.JSON_IS_REQUIRED, false);
                            break;
                    }
                }

                if (object.has("default_value") && !object.get("default_value").isJsonNull()) {
                    newObject.addProperty(MainItem.JSON_DEFAULT_VALUE, object.get("default_value").getAsString());
                }

                if (object.has("can_add") && !object.get("can_add").isJsonNull()) {
                    newObject.addProperty(MainItem.JSON_CAN_ADD, object.get("can_add").getAsBoolean());
                }

                if (object.has("capitalize") && !object.get("capitalize").isJsonNull()) {
                    newObject.addProperty(MainItem.JSON_CAPITALIZE, object.get("capitalize").getAsBoolean());
                }


                if (object.has("val") && !object.get("val").isJsonNull()) {

                    JsonArray newVal = new JsonArray();
                    JsonArray val = object.get("val").getAsJsonArray();

                    for (int j = 0; j < val.size(); j++) {

                        JsonObject valueObject = val.get(j).getAsJsonObject();
                        JsonObject newValueObject = new JsonObject();
                        if (valueObject.has("id") && !valueObject.get("id").isJsonNull()) {
                            newValueObject.addProperty(ListItem.JSON_VALUE_ID, valueObject.get("id").getAsLong());
                        }
                        if (valueObject.has("name") && !valueObject.get("name").isJsonNull()) {
                            newValueObject.addProperty(ListItem.JSON_VALUE_NAME, valueObject.get("name").getAsString());
                        }
                        if (valueObject.has("mark") && !valueObject.get("mark").isJsonNull()) {
                            try {
                                newValueObject.addProperty(ListItem.JSON_VALUE_MARK, valueObject.get("mark").getAsInt());
                            }catch (Exception ignored){}
                        }
                        if (valueObject.has("engine_mark") && !valueObject.get("engine_mark").isJsonNull()) {
                            try {
                                newValueObject.addProperty(ListItem.JSON_VALUE_ENGINE_MARK, valueObject.get("engine_mark").getAsInt());
                            }catch (Exception ignored){}
                        }
                        if (valueObject.has("model") && !valueObject.get("model").isJsonNull() && valueObject.get("model").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_MODEL, valueObject.get("model").getAsJsonArray());
                        }
                        if (valueObject.has("engine_model") && !valueObject.get("engine_model").isJsonNull() && valueObject.get("engine_model").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_ENGINE_MODEL, valueObject.get("engine_model").getAsJsonArray());
                        }

                        if (valueObject.has("kpp_model") && !valueObject.get("kpp_model").isJsonNull() && valueObject.get("kpp_model").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_KPP_MODEL, valueObject.get("kpp_model").getAsJsonArray());
                        }

                        if (valueObject.has("kpp_mark") && !valueObject.get("kpp_mark").isJsonNull() && valueObject.get("kpp_mark").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_KPP_MARK, valueObject.get("kpp_mark").getAsJsonArray());
                        }

                        if (valueObject.has("kmu_mark") && !valueObject.get("kmu_mark").isJsonNull() && valueObject.get("kmu_mark").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_KMU_MARK, valueObject.get("kmu_mark").getAsJsonArray());
                        }

                        if (valueObject.has("khou_mark") && !valueObject.get("khou_mark").isJsonNull() && valueObject.get("khou_mark").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_KHOU_MARK, valueObject.get("khou_mark").getAsJsonArray());
                        }

                        if (valueObject.has("sections") && !valueObject.get("sections").isJsonNull() && valueObject.get("sections").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_SECTIONS, valueObject.get("sections").getAsJsonArray());
                        }

                        if (valueObject.has("vehicle_owner") && !valueObject.get("vehicle_owner").isJsonNull() && valueObject.get("vehicle_owner").isJsonArray()) {
                            newValueObject.add(ListItem.JSON_VALUE_VEHICLE_OWNER, valueObject.get("vehicle_owner").getAsJsonArray());
                        }

                        if (valueObject.has("axis_code") && !valueObject.get("axis_code").isJsonNull() && !valueObject.get("axis_code").getAsString().isEmpty()) {
                            newValueObject.addProperty(ListItem.JSON_TIRE_SCHEME_ID, valueObject.get("axis_code").getAsInt());
                        }

                        if (valueObject.has("axis") && !valueObject.get("axis").isJsonNull()) {
                            newValueObject.add(ListItem.JSON_PROTECTOR_VALUES, valueObject.get("axis").getAsJsonArray());
                        }

                        if (valueObject.has("reveal_os") && !valueObject.get("reveal_os").isJsonNull()) {
                            newValueObject.add(ListItem.JSON_REVEAL_OS, valueObject.get("reveal_os").getAsJsonArray());
                        }

                        newVal.add(newValueObject);

                        if (object.get("code").getAsString().equals("general_type_id")) {
                            if (newValueObject.get(ListItem.JSON_VALUE_ID).getAsLong() == propCode) {
                                newObject.add(MainItem.JSON_LIST_VALUE, newValueObject);
                                newObject.addProperty(MainItem.IS_NEVER_MODIFIED, true);
                            }
                        } else {
                            if (j == 0) {
                                newObject.add(MainItem.JSON_LIST_VALUE, newValueObject);
                            }
                        }

                        if (object.get("code").getAsString().equals("view_type_list")) {
                            if (newValueObject.get(ListItem.JSON_VALUE_ID).getAsLong() == inspectionCode) {
                                newObject.add(MainItem.JSON_LIST_VALUE, newValueObject);
                                newObject.addProperty(MainItem.IS_NEVER_MODIFIED, true);
                            }
                        } else {
                            if (j == 0) {
                                newObject.add(MainItem.JSON_LIST_VALUE, newValueObject);
                            }
                        }

                    }
                    newObject.add(MainItem.JSON_LIST_VALUES, newVal);


                }
                if(newObject.has(MainItem.JSON_TYPE))
                    newArray.add(newObject);
                //       }catch (Exception ignored){}
            }
        }

        return newArray;
    }

    private static JsonArray getProtectorItems(JsonArray array){
        JsonArray newArray = new JsonArray();
        JsonArray protectorArray = new JsonArray();

        JsonObject image = new JsonObject();
        image.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_TIRE_SCHEME);



        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            String name = object.get("name").getAsString();


            JsonObject newObject = new JsonObject();

            newObject.addProperty(ProtectorItem.JSON_TITLE,name);
            newObject.addProperty(ProtectorItem.JSON_CODE,object.get("code").getAsString());
            newObject.addProperty(ProtectorItem.JSON_GROUP_NAME,object.get("group").getAsString());
            newObject.addProperty(ProtectorItem.JSON_TYPE,ProtectorItem.TYPE_PROTECTOR);
            protectorArray.add(newObject);
        }


        image.add(MainItem.JSON_PROTECTOR_VALUES,protectorArray);

        newArray.add(image);

        return newArray;
    }

    private static JsonArray getPhotoItems(JsonArray array){
        boolean isVideo = false;
        JsonArray newArray = new JsonArray();

        JsonObject header = new JsonObject();
        header.addProperty(MainItem.JSON_TYPE,MainItem.TYPE_HEADER);
        header.addProperty(MainItem.JSON_NAME,array.get(0).getAsJsonObject().get("group").getAsString());
        newArray.add(header);

        JsonArray photoArray = new JsonArray();
        for (int i = 0; i < array.size(); i++) {

            JsonObject object = array.get(i).getAsJsonObject();
            JsonObject newObject = new JsonObject();
            if(object!=null&&object.has("code")&&!object.get("code").isJsonNull()
                    &&object.has("prop_type")&&!object.get("prop_type").isJsonNull()&&
                    object.get("prop_type").getAsString().equals("F")){

                if(object.get("code").getAsString().startsWith("video")){
                    isVideo = true;
                }


                newObject.addProperty(PhotoItem.JSON_TITLE, object.get("name").getAsString());
                newObject.addProperty(PhotoItem.JSON_CODE, object.get("code").getAsString());

                if (object.has("is_os") && !object.get("is_os").isJsonNull()) {
                    newObject.addProperty(PhotoItem.JSON_IS_OS, object.get("is_os").getAsInt());
                }
                if (i == array.size() - 1) {
                    if (object.has("is_defect") && !object.get("is_defect").isJsonNull() && object.get("is_defect").getAsBoolean()) {
                        newObject.addProperty(PhotoItem.JSON_IS_DEFECT, true);
                        Utility.saveData(Const.DEFAULT_DEFECT_NAME, object.get("name").getAsString());
                        //   newObject.addProperty(PhotoItem.JSON_TITLE,object.get("name").getAsString()+" 1");
                        newObject.addProperty(PhotoItem.JSON_TITLE, "Дефект 1");
                    } else {
                        newObject.addProperty(PhotoItem.JSON_IS_DEFECT, false);
                    }


                } else {
                    newObject.addProperty(PhotoItem.JSON_IS_DEFECT, false);
                }
                if (isVideo) {
                    if (object.has("duration") && !object.get("duration").isJsonNull()&&!object.get("duration").getAsString().equals("")) {
                        newObject.addProperty(PhotoItem.JSON_DURATION, object.get("duration").getAsInt());
                    }else {
                        newObject.addProperty(PhotoItem.JSON_DURATION,30);
                    }
                }
                photoArray.add(newObject);


            }

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
