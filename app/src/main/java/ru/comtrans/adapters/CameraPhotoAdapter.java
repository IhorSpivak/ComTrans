package ru.comtrans.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        public ImageView photo;
        public ImageView addPhoto;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PhotoItem getItem(int position) {
        return items.get(position);
    }

    public void addItem(PhotoItem item) {
        items.add(item);
        notifyDataSetChanged();
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
           // viewHolder.title = (TextView) convertView.findViewById(R.id.photo_title);
            viewHolder.photo  = (ImageView)convertView.findViewById(R.id.img_photo);
            viewHolder.addPhoto = (ImageView)convertView.findViewById(R.id.img_add_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(item.getImagePath()!=null) {
            viewHolder.addPhoto.setVisibility(View.GONE);
            viewHolder.photo.setVisibility(View.VISIBLE);

            try {
                int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(item.getImagePath()), value, value);

                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
                bitmap.recycle();
                viewHolder.photo.setImageBitmap(rotatedBitmap);
            }catch (NullPointerException e){
                e.printStackTrace();
                //if photo has been deleted in other app
            }
        }else {
            viewHolder.addPhoto.setVisibility(View.VISIBLE);
            viewHolder.photo.setVisibility(View.INVISIBLE);
        }
      //  viewHolder.txtItem.setText(getItem(position));

        return convertView;
    }
}
