package ru.comtrans.items;

/**
 * Created by Artco on 24.05.2016.
 */
public class PhotoItem {
    private boolean isDefect;
    private String title;
    private String imagePath;
    private String id;

    public PhotoItem(){}

    public boolean isDefect() {
        return isDefect;
    }

    public void setDefect(boolean defect) {
        isDefect = defect;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
