package ru.comtrans.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artco on 24.05.2016.
 */
public class PhotoItem implements Parcelable {


    public static final String JSON_IS_DEFECT = "is_defect";
    public static final String JSON_TITLE = "title";
    public static final String JSON_SIZE = "size";
    public static final String JSON_CODE = "code";
    public static final String JSON_IMAGE_PATH = "image_path";
    public static final String JSON_ID = "id";
    public static final String JSON_DURATION = "duration";
    public static final String JSON_IS_VIDEO = "is_video";
    public static final String JSON_IS_SEND = "is_send";
    public static final String JSON_IS_EDITED = "is_edited";
    public static final String JSON_RE_PHOTO_COUNT = "re_photo_count";
    public static final String JSON_IS_OS = "is_os";





    private long size;
    private boolean isDefect;
    private String title;
    private String imagePath;
    private long id;
    private String code;
    private int duration;
    private int rePhotoCount = 0;
    private boolean isVideo;
    private boolean isSend = false;
    private boolean isEdited = false;
    private int isOs;

    public int getIsOs() {
        return isOs;
    }

    public void setIsOs(int isOs) {
        this.isOs = isOs;
    }

    public void incrementRePhotoCount(){
        rePhotoCount++;
    }

    public int getRePhotoCount() {
        return rePhotoCount;
    }

    public void setRePhotoCount(int rePhotoCount) {
        this.rePhotoCount = rePhotoCount;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.size);
        dest.writeByte(this.isDefect ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.imagePath);
        dest.writeLong(this.id);
        dest.writeString(this.code);
        dest.writeInt(this.duration);
        dest.writeInt(this.rePhotoCount);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSend ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEdited ? (byte) 1 : (byte) 0);
    }

    protected PhotoItem(Parcel in) {
        this.size = in.readLong();
        this.isDefect = in.readByte() != 0;
        this.title = in.readString();
        this.imagePath = in.readString();
        this.id = in.readLong();
        this.code = in.readString();
        this.duration = in.readInt();
        this.rePhotoCount = in.readInt();
        this.isVideo = in.readByte() != 0;
        this.isSend = in.readByte() != 0;
        this.isEdited = in.readByte() != 0;
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
