package ru.comtrans.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;

import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 13.07.2016.
 */
public class PhotoContainerAdapter extends RecyclerView.Adapter<PhotoContainerAdapter.PhotoViewHolder> {
    Context c;
    ArrayList<PhotoItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PhotoItem item, View view);
    }

    public PhotoContainerAdapter(Context c, ArrayList<PhotoItem> items, OnItemClickListener listener){
        this.c = c;
        this.items = items;
        this.listener = listener;
    }
    @Override
    public PhotoContainerAdapter.PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_item_recycler_view, parent, false);

                return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoContainerAdapter.PhotoViewHolder holder, int position) {
        PhotoItem item = getItem(position);
        holder.title.setText(item.getTitle());
        holder.bind(item,listener);
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public RelativeLayout photoLayout;



        public PhotoViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            photoLayout = (RelativeLayout)itemView.findViewById(R.id.photo_layout);
        }

        public void bind(final PhotoItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });
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
