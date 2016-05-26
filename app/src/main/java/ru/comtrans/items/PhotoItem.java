package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artco on 24.05.2016.
 */
public class PhotoItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDefect ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.imagePath);
        dest.writeString(this.id);
    }

    protected PhotoItem(Parcel in) {
        this.isDefect = in.readByte() != 0;
        this.title = in.readString();
        this.imagePath = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<PhotoItem> CREATOR = new Parcelable.Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel source) {
            return new PhotoItem(source);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };
}
