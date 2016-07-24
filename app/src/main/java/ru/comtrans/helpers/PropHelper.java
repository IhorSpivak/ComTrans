package ru.comtrans.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropHelper {
    private static PropHelper instance;
    private JsonArray general;
    private JsonArray teh;
    private JsonArray shin;
    private JsonArray doc;
    private JsonArray client;
    private JsonArray photogeneral;
    private JsonArray photocabine;
    private JsonArray photoglass;
    private JsonArray photoshas;
    private JsonArray photoengine;
    private JsonArray phototrans;
    private JsonArray photochassis;
    private JsonArray photobus;
    private JsonArray video;
    private JsonArray photodop;
    private JsonArray tec;
    private JsonArray cabn;
    private JsonArray shas;
    private JsonArray fuel;
    private JsonArray _break;
    private JsonArray optional;
    Gson gson;

    public static PropHelper getInstance() {
        if(instance==null){
            instance = new PropHelper();
        }
        return instance;
    }

    private PropHelper(){
        gson = new Gson();
        parseJson();
    }

    public void parseJson(){

        JsonObject prop = gson.fromJson(Utility.getSavedData(Const.JSON_PROP), JsonObject.class);
        general = gson.fromJson(prop.getAsJsonArray("general"),JsonArray.class);
        teh = gson.fromJson(prop.getAsJsonArray("teh"),JsonArray.class);
        shin = gson.fromJson(prop.getAsJsonArray("shin"),JsonArray.class);
        doc = gson.fromJson(prop.getAsJsonArray("doc"),JsonArray.class);
        client = gson.fromJson(prop.getAsJsonArray("client"),JsonArray.class);
        photogeneral = gson.fromJson(prop.getAsJsonArray("photogeneral"),JsonArray.class);
        photocabine = gson.fromJson(prop.getAsJsonArray("photocabine"),JsonArray.class);
        photoglass = gson.fromJson(prop.getAsJsonArray("photoglass"),JsonArray.class);
        photoshas = gson.fromJson(prop.getAsJsonArray("photoshas"),JsonArray.class);
        photoengine = gson.fromJson(prop.getAsJsonArray("photoengine"),JsonArray.class);
        phototrans = gson.fromJson(prop.getAsJsonArray("phototrans"),JsonArray.class);
        photochassis = gson.fromJson(prop.getAsJsonArray("photochassis"),JsonArray.class);
        photobus = gson.fromJson(prop.getAsJsonArray("photobus"),JsonArray.class);
        video = gson.fromJson(prop.getAsJsonArray("video"),JsonArray.class);
        photodop = gson.fromJson(prop.getAsJsonArray("photodop"),JsonArray.class);
        tec = gson.fromJson(prop.getAsJsonArray("tec"),JsonArray.class);
        cabn = gson.fromJson(prop.getAsJsonArray("cabn"),JsonArray.class);
        shas = gson.fromJson(prop.getAsJsonArray("shas"),JsonArray.class);
        fuel = gson.fromJson(prop.getAsJsonArray("fuel"),JsonArray.class);
        _break = gson.fromJson(prop.getAsJsonArray("break"),JsonArray.class);
        optional = gson.fromJson(prop.getAsJsonArray("optional"),JsonArray.class);

    }

    private static ArrayList<MainItem> getItems(JsonArray array){
        ArrayList<MainItem> items = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {

            JsonObject object = array.get(i).getAsJsonObject();

            if(i==0){
                MainItem item = new MainItem();
                item.setType(MainItem.TYPE_HEADER);
                item.setName(object.get("group").getAsString());
                items.add(item);
            }


            MainItem item = new MainItem();

            switch (object.get("prop_type").getAsString()){
                case "S":
                    item.setType(MainItem.TYPE_STRING);
                    break;
                case "N":
                    item.setType(MainItem.TYPE_NUMBER);
                    break;
                case "L":
                    item.setType(MainItem.TYPE_FLAG);
                    break;
                case "DR":
                    item.setType(MainItem.TYPE_LIST);
                    break;

            }

            item.setName(object.get("name").getAsString());
            item.setCode(object.get("code").getAsString());

            if(object.has("val")&&!object.get("val").isJsonNull()){
                ArrayList<ListItem> listItems = new ArrayList<>();
                JsonArray val = object.get("val").getAsJsonArray();
                for (int j = 0; j < val.size(); j++) {
                    ListItem listItem = new ListItem(val.get(j).getAsJsonObject().get("id").getAsLong(),val.get(j).getAsJsonObject().get("name").getAsString());
                    listItems.add(listItem);
                }
                item.setListValues(listItems);
                item.setListValue(listItems.get(0));


            }

            items.add(item);
        }

        return items;
    }

    private static ArrayList<MainItem> getPhotoItems(JsonArray array, boolean isVideo,boolean isNeedDefects){
        ArrayList<MainItem> items = new ArrayList<>();

        MainItem item = new MainItem();
        item.setType(MainItem.TYPE_HEADER);
        item.setName(array.get(0).getAsJsonObject().get("group").getAsString());
        items.add(item);
        ArrayList<PhotoItem> photoItems = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {

            JsonObject object = array.get(i).getAsJsonObject();
            PhotoItem photoItem = new PhotoItem(object.get("name").getAsString());

            if(i == array.size()-1){
                if(isNeedDefects){
                    photoItem.setDefect(true);
                    Utility.saveData(Const.DEFAULT_DEFECT_NAME,photoItem.getTitle());
                    photoItem.setTitle(photoItem.getTitle()+" 1");
                }else {
                    photoItem.setDefect(false);
                }


            }else {
                photoItem.setDefect(false);
            }
            if(isVideo){
                photoItem.setDuration(15);
            }
            photoItems.add(photoItem);



        }
        MainItem photoItem = new MainItem();
        if(isVideo){
            photoItem.setType(MainItem.TYPE_VIDEO);
        }else {
            photoItem.setType(MainItem.TYPE_PHOTO);
        }

        photoItem.setPhotoItems(photoItems);
        items.add(photoItem);


        return items;
    }

    private ArrayList<MainItem> getFirstScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        items.addAll(getItems(general));
        items.addAll(getItems(tec));
        items.addAll(getItems(teh));
        items.addAll(getPhotoItems(photogeneral,false,true));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;
    }

    private ArrayList<MainItem> getSecondScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        items.addAll(getItems(cabn));
        items.addAll(getPhotoItems(photocabine,false,true));
        items.addAll(getPhotoItems(photoglass,false,true));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;

    }

    private ArrayList<MainItem> getThirdScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        items.addAll(getItems(shas));
        items.addAll(getPhotoItems(photochassis,false,true));
        items.addAll(getPhotoItems(photoengine,false,true));
        items.addAll(getPhotoItems(phototrans,false,true));
        items.addAll(getPhotoItems(photoshas,false,true));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;
    }

    private ArrayList<MainItem> getFourthScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        MainItem schemeItem = new MainItem(MainItem.TYPE_TIRE_SCHEME);
        items.add(schemeItem);
        items.addAll(getItems(shin));
        items.addAll(getPhotoItems(photobus,false,false));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;
    }

    private ArrayList<MainItem> getFifthScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        items.addAll(getItems(optional));
        items.addAll(getPhotoItems(photodop,false,false));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;
    }

    private ArrayList<MainItem> getSixthScreenItems(){
        ArrayList<MainItem> items = new ArrayList<>();
        items.addAll(getPhotoItems(video,true,false));
        MainItem item = new MainItem(MainItem.TYPE_BOTTOM_BAR);
        items.add(item);
        return items;
    }

    public ArrayList<MainItem> getScreenItems(int page){
        switch (page){
            case 1:
                return getFirstScreenItems();
            case 2:
                return getSecondScreenItems();
            case 3:
                return getThirdScreenItems();
            case 4:
                return getFourthScreenItems();
            case 5:
                return getFifthScreenItems();
            case 6:
                return getSixthScreenItems();
            default:
                return getFirstScreenItems();
        }
    }



    public ArrayList<ListItem> getMarks(){
        ArrayList<ListItem> items = new ArrayList<>();
       // items.add(new MainItem(0,));
        for (int i = 0; i < general.size(); i++) {
            if(general.get(i).getAsJsonObject().get("code").getAsString().equals("general_marka")){
                JsonArray marks = general.get(i).getAsJsonObject().getAsJsonArray("val");
                for (int j = 0; j < marks.size(); j++) {
                    JsonObject markObject = marks.get(j).getAsJsonObject();
                    ListItem mainItem = new ListItem(markObject.get("id").getAsLong(),markObject.get("name").getAsString());
                    items.add(mainItem);
                }
            }
        }
        return items;
    }
    public String getMarkName(){
        for (int i = 0; i < general.size(); i++) {
            if(general.get(i).getAsJsonObject().get("code").getAsString().equals("general_marka")){
               return general.get(i).getAsJsonObject().get("name").getAsString();
            }
        }
        return "Марка";
    }

    public String getModelName(){
        for (int i = 0; i < general.size(); i++) {
            if(general.get(i).getAsJsonObject().get("code").getAsString().equals("general_model")){
                return general.get(i).getAsJsonObject().get("name").getAsString();
            }
        }
        return "Модель";
    }
}
