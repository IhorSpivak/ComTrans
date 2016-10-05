package ru.comtrans.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.comtrans.items.DialogItem;

/**
 * Created by Artco on 05.10.2016.
 */

public class DialogArrayAdapter extends ArrayAdapter<DialogItem> {
    private ArrayList<DialogItem> items;


    public DialogArrayAdapter(Context context, int resource, ArrayList<DialogItem> objects) {
        super(context, resource, objects);
        this.items = objects;


    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        TextView textView = (TextView) v;
        textView.setText(getItem(position).getStr());

        return v;
    }
}
