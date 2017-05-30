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
    public static final String JSON_VALUE_KPP_MARK = "value_kpp_mark";
    public static final String JSON_VALUE_KPP_MODEL = "value_kpp_model";
    public static final String JSON_VALUE_VEHICLE_OWNER = "value_vehicle_owner";
    public static final String JSON_VALUE_KHOU_MARK = "value_khou_mark";
    public static final String JSON_VALUE_KMU_MARK = "value_kmu_mark";
    public static final String JSON_VALUE_SECTIONS = "value_sections";

    public static final String JSON_PROTECTOR_VALUES = "protector_values";
    public static final String JSON_TIRE_SCHEME_ID = "tire_scheme_id";
    public static final String JSON_REVEAL_OS = "reveal_os";

    private long id;
    private String name;
    private ArrayList<Integer> mark;
    private ArrayList<Integer> engineMark;
    private ArrayList<Integer> tireSchemeId;

    private ArrayList<Integer> model;
    private ArrayList<Integer> engineModel;
    private ArrayList<Integer> kppMark;
    private ArrayList<Integer> kppModel;
    private ArrayList<Integer> vehicleOwner;
    private ArrayList<String> protectorValues;
    private ArrayList<Integer> revealOs;
    private ArrayList<Integer> khouMark;
    private ArrayList<Integer> kmuMark;
    private ArrayList<Integer> sections;



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

    public ArrayList<Integer> getMark() {
        return mark;
    }

    public void setMark(ArrayList<Integer> mark) {
        this.mark = mark;
    }

    public ArrayList<Integer> getEngineMark() {
        return engineMark;
    }

    public void setEngineMark(ArrayList<Integer> engineMark) {
        this.engineMark = engineMark;
    }

    public ArrayList<Integer> getTireSchemeId() {
        return tireSchemeId;
    }

    public void setTireSchemeId(ArrayList<Integer> tireSchemeId) {
        this.tireSchemeId = tireSchemeId;
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

    public ArrayList<Integer> getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(ArrayList<Integer> vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public ArrayList<Integer> getEngineModel() {
        return engineModel;
    }

    public ArrayList<Integer> getKppModel() {
        return kppModel;
    }

    public ArrayList<Integer> getKppMark() {
        return kppMark;
    }

    public void setKppMark(ArrayList<Integer> kppMark) {
        this.kppMark = kppMark;
    }

    public void setKppModel(ArrayList<Integer> kppModel) {
        this.kppModel = kppModel;
    }

    public void setEngineModel(ArrayList<Integer> engineModel) {
        this.engineModel = engineModel;
    }

    public ListItem(long id, String name) {
        this.id = id;
        this.name = name;

    }

    public ArrayList<Integer> getKhouMark() {
        return khouMark;
    }

    public void setKhouMark(ArrayList<Integer> khouMark) {
        this.khouMark = khouMark;
    }

    public ArrayList<Integer> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Integer> sections) {
        this.sections = sections;
    }

    public ArrayList<Integer> getKmuMark() {
        return kmuMark;
    }

    public void setKmuMark(ArrayList<Integer> kmuMark) {
        this.kmuMark = kmuMark;
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
        dest.writeList(this.mark);
        dest.writeList(this.engineMark);
        dest.writeStringList(this.protectorValues);
        dest.writeList(this.revealOs);
        dest.writeList(this.model);
        dest.writeList(this.engineModel);
        dest.writeList(this.kppMark);
        dest.writeList(this.kppModel);
        dest.writeList(this.khouMark);
        dest.writeList(this.kmuMark);
        dest.writeList(this.sections);
        dest.writeList(this.vehicleOwner);
        dest.writeList(this.tireSchemeId);
    }

    protected ListItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.mark = new ArrayList<>();
        in.readList(this.mark, Integer.class.getClassLoader());
        this.engineMark = new ArrayList<>();
        in.readList(this.engineMark, Integer.class.getClassLoader());
        this.protectorValues = in.createStringArrayList();
        this.revealOs = new ArrayList<>();
        in.readList(this.revealOs, Integer.class.getClassLoader());
        this.model = new ArrayList<>();
        in.readList(this.model, Integer.class.getClassLoader());
        this.engineModel = new ArrayList<>();
        in.readList(this.engineModel, Integer.class.getClassLoader());
        this.kppMark = new ArrayList<>();
        in.readList(this.kppMark, Integer.class.getClassLoader());
        this.kppModel = new ArrayList<>();
        in.readList(this.kppModel, Integer.class.getClassLoader());
        this.khouMark = new ArrayList<>();
        in.readList(this.khouMark, Integer.class.getClassLoader());
        this.kmuMark = new ArrayList<>();
        in.readList(this.kmuMark, Integer.class.getClassLoader());
        this.sections = new ArrayList<>();
        in.readList(this.sections, Integer.class.getClassLoader());
        this.vehicleOwner = new ArrayList<>();
        in.readList(this.vehicleOwner, Integer.class.getClassLoader());
        this.tireSchemeId = new ArrayList<>();
        in.readList(this.tireSchemeId, Integer.class.getClassLoader());
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
