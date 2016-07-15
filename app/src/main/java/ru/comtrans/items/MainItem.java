package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Artco on 07.07.2016.
 */
public class MainItem implements Parcelable {
    public static final int TYPE_LIST = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_HEADER = 3;
    public static final int TYPE_FLAG = 4;
    public static final int TYPE_PHOTO = 5;
    public static final int TYPE_VIDEO = 6;
    public static final int TYPE_BOTTOM_BAR = 7;


    long id;
    String name;
    int type;
    ArrayList<ListItem> values;
    ArrayList<PhotoItem> photoItems;

    public ArrayList<PhotoItem> getPhotoItems() {
        if(values==null){
            values = new ArrayList<>();
        }
        return photoItems;
    }

    public void setPhotoItems(ArrayList<PhotoItem> photoItems) {
        this.photoItems = photoItems;
    }

    public ArrayList<ListItem> getValues() {
        if(values==null){
            values = new ArrayList<>();
        }
        return values;
    }

    public void setValues(ArrayList<ListItem> values) {
        this.values = values;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MainItem() {
    }

    public MainItem(long id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.type);
    }

    protected MainItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<MainItem> CREATOR = new Creator<MainItem>() {
        @Override
        public MainItem createFromParcel(Parcel source) {
            return new MainItem(source);
        }

        @Override
        public MainItem[] newArray(int size) {
            return new MainItem[size];
        }
    };
}
