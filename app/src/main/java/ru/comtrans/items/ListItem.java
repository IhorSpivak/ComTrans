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
    public static final String JSON_VALUE_ENGINE_MARK = "value_engine_mark";
    public static final String JSON_VALUE_MODEL = "value_model";
    public static final String JSON_VALUE_ENGINE_MODEL = "value_engine_model";
    public static final String JSON_VALUE_KPP_MODEL = "value_kpp_model";

    public static final String JSON_PROTECTOR_VALUES = "protector_values";
    public static final String JSON_TIRE_SCHEME_ID = "tire_scheme_id";
    public static final String JSON_REVEAL_OS = "reveal_os";

    private long id;
    private String name;
    private int mark;
    private int engineMark;

    private ArrayList<Integer> model;
    private ArrayList<Integer> engineModel;
    private ArrayList<String> protectorValues;
    private ArrayList<Integer> revealOs;
    private int tireSchemeId;



    public ArrayList<Integer> getRevealOs() {
        return revealOs;
    }

    public void setRevealOs(ArrayList<Integer> revealOs) {
        this.revealOs = revealOs;
    }

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

    public int getEngineMark() {
        return engineMark;
    }

    public void setEngineMark(int engineMark) {
        this.engineMark = engineMark;
    }

    public ArrayList<Integer> getModel() {
        return model;
    }

    public void setModel(ArrayList<Integer> model) {
        this.model = model;
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

    public ArrayList<Integer> getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(ArrayList<Integer> engineModel) {
        this.engineModel = engineModel;
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
        dest.writeInt(this.engineMark);
        dest.writeStringList(this.protectorValues);
        dest.writeList(this.revealOs);
        dest.writeList(this.model);
        dest.writeList(this.engineModel);
        dest.writeInt(this.tireSchemeId);
    }

    protected ListItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.mark = in.readInt();
        this.engineMark = in.readInt();
        this.protectorValues = in.createStringArrayList();
        this.revealOs = new ArrayList<>();
        in.readList(this.revealOs, Integer.class.getClassLoader());
        this.model = new ArrayList<>();
        in.readList(this.model, Integer.class.getClassLoader());
        this.engineModel = new ArrayList<>();
        in.readList(this.engineModel, Integer.class.getClassLoader());
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
