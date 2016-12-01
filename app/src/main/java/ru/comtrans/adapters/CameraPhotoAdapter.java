package ru.comtrans.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.views.VerticalTextView;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraPhotoAdapter extends BaseAdapter {
    private ArrayList<PhotoItem> items;
    private Context context;
    int selectedPosition = 0; //needs to highlight first selected position
    private int defectsCount = 1;

    public ArrayList<PhotoItem> getItems() {
        return items;
    }


    public void setDefectsCount(int defectsCount) {
        this.defectsCount = defectsCount;
    }

    public int getDefectsCount() {
        return defectsCount;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public CameraPhotoAdapter(ArrayList<PhotoItem> items, Context context){
        this.items = items;
        this.context = context;
        this.selectedPosition = items.size()-1;
        defectsCount = getFactDefectCount()+1;
    }

    static class ViewHolder{
        public VerticalTextView title;
        public ImageView photo;
        public ImageView addPhoto;
        public ImageView imgDefect;
        public RelativeLayout photoLayout;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public boolean isImagePathNull(int position){
        return items.get(position).getImagePath()!=null;
    }

    @Override
    public PhotoItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(PhotoItem item,int position) {
        items.remove(position);
        items.add(position,item);

        if(item.isDefect()){
            selectedPosition++;
            defectsCount++;


        items.add(0,new PhotoItem(String.format(context.getString(R.string.default_defect_name)
                ,defectsCount),true));
        }
        notifyDataSetChanged();
    }


    public void setTitleForItem(PhotoItem item,int position) {
        items.remove(position);
        items.add(position,item);
        notifyDataSetChanged();
    }

    public void setImagePathForItem(PhotoItem item,int position) {
        items.remove(position);
        items.add(position,item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        items.remove(position);
        notifyDataSetChanged();
    }


    public int getFactDefectCount(){
        int count = 0;
        for(int i=0; i<items.size(); i++){
            if(items.get(i).isDefect()&&items.get(i).getImagePath()!=null){
                count++;
            }
        }
        return count;
    }

    public boolean isPositionDefect(int position){
        if(items.get(position).isDefect()){
            return true;
        }else {
            return false;
        }
    }



    public int getPhotosCount(){
        int count = 0;
        for(PhotoItem item:items){
            if(item.getImagePath()!=null&&!item.isDefect())
                count++;
        }
        return count;
    }

    public int getNonDefectPhotosCount(){
        int count = 0;
        for(PhotoItem item:items){
            if(!item.isDefect())
                count++;
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        PhotoItem item = getItem(position);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.photo_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (VerticalTextView) convertView.findViewById(R.id.title);
            viewHolder.photo  = (ImageView)convertView.findViewById(R.id.img_photo);
            viewHolder.addPhoto = (ImageView)convertView.findViewById(R.id.img_add_photo);
            viewHolder.imgDefect = (ImageView)convertView.findViewById(R.id.img_defect);
            viewHolder.photoLayout = (RelativeLayout)convertView.findViewById(R.id.photo_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(selectedPosition==position){
            viewHolder.photoLayout.setBackgroundResource(R.drawable.photo_selected_background);
        }else {
            viewHolder.photoLayout.setBackgroundResource(R.drawable.photo_not_selected_background);
        }

        if(item.isDefect()){
            viewHolder.imgDefect.setVisibility(View.VISIBLE);
        }else {
            viewHolder.imgDefect.setVisibility(View.GONE);
        }


        if(item.getTitle().length()>9){
            viewHolder.title.setText(item.getTitle().substring(0,9)+"...");
        }else {
            viewHolder.title.setText(item.getTitle());
        }
        Log.d("TAG",item.getTitle());


        if(item.getImagePath()!=null) {
            File file = new File(item.getImagePath());
            if(file.exists()) {
                viewHolder.addPhoto.setVisibility(View.GONE);
                viewHolder.photo.setVisibility(View.VISIBLE);
                Log.d("TAG", item.getImagePath());

                Ion.with(viewHolder.photo).load(item.getImagePath());
            }else {
                item.setImagePath(null);
                notifyDataSetChanged();
            }


        }else {
            viewHolder.addPhoto.setVisibility(View.VISIBLE);
            viewHolder.photo.setVisibility(View.INVISIBLE);
        }
      //  viewHolder.txtItem.setText(getItem(position));

        return convertView;
    }


}
