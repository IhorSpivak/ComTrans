package ru.comtrans.singlets;

import android.util.Log;

import java.util.ArrayList;

import ru.comtrans.items.IdsRelationHelperItem;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.items.ProtectorItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlockHelper {
    private InfoBlocksStorage storage;
    final String LOG_TAG = "myLogs";
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
        if (instance == null)
            instance = new InfoBlockHelper();
        return instance;
    }

    public InfoBlockHelper() {
        items = new ArrayList<>();
        storage = InfoBlocksStorage.getInstance();
    }

    public void getAllItems(String id) {
        this.id = id;
        items.clear();
        items = storage.getInfoBlock(id);
    }

    public ListItem getValueByKey(String key) {
        for (ArrayList<MainItem> array :
                items) {
            for (MainItem item :
                    array) {
                if (item.getCode() != null && item.getCode().equals(key)) {
                    return item.getListValue();
                }
            }
        }
        return null;
    }

    public ListItem getTireSchemeValue() {
        return getValueByKey("shas_wheel_formula");
    }

    public ListItem getModelValue() {
        return getValueByKey("general_model");
    }

    public ListItem getMarkValue() {
        return getValueByKey("general_marka");
    }

    public ListItem getEngineMarkValue() {
        return getValueByKey("tec_engine_mark");
    }

    public ListItem getEngineModelValue() {
        return getValueByKey("tec_engine_model");
    }

    public ListItem getMarkKppValue() {
        return getValueByKey("MARKA_KPP");
    }

    public ListItem getModelKppValue() {
        return getValueByKey("MODEL_KPP");
    }

    public ListItem getVehicleOwnerValue() {
        return getValueByKey("SOBSTVENNIK_CHASTNOE_YUR_LITSO");
    }

    public ListItem getMarkKMUValue() {
        return getValueByKey("MARKA_KMU");
    }

    public ListItem getMarkKHOUValue() {
        return getValueByKey("holod_brand");
    }

    public ListItem getInspectionCodeValue() {
        return getValueByKey(IdsRelationHelperItem.CODE_INSPECTION_TYPE);
    }


    public void saveScreen(ArrayList<MainItem> screen, int position) {
        items.set(position, screen);
    }

    public void saveAll() {
        storage.saveInfoBlock(id, items);
    }

    public void cancelSaving(){
        storage.cancelSaving();
        Log.e(LOG_TAG, "AsyncTask существует. Процесс закрытия  storage.cancelSaving()");
    }

    public void saveProtector(ArrayList<ProtectorItem> protectorItems) {
        for (int k = 0; k < items.size(); k++) {
            for (int l = 0; l < items.get(k).size(); l++) {
                MainItem item = items.get(k).get(l);
                if (item.getType() == MainItem.TYPE_TIRE_SCHEME) {
                    for (int i = 0; i < item.getProtectorItems().size(); i++) {
                        for (int j = 0; j < protectorItems.size(); j++) {
                            if (item.getProtectorItems().get(i).getCode() != null && item.getProtectorItems().get(i).getCode().equals(protectorItems.get(j).getCode())) {

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

    public void savePhotos(int screenNum, int position, ArrayList<PhotoItem> photoItems) {
        ArrayList<PhotoItem> currentPhotoItems = new ArrayList<>(items.get(screenNum).get(position).getPhotoItems());
        ArrayList<PhotoItem> defectItems = new ArrayList<>();
        ArrayList<PhotoItem> currentDefectItems = new ArrayList<>();

        for (int i = 0; i < photoItems.size(); i++) {
            if (photoItems.get(i).isDefect()) {
                defectItems.add(photoItems.get(i));
                Log.d("TAG", "defects " + photoItems.get(i).getTitle());
            }
        }
        photoItems.removeAll(defectItems);


        for (int i = 0; i < currentPhotoItems.size(); i++) {
            if (currentPhotoItems.get(i).isDefect())
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
            Log.d("TAG", "------------------------- " + currentPhotoItems.get(i).getTitle());
        }


        items.get(screenNum).get(position).setPhotoItems(currentPhotoItems);
    }

    public ArrayList<PhotoItem> getPhotos(int screenNum, int position) {
        ArrayList<PhotoItem> newItems = new ArrayList<>();
        if(position<items.get(screenNum).size()) {
            ArrayList<PhotoItem> currentPhotoItems = items.get(screenNum).get(position).getPhotoItems();
            for (PhotoItem photoItem : currentPhotoItems) {
                if (!photoItem.isDefect()) {
                    if (photoItem.getIsOs() != 0 && getTireSchemeValue().getId() != -1) {
                        if (getTireSchemeValue().getRevealOs().contains(photoItem.getIsOs())) {
                            newItems.add(photoItem);
                        }
                    } else {
                        newItems.add(photoItem);
                    }
                }
            }
            for (int i = 0; i < currentPhotoItems.size(); i++) {
                if (currentPhotoItems.get(i).isDefect())
                    newItems.add(currentPhotoItems.get(i));
            }
        }
        return newItems;

    }


    public int getItemsSize() {
        return items.size();
    }

    public ArrayList<MainItem> getScreen(int position) {
        return items.get(position);
    }
}
