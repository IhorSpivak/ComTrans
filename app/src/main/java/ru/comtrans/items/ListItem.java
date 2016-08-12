package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Artco on 07.07.2016.
 */
public class ListItem implements Parcelable {

    public static final String JSON_VALUE_ID = "value_id";
    public static final String JSON_VALUE_NAME = "value_name";
    public static final String JSON_VALUE_MARK = "value_mark";
    public static final String JSON_PROTECTOR_VALUES = "protector_values";
    public static final String JSON_TIRE_SCHEME_ID = "tire_scheme_id";

    private long id;
    private String name;
    private int mark;

    private ArrayList<String> protectorValues;
    private int tireSchemeId;



    public ArrayList<String> getProtectorValues() {
        return protectorValues;
    }

    public void setProtectorValues(ArrayList<String> protectorValues) {
        this.protectorValues = protectorValues;
    }

    public int getTireSchemeId() {
        return tireSchemeId;
    }

    public void setTireSchemeId(int tireSchemeId) {
        this.tireSchemeId = tireSchemeId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
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

    public ListItem() {
    }

    public ListItem(long id, String name) {
        this.id = id;
        this.name = name;

    }


    @Override
    public String toString() {
        return id+" "+name+" "+mark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.mark);
        dest.writeStringList(this.protectorValues);
        dest.writeInt(this.tireSchemeId);
    }

    protected ListItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.mark = in.readInt();
        this.protectorValues = in.createStringArrayList();
        this.tireSchemeId = in.readInt();
    }

    public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
        @Override
        public ListItem createFromParcel(Parcel source) {
            return new ListItem(source);
        }

        @Override
        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };
}
