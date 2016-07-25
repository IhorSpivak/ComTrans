package ru.comtrans.items;

/**
 * Created by Artco on 25.07.2016.
 */
public class MyInfoBlockItem {
    private String date,status,mark,model,year,photoPath;

    public MyInfoBlockItem(String date, String status, String mark, String model, String year, String photoPath) {
        this.date = date;
        this.status = status;
        this.mark = mark;
        this.model = model;
        this.year = year;
        this.photoPath = photoPath;
    }

    public MyInfoBlockItem(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
