package ru.comtrans.singlets;

import android.util.Log;

import java.util.ArrayList;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlockHelper {
    private InfoBlocksStorage storage;
    private static InfoBlockHelper instance;
    private ArrayList<ArrayList<MainItem>> items;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static InfoBlockHelper getInstance() {
        if(instance==null)
            instance = new InfoBlockHelper();
        return instance;
    }

    private InfoBlockHelper(){
        items = new ArrayList<>();
        storage = InfoBlocksStorage.getInstance();
    }

    public void getAllItems(String id){
        this.id = id;
        items.clear();
        items = storage.getInfoBlock(id);
    }

    public ListItem getTireSchemeValue(){
        for (ArrayList<MainItem> array :
                items) {
            for (MainItem item :
                    array) {
                if(item.getCode()!=null&&item.getCode().equals("shas_wheel_formula")){
                    return item.getListValue();
                }
            }
        }
        return null;
    }

    public ListItem getModelValue(){
        for (ArrayList<MainItem> array :
                items) {
            for (MainItem item :
                    array) {
                if(item.getCode()!=null&&item.getCode().equals("general_model")){
                    return item.getListValue();
                }
            }
        }
        return null;
    }

    public ListItem getMarkValue(){
        for (ArrayList<MainItem> array :
                items) {
            for (MainItem item :
                    array) {
                if(item.getCode()!=null&&item.getCode().equals("general_marka")){
                    return item.getListValue();
                }
            }
        }
        return null;
    }





    public void saveScreen(ArrayList<MainItem> screen,int position){
        items.set(position,screen);
    }
    public void saveAll(){
            storage.saveInfoBlock(id, items);
     }

    public void saveProtector(ArrayList<ProtectorItem> protectorItems){
        for (int k = 0; k < items.size(); k++) {
            for(int l=0; l<items.get(k).size(); l++){
                MainItem item = items.get(k).get(l);
                if(item.getType()==MainItem.TYPE_TIRE_SCHEME){
                    for(int i=0;i<item.getProtectorItems().size(); i++){
                        for(int j=0; j<protectorItems.size(); j++){
                            if(item.getProtectorItems().get(i).getCode()!=null&&item.getProtectorItems().get(i).getCode().equals(protectorItems.get(j).getCode())){

                                items.get(k).get(l).getProtectorItems().get(i).setValue(protectorItems.get(j).getValue());
                            }
                        }
                    }
                }
        }
        }
    }



    public ArrayList<ArrayList<MainItem>> getItems() {
        return items;
    }

    public void savePhotos(int screenNum, int position, ArrayList<PhotoItem> photoItems){
        ArrayList<PhotoItem> currentPhotoItems = new ArrayList<>(items.get(screenNum).get(position).getPhotoItems());
        ArrayList<PhotoItem> defectItems = new ArrayList<>();
        ArrayList<PhotoItem> currentDefectItems = new ArrayList<>();

        for (int i = 0; i <photoItems.size() ; i++) {
            if(photoItems.get(i).isDefect()) {
                defectItems.add(photoItems.get(i));
                Log.d("TAG","defects "+photoItems.get(i).getTitle());
            }
        }
        photoItems.removeAll(defectItems);



        for (int i = 0; i < currentPhotoItems.size(); i++) {
            if(currentPhotoItems.get(i).isDefect())
                currentDefectItems.add(currentPhotoItems.get(i));
        }

        currentPhotoItems.removeAll(currentDefectItems);



        for (int i = 0; i < currentPhotoItems.size(); i++) {
            for (int j = 0; j < photoItems.size(); j++) {
                if (currentPhotoItems.get(i).getCode().equals(photoItems.get(j).getCode())) {
                    currentPhotoItems.get(i).setImagePath(photoItems.get(j).getImagePath());
                }
            }

        }


        currentPhotoItems.addAll(defectItems);

        for (int i = 0; i < currentPhotoItems.size(); i++) {
            Log.d("TAG","------------------------- "+currentPhotoItems.get(i).getTitle());
        }


        items.get(screenNum).get(position).setPhotoItems(currentPhotoItems);
    }

    public ArrayList<PhotoItem> getPhotos(int screenNum, int position){
        ArrayList<PhotoItem> newItems = new ArrayList<>();
        ArrayList<PhotoItem> currentPhotoItems = items.get(screenNum).get(position).getPhotoItems();
        for (PhotoItem photoItem : currentPhotoItems) {
            if (!photoItem.isDefect()) {
                if(photoItem.getIsOs()!=0&&getTireSchemeValue().getId()!=-1){
                    if(getTireSchemeValue().getRevealOs().contains(photoItem.getIsOs())){
                        newItems.add(photoItem);
                    }
                }else {
                    newItems.add(photoItem);
                }
            }
        }
        for (int i = 0; i < currentPhotoItems.size(); i++) {
            if(currentPhotoItems.get(i).isDefect())
                newItems.add(currentPhotoItems.get(i));
        }

        return newItems;

    }


    public int getItemsSize(){
        return items.size();
    }

    public ArrayList<MainItem> getScreen(int position){
        return items.get(position);
    }
}