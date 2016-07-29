package ru.comtrans.helpers;

import java.util.ArrayList;

import ru.comtrans.items.MainItem;

/**
 * Created by Artco on 27.07.2016.
 */
public class InfoBlockHelper {
    private InfoBlocksStorage storage;
    private static InfoBlockHelper instance;
    private ArrayList<ArrayList<MainItem>> items;
    private String id;

    public static InfoBlockHelper getInstance(String id) {
        if(instance==null)
            instance = new InfoBlockHelper(id);
        return instance;
    }

    private InfoBlockHelper(String id){
        this.id = id;
        items = new ArrayList<>();
        storage = InfoBlocksStorage.getInstance();
    }

    public void getAllItems(String id){
        items = storage.getInfoBlock(id);
    }

    public void saveScreen(ArrayList<MainItem> screen,int position){
        items.set(position,screen);
    }
    public void saveAll(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                storage.saveInfoBlock(items,id);
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
