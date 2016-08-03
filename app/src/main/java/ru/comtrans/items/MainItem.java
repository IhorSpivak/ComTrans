package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Artco on 07.07.2016.
 */
public class MainItem implements Parcelable {

    public static final String JSON_TYPE = "type";
    public static final String JSON_CODE = "code";
    public static final String JSON_VALUE = "value";
    public static final String JSON_IS_CHECKED = "is_checked";
    public static final String JSON_LIST_VALUE = "list_value";
    public static final String JSON_LIST_VALUES = "list_values";
    public static final String JSON_PHOTO_VALUES = "photo_values";
    public static final String JSON_NAME = "name";
    public static final String JSON_ID = "id";



    public static final int TYPE_LIST = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_HEADER = 3;
    public static final int TYPE_FLAG = 4;
    public static final int TYPE_PHOTO = 5;
    public static final int TYPE_VIDEO = 6;
    public static final int TYPE_BOTTOM_BAR = 7;
    public static final int TYPE_TIRE_SCHEME = 8;


    String id;
    String name;
    String code;
    String value;
    int type;
    boolean isChecked;
    ListItem listValue;
    ArrayList<ListItem> listValues;
    ArrayList<PhotoItem> photoItems;



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ListItem getListValue() {
        return listValue;
    }

    public void setListValue(ListItem listValue) {
        this.listValue = listValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<PhotoItem> getPhotoItems() {
        if(photoItems ==null){
            photoItems = new ArrayList<>();
        }
        return photoItems;
    }

    public int getPhotosCount(){
        if(photoItems ==null){
            return 0;
        }else {
            int count = 0;
            for(PhotoItem item:photoItems){
                if(!item.isDefect())
                    count++;
            }
            return count;
        }

    }

    public void setPhotoItems(ArrayList<PhotoItem> photoItems) {
        this.photoItems = photoItems;
    }

    public ArrayList<ListItem> getListValues() {
        if(listValues ==null){
            listValues = new ArrayList<>();
        }
        return listValues;
    }

    public void setListValues(ArrayList<ListItem> listValues) {
        this.listValues = listValues;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public MainItem(int type) {
        this.type = type;
    }

    public MainItem(String id, String name, int type) {
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
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeString(this.value);
        dest.writeInt(this.type);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.listValue, flags);
        dest.writeTypedList(this.listValues);
        dest.writeTypedList(this.photoItems);
    }

    protected MainItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.code = in.readString();
        this.value = in.readString();
        this.type = in.readInt();
        this.isChecked = in.readByte() != 0;
        this.listValue = in.readParcelable(ListItem.class.getClassLoader());
        this.listValues = in.createTypedArrayList(ListItem.CREATOR);
        this.photoItems = in.createTypedArrayList(PhotoItem.CREATOR);
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
