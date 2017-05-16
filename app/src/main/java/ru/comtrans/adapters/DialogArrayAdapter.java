package ru.comtrans.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.items.DialogItem;

/**
 * Created by Artco on 05.10.2016.
 */

public class DialogArrayAdapter extends BaseAdapter {
    private ArrayList<DialogItem> items;

    public DialogArrayAdapter() {

    }

    public void setItems(ArrayList<DialogItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public DialogItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        TextView textView = (TextView) v;
        textView.setText(getItem(position).getStr());

        return v;
    }
}
