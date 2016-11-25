package ru.comtrans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.comtrans.R;
import ru.comtrans.items.ListItem;

public class ListAdapter extends BaseAdapter implements Filterable {



    private ArrayList<ListItem> mData = new ArrayList<>();
    private ArrayList<ListItem> items = new ArrayList<>();
    private long mark;


    private LayoutInflater mInflater;

    public ListAdapter(Context context,ArrayList<ListItem> items, long mark) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.items = items;
        this.mark = mark;
        enterValues(items);
    }

    public void enterValues(ArrayList<ListItem> items){
        mData.clear();

        for (ListItem item:items) {
            if(mark!=-1){
                if(item.getId()==-1||item.getMark()==mark){
                    addItemToTempArray(item);
                }
            }else {
                addItemToTempArray(item);
            }

        }
        notifyDataSetChanged();
    }

    public void addItemToTempArray(final ListItem item) {
        mData.add(item);
    }

    public void addItem(ListItem item){
        items.add(item);
        enterValues(items);
    }

    private void sortItems(){
        Collections.sort(mData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem s1, ListItem s2) {
                if(s1.getId()==-1){
                    return 1;
                }else if(s2.getId()==-1){
                    return 1;
                }else {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }

            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        sortItems();
        super.notifyDataSetChanged();
    }

    public boolean containsValue(String value){
        for (ListItem item :
                mData) {
            if(item.getName()!=null&&item.getName().equals(value)){
                return true;
            }
        }
        return false;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ListItem getItem(int position) {
        return mData.get(position);
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.search_list_item, parent,false);
            holder.textView = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                enterValues((ArrayList<ListItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<ListItem> FilteredArrayNames = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = items.size();
                    results.values = items;
                } else {
                    String searchCriteria = constraint.toString().toLowerCase();
                    for (int i = 0; i < items.size(); i++) {
                        String dataNames = items.get(i).getName();
                        if (dataNames.toLowerCase().contains(searchCriteria))  {
                            FilteredArrayNames.add(items.get(i));
                        }
                    }

                    results.count = FilteredArrayNames.size();


                    results.values = FilteredArrayNames;

                }

                return results;
            }
        };
    }



    public static class ViewHolder {
        public TextView textView;
    }

}
