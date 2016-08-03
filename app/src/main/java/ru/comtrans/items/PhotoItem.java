package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artco on 24.05.2016.
 */
public class PhotoItem implements Parcelable {

    public static final String JSON_IS_DEFECT = "is_defect";
    public static final String JSON_TITLE = "title";
    public static final String JSON_IMAGE_PATH = "image_path";
    public static final String JSON_ID = "id";
    public static final String JSON_DURATION = "duration";
    public static final String JSON_IS_VIDEO = "is_video";






    private boolean isDefect;
    private String title;
    private String imagePath;
    private String id;
    private int duration;
    private boolean isVideo;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public PhotoItem(){}

    public PhotoItem(String title,boolean isDefect){
        this.title = title;
        this.isDefect = isDefect;
    }

    public PhotoItem(String title){
        this.title = title;
    }

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
        dest.writeInt(this.duration);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
    }

    protected PhotoItem(Parcel in) {
        this.isDefect = in.readByte() != 0;
        this.title = in.readString();
        this.imagePath = in.readString();
        this.id = in.readString();
        this.duration = in.readInt();
        this.isVideo = in.readByte() != 0;
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
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
