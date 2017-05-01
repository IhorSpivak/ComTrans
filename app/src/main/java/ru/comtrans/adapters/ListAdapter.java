package ru.comtrans.adapters;

import android.content.Context;
import android.util.Log;
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
import java.util.List;
import java.util.Map;

import ru.comtrans.R;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.IdsRelationHelperItem;
import ru.comtrans.items.ListItem;

public class ListAdapter extends BaseAdapter implements Filterable {


    private ArrayList<ListItem> mData = new ArrayList<>();
    private ArrayList<ListItem> items = new ArrayList<>();
    //    private long mark;
    private IdsRelationHelperItem idsRelationHelperItem;
    private boolean isNeedSort;
    private long propCode;


    private LayoutInflater mInflater;

    public ListAdapter(Context context, ArrayList<ListItem> items, IdsRelationHelperItem idsRelationHelperItem, boolean isNeedSort, long propCode) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.items = items;
        this.idsRelationHelperItem = idsRelationHelperItem;
        this.isNeedSort = isNeedSort;
        this.propCode = propCode;
        enterValues(items);
    }

    public void enterValues(ArrayList<ListItem> items) {
        mData.clear();
        switch (idsRelationHelperItem.getCode()) {
            case IdsRelationHelperItem.CODE_GENERAL_TYPE_ID:
                filterByCategory();
                break;
            case IdsRelationHelperItem.CODE_GENERAL_MARK:
                filterMark();
                break;
            case IdsRelationHelperItem.CODE_GENERAL_MODEL:
                //todo type_link

                filterByMark();
                break;
            case IdsRelationHelperItem.CODE_TEC_ENGINE_MARK:
                //todo type_link

                filterByMark();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL:
                filterByModel();
                break;
//            case IdsRelationHelperItem.CODE_GENERAL_TYPE_ID:
//                filterByModel();
//                break;
            case IdsRelationHelperItem.CODE_TEC_ENGINE_POWER:
                filterByEngineModel();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_ENGINE_TYPE:
                filterByEngineModel();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_ENGINE_VOLUME:
                filterByEngineModel();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_ECO_CLASS:
                filterByEngineModel();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_SHAS_CAPACITY_TOTAL:
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_SHAS_WHEEL_FORMULA:
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_MARK_KPP:
                filterByMark();
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_MODEL_KPP:
                filterByModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_KPP_GEARS:
                filterByKppModel();
                break;
            case IdsRelationHelperItem.CODE_TEC_KPP_TYPE:
                filterByKppModel();
                break;
            case IdsRelationHelperItem.CODE_FORM_ORGANIZATION:
                filterByVehicleOwner();
                break;
            case IdsRelationHelperItem.CODE_VEHICLE_OWNER:
                break;
            case IdsRelationHelperItem.TYPE_OF_INSPECTION:
                checkingTypeIspection();
                break;
            default:
                break;
        }
        if (getCount() <= 1) {
            addAllItemsToTempArray();
        }
        notifyDataSetChanged();
    }

    private void checkingTypeIspection() {

    }

    //Filters

    private void filterByCategory() {
        List<Map<String, List<String>>> categories = Utility.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Map<String, List<String>> map = categories.get(i);
            List<String> transportTypeIds = map.get(String.valueOf(propCode));
            if (transportTypeIds != null && !transportTypeIds.isEmpty()) {
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getId() == -1 || transportTypeIds.contains(String.valueOf(items.get(j).getId()))) {
                        addItemToTempArray(items.get(j));
                    }
                }
            }
        }

    }

    private void filterMark(){
        for (ListItem item : items) {
            if (item.getId() == -1 || isValidMark(item.getName())) {
                addItemToTempArray(item);
            }
        }
    }

    private boolean isValidMark(String name) {
        List<String> serverMarkNamesErrors = new ArrayList<String>() {{
            add("");
            add("1");
            add("2");
            add("ae95d3c7");
            add("e13a32a1");
            add("947d6a9f");
            add("bcb48ddd");
        }};

        if (serverMarkNamesErrors.contains(name)) {
            return false;
        } else {
            return true;
        }
    }



    //||   != "ae95d3c7" ||  "e13a321"
    private void filterByMark(){
        for (ListItem item : items) {
            Log.e("TMP_TEST", "item.getMark()=" + item.getMark());
            if (item.getId() == -1 || item.getMark() == idsRelationHelperItem.getMark())  {
                addItemToTempArray(item);
            }
        }
    }

    private void filterByModel(){
        for (ListItem item : items) {
            Log.e("TMP_TEST", "item.getModel()=" + item.getModel());
            boolean isAdd = false;
            if(item.getId() == -1)
                isAdd = true;
            if (item.getModel() != null && item.getModel().size() != 0)
                for (int i = 0; i < item.getModel().size(); i++) {
                    if (item.getModel().get(i) == idsRelationHelperItem.getModel()) {
                        isAdd = true;
                        break;
                    }
                }
            if (isAdd)
                addItemToTempArray(item);
        }
    }

    private void filterByEngineModel(){
        for (ListItem item : items) {
            Log.e("TMP_TEST", "item.getEngineModel()=" + item.getEngineModel());
            boolean isAdd = false;
            if(item.getId() == -1)
                isAdd = true;
            if (item.getEngineModel() != null && item.getEngineModel().size() != 0)
                for (int i = 0; i < item.getEngineModel().size(); i++) {
                    if (item.getEngineModel().get(i) == idsRelationHelperItem.getEngineModel()) {
                        isAdd = true;
                        break;
                    }
                }
            if (isAdd)
                addItemToTempArray(item);
        }
    }

    private void filterByKppModel(){
        for (ListItem item : items) {
            Log.e("TMP_TEST", "item.getKppModel()=" + item.getKppModel());
            boolean isAdd = false;
            if(item.getId() == -1)
                isAdd = true;
            if (item.getKppModel() != null && item.getKppModel().size() != 0)
                for (int i = 0; i < item.getKppModel().size(); i++) {
                    if (item.getKppModel().get(i) == idsRelationHelperItem.getKppModel()) {
                        isAdd = true;
                        break;
                    }
                }
            if (isAdd)
                addItemToTempArray(item);
        }
    }

    private void filterByVehicleOwner(){
        for (ListItem item : items) {
            Log.e("TMP_TEST", "item.getVehicleOwner()=" + item.getVehicleOwner());
            boolean isAdd = false;
            if(item.getId() == -1)
                isAdd = true;
            if (item.getVehicleOwner() != null && item.getVehicleOwner().size() != 0)
                for (int i = 0; i < item.getVehicleOwner().size(); i++) {
                    if (item.getVehicleOwner().get(i) == idsRelationHelperItem.getVehicleOwner()) {
                        isAdd = true;
                        break;
                    }
                }
            if (isAdd)
                addItemToTempArray(item);
        }
    }

    public void addItemToTempArray(final ListItem item) {
        mData.add(item);
    }

    private void addAllItemsToTempArray() {
        mData.clear();
        mData.addAll(items);
    }

    public void addItem(ListItem item) {
        items.add(item);
        enterValues(items);
    }

    private void sortItems() {
        Collections.sort(mData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem s1, ListItem s2) {
                if (s1.getId() == -1) {
                    return 1;
                } else if (s2.getId() == -1) {
                    return 1;
                } else {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }

            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        if (isNeedSort)
            sortItems();
        super.notifyDataSetChanged();
    }

    public boolean containsValue(String value) {
        for (ListItem item :
                mData) {
            if (item.getName() != null && item.getName().equals(value)) {
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
            convertView = mInflater.inflate(R.layout.search_list_item, parent, false);
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
                        if (dataNames.toLowerCase().contains(searchCriteria)) {
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
