package ru.comtrans.items;

/**
 * Created by Artco on 10.08.2016.
 */
public class ProtectorItem {
    public static final String JSON_CODE = "json_code";
    public static final String JSON_TITLE = "json_title";
    public static final String JSON_VALUE = "json_value";
    public static final String JSON_TYPE = "json_type";
    public static final String JSON_GROUP_NAME = "json_group_name";
    private String code,title,value,groupName;
    private int type;
    public static final int TYPE_PROTECTOR = 1;
    public static final int TYPE_HEADER = 2;

    public ProtectorItem(){}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
