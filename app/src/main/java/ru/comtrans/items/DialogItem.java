package ru.comtrans.items;

import android.content.Context;

/**
 * Created by Artco on 05.10.2016.
 */

public class DialogItem {
    String str;
    int resId;

    public DialogItem(int resId, Context context) {
        this.resId = resId;
        str = context.getString(resId);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
