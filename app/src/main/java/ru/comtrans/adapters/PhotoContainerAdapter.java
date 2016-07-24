package ru.comtrans.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 13.07.2016.
 */
public class PhotoContainerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context c;
    ArrayList<PhotoItem> items;
    int type;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PhotoItem item, View view);
    }

    public PhotoContainerAdapter(Context c, ArrayList<PhotoItem> items, OnItemClickListener listener, int type){
        this.c = c;
        this.items = items;
        this.listener = listener;
        this.type =type;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (type){
            case MainItem.TYPE_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_item_recycler_view, parent, false);

                return new VideoViewHolder(v);
            case MainItem.TYPE_PHOTO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_item_recycler_view, parent, false);

                return new PhotoViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_item_recycler_view, parent, false);

                return new PhotoViewHolder(v);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isVideo()?MainItem.TYPE_VIDEO:MainItem.TYPE_PHOTO;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoItem item = getItem(position);
        switch (type){
            case MainItem.TYPE_PHOTO:
                ((PhotoViewHolder)holder).title.setText(item.getTitle());
                Ion.with(((PhotoViewHolder)holder).imageThumbnail).load(item.getImagePath());
                if(item.getImagePath()!=null){
                    ((PhotoViewHolder)holder).title.setTextColor(Color.WHITE);
                }else {
                    ((PhotoViewHolder)holder).title.setTextColor(Color.BLACK);
                }
                break;
            case MainItem.TYPE_VIDEO:
                ((VideoViewHolder)holder).title.setText(item.getTitle());
                if(item.getImagePath()!=null){
                    ((VideoViewHolder)holder).title.setTextColor(Color.WHITE);
                }else {
                    ((VideoViewHolder)holder).title.setTextColor(Color.BLACK);
                }
                Ion.with(((VideoViewHolder)holder).videoThumbnail).load(item.getImagePath());
                break;
        }

        ((CustomViewHolder)holder).bind(item,listener);


    }


    private static class CustomViewHolder extends RecyclerView.ViewHolder{


        public CustomViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(final PhotoItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });
        }
    }

    public static class PhotoViewHolder extends CustomViewHolder{
        public TextView title;
        public RelativeLayout photoLayout;
        public ImageView imageThumbnail;



        public PhotoViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            photoLayout = (RelativeLayout)itemView.findViewById(R.id.photo_layout);
            imageThumbnail = (ImageView)itemView.findViewById(R.id.image);
        }

    }

    public static class VideoViewHolder extends CustomViewHolder{
        public TextView title;
        public RelativeLayout videoLayout;
        public ImageView videoThumbnail;



        public VideoViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.tv_video_title);
            videoLayout = (RelativeLayout)itemView.findViewById(R.id.video_layout);
            videoThumbnail = (ImageView)itemView.findViewById(R.id.img_video_thumb);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public PhotoItem getItem(int position){
        return items.get(position);
    }
}
