package ru.comtrans.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.SendingService;

/**
 * Created by Artco on 25.07.2016.
 */
public class MyInfoBlocksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MyInfoBlockItem item, int position);
    }

    private ArrayList<MyInfoBlockItem> items;
    private Context context;
    private final OnItemClickListener listener;
    private InfoBlocksStorage storage;

    public MyInfoBlocksAdapter(Context context, ArrayList<MyInfoBlockItem> items, MyInfoBlocksAdapter.OnItemClickListener listener){
        this.items = items;
        this.context = context;
        this.listener = listener;
        storage = InfoBlocksStorage.getInstance();
    }

    public void setItems(ArrayList<MyInfoBlockItem> items) {
        this.items = items;
        notifyDataSetChanged();
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
        MyInfoBlockViewHolder myHolder = ((MyInfoBlockViewHolder)holder);
        myHolder.date.setText(item.getDate());
        SimpleDateFormat df = new SimpleDateFormat(Const.INFO_BLOCK_FULL_DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat expectedFormat = new SimpleDateFormat(Const.INFO_BLOCK_DATE_FORMAT, Locale.getDefault());
        try {
            Date date = df.parse(item.getDate());
            String formattedDate = expectedFormat.format(date);
            myHolder.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        switch (storage.getInfoBlockStatus(item.getId())){
                case MyInfoBlockItem.STATUS_DRAFT:
                    myHolder.status.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                    myHolder.status.setText(R.string.status_draft);
                    break;
                case MyInfoBlockItem.STATUS_SENDING:
                    myHolder.status.setTextColor(ContextCompat.getColor(context,android.R.color.holo_blue_dark));
//                    myHolder.status.setText(String.format(context.getString(R.string.status_sending),item.getProgress()));
                    myHolder.status.setText(item.getProgress());
                    break;
                case MyInfoBlockItem.STATUS_SENT:
                    myHolder.status.setTextColor(ContextCompat.getColor(context,android.R.color.holo_green_dark));
                    myHolder.status.setText(R.string.status_sent);
                    break;
            }



        myHolder.mark.setText(item.getMark());
        myHolder.model.setText(item.getModel());
        myHolder.year.setText(item.getYear());
        if(item.getPhotoPath()!=null){
            Ion.with(myHolder.photo).load(item.getPhotoPath());
            myHolder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else {
            myHolder.photo.setScaleType(ImageView.ScaleType.CENTER);
            myHolder.photo.setImageResource(R.drawable.ic_placeholder);
        }



        ((MyInfoBlockViewHolder)holder).bind(item,listener);

    }

    public void updateProgress(String id,String progress){
        for (int i = 0; i < items.size(); i++) {
            MyInfoBlockItem item = items.get(i);
            if (item.getId().equals(id)){
                item.setStatus(storage.getInfoBlockStatus(item.getId()));
                item.setProgress(progress);
                items.set(i,item);
                notifyDataSetChanged();
            }
        }


    }

    public void saveProgress(){
        for (int i = 0; i < items.size(); i++) {
            if (storage.getInfoBlockStatus(items.get(i).getId()) == MyInfoBlockItem.STATUS_SENDING) {
                storage.updateInfoBlockProgress(storage.getPreviewItems().get(i).getId(), items.get(i).getProgress());
            }
        }
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
