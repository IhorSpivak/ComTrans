package ru.comtrans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraPhotoAdapter extends BaseAdapter {
    private ArrayList<PhotoItem> items;
    private Context context;

    public CameraPhotoAdapter(ArrayList<PhotoItem> items, Context context){
        this.items = items;
        this.context = context;
    }

    static class ViewHolder{
        public TextView title;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PhotoItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.photo_list_item, parent, false);
            viewHolder = new ViewHolder();
           // viewHolder.title = (TextView) convertView.findViewById(R.id.photo_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

      //  viewHolder.txtItem.setText(getItem(position));

        return convertView;
    }
}
