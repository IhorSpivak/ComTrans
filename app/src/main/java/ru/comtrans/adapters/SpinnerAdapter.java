package ru.comtrans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.ListItem;

/**
 * Created by Artco on 07.07.2016.
 */
public class SpinnerAdapter extends BaseAdapter {
    Context c;
    ArrayList<ListItem> items;

    public SpinnerAdapter(Context c, ArrayList<ListItem> items){
        this.c = c;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View row = inflater.inflate(android.R.layout.simple_spinner_item, viewGroup,
                false);
        ((TextView)row).setText(items.get(i).getName());

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,
                false);
        ((TextView)row).setText(items.get(position).getName());


        return row;
    }
}
