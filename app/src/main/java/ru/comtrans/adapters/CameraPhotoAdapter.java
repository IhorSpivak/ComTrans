package ru.comtrans.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraPhotoAdapter extends BaseAdapter {
    private ArrayList<PhotoItem> items;
    private Context context;
    int selectedPosition = 0; //needs to highlight first selected position
    int defectsCount = 1;

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
    }

    static class ViewHolder{
        public TextView title;
        public ImageView photo;
        public ImageView addPhoto;
        public ImageView imgDefect;
        public RelativeLayout photoLayout;
    }

    @Override
    public int getCount() {
        return items.size();
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
            items.add(0,new PhotoItem(String.format(context.getString(R.string.defect_n),defectsCount),true));
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

    public int getLastDefectPosition(){
        for(int i=0; i<items.size();i++){
            if(items.get(i).isDefect()&&items.get(i).getImagePath()==null){
                return i;
            }
        }
        return 0;
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

//    public void addItem(PhotoItem item) {
//        items.add(0,item);
//        selectedPosition++;
//        notifyDataSetChanged();
//    }


    public int getPhotosCount(){
        int count = 0;
        for(PhotoItem item:items){
            if(item.getImagePath()!=null&&!item.isDefect())
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
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
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

        viewHolder.title.setText(item.getTitle());

        if(item.getImagePath()!=null) {
            viewHolder.addPhoto.setVisibility(View.GONE);
            viewHolder.photo.setVisibility(View.VISIBLE);
            Log.d("TAG",item.getImagePath());

            Ion.with(viewHolder.photo).load(item.getImagePath());

//            try {
//                int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
//                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(item.getImagePath()), value, value);
//
//                Matrix matrix = new Matrix();
//                matrix.postRotate(270);
//                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//                        matrix, true);
//                bitmap.recycle();
//                viewHolder.photo.setImageBitmap(rotatedBitmap);
//            }catch (NullPointerException e){
//                e.printStackTrace();
//                //if photo has been deleted in other app
//            }
        }else {
            viewHolder.addPhoto.setVisibility(View.VISIBLE);
            viewHolder.photo.setVisibility(View.INVISIBLE);
        }
      //  viewHolder.txtItem.setText(getItem(position));

        return convertView;
    }
}
