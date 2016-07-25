package ru.comtrans.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.MyInfoBlockItem;

/**
 * Created by Artco on 25.07.2016.
 */
public class MyInfoBlocksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MyInfoBlockItem item, int position);
    }

    private ArrayList<MyInfoBlockItem> items;
    Context context;
    private final OnItemClickListener listener;

    public MyInfoBlocksAdapter(Context context, ArrayList<MyInfoBlockItem> items, MyInfoBlocksAdapter.OnItemClickListener listener){
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_infoblock_item, parent, false);
        return new MyInfoBlockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyInfoBlockItem item = getItem(position);
        ((MyInfoBlockViewHolder)holder).date.setText(item.getDate());
        ((MyInfoBlockViewHolder)holder).status.setText(item.getStatus());
        ((MyInfoBlockViewHolder)holder).mark.setText(item.getMark());
        ((MyInfoBlockViewHolder)holder).model.setText(item.getModel());
        ((MyInfoBlockViewHolder)holder).year.setText(item.getYear());
        Ion.with(((MyInfoBlockViewHolder)holder).photo).load(item.getPhotoPath());
        ((MyInfoBlockViewHolder)holder).bind(item,listener);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public MyInfoBlockItem getItem(int position){
        return items.get(position);
    }

    private static class MyInfoBlockViewHolder extends RecyclerView.ViewHolder{
        public TextView date,status,mark,model,year;
        public ImageView photo;

        public MyInfoBlockViewHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.info_block_date);
            status = (TextView)itemView.findViewById(R.id.info_block_status);
            mark = (TextView)itemView.findViewById(R.id.info_block_mark);
            model = (TextView)itemView.findViewById(R.id.info_block_model);
            year = (TextView)itemView.findViewById(R.id.info_block_year);
            photo = (ImageView)itemView.findViewById(R.id.info_block_image);

        }

        public void bind(final MyInfoBlockItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item,getAdapterPosition());
                }
            });
        }
    }


}
