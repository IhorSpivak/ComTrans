package ru.comtrans.items;

/**
 * Created by Artco on 25.07.2016.
 */
public class MyInfoBlockItem {

    public static final int STATUS_DRAFT = 11;
    public static final int STATUS_SENDING = 12;
    public static final int STATUS_SENT = 13;
    public static final int STATUS_STOPPED = 14;

    public static final String JSON_ID = "id";
    public static final String JSON_DATE = "date";
    public static final String JSON_SIZE = "size";
    public static final String JSON_STATUS = "status";
    public static final String JSON_MARK = "mark";
    public static final String JSON_MODEL = "model";
    public static final String JSON_YEAR = "year";
    public static final String JSON_PHOTO_PATH = "photo_path";
    public static final String JSON_LAST_POSITION = "last_position";
    public static final String JSON_PROGRESS = "progress";

    private int status;
    private double size;
    private int lastPosition,progress;

    private String id,date,mark,model,year,photoPath;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyInfoBlockItem(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}
