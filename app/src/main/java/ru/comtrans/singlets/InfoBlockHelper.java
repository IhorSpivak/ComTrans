package ru.comtrans.singlets;

import android.util.Log;

import java.util.ArrayList;

import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlockHelper {
    private InfoBlocksStorage storage;
    private static InfoBlockHelper instance;
    private ArrayList<ArrayList<MainItem>> items;
    private String id;

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

    public void saveScreen(ArrayList<MainItem> screen,int position){
        items.set(position,screen);
    }
    public void saveAll(final boolean removeItems){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    storage.saveInfoBlock(id, items);
                    if (removeItems) {
                        items.clear();
                    }
                }catch (Exception e){
                    Log.e("TAG","error",e);
                }

            }
        });
        t.start();
    }



    public ArrayList<ArrayList<MainItem>> getItems() {
        return items;
    }
    public int getItemsSize(){
        return items.size();
    }

    public ArrayList<MainItem> getScreen(int position){
        return items.get(position);
    }
}
