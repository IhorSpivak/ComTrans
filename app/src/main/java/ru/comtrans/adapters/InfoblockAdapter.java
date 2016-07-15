package ru.comtrans.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;

import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 12.07.2016.
 */
public class InfoBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MainItem> items;
    Context c;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MainItem item, View view);
    }

    public InfoBlockAdapter(Context c, ArrayList<MainItem> items, OnItemClickListener listener){
        this.c = c;
        this.items = items;
        this.listener = listener;
    }





    private static class CustomViewHolder extends RecyclerView.ViewHolder{


        public CustomViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(final MainItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });
        }
    }

    private static class ListViewHolder extends CustomViewHolder{
        public TextView title;
        public TextView tvList;

        public ListViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_title);
            tvList = (TextView) itemView.findViewById(R.id.tv_list);

        }

    }

    private static class BottomBarViewHolder extends CustomViewHolder{
        public Button btnPrevious,btnNext;


        public BottomBarViewHolder(View itemView) {
            super(itemView);
            btnNext = (Button) itemView.findViewById(R.id.btn_next);
            btnPrevious = (Button) itemView.findViewById(R.id.btn_previous);
        }

    }

    private static class EditTextViewHolder extends CustomViewHolder{
        public EditText editText;
        public TextInputLayout textInputLayout;


        public EditTextViewHolder(View itemView) {
            super(itemView);
            textInputLayout = (TextInputLayout)itemView.findViewById(R.id.text_input_layout);
            editText = (EditText) itemView.findViewById(R.id.edit_text);
        }

    }

    private static class HeaderViewHolder extends CustomViewHolder{
        public TextView tvHeader;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        }

    }

    private static class FlagViewHolder extends CustomViewHolder{
        public CheckBox checkBox;


        public FlagViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
        }

    }

    private static class PhotoViewHolder extends CustomViewHolder{
        public RecyclerView list;
        public ProgressBar progressBar;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            list = (RecyclerView) itemView.findViewById(android.R.id.list);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }


    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case MainItem.TYPE_LIST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ListViewHolder(v);
            case MainItem.TYPE_STRING:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_text_item, parent, false);

                return new EditTextViewHolder(v);
            case MainItem.TYPE_NUMBER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_text_item, parent, false);
                EditText editText = (EditText) v.findViewById(R.id.edit_text);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                return new EditTextViewHolder(v);
            case MainItem.TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);

            case MainItem.TYPE_FLAG:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.flag_item, parent, false);

                return new FlagViewHolder(v);

            case MainItem.TYPE_PHOTO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_container_recycler_view, parent, false);

                return new PhotoViewHolder(v);

            case MainItem.TYPE_BOTTOM_BAR:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.next_list_item, parent, false);

                return new BottomBarViewHolder(v);

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MainItem item = getItem(position);



        switch (getItemViewType(position)){
            case MainItem.TYPE_LIST:
                ((ListViewHolder)holder).title.setText(item.getName());
                ((ListViewHolder)holder).tvList.setText(item.getValues().get(0).getName());

                break;


            case MainItem.TYPE_PHOTO:
                LinearLayoutManager manager = new LinearLayoutManager(c,LinearLayoutManager.HORIZONTAL,false);
                ((PhotoViewHolder)holder).list.setLayoutManager(manager);
                PhotoContainerAdapter photoContainerAdapter = new PhotoContainerAdapter(c, item.getPhotoItems(), new PhotoContainerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PhotoItem item, View view) {
                        Intent i = new Intent(view.getContext(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_PHOTO);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,getItem(position).getPhotoItems());
                        view.getContext().startActivity(i);
                    }
                });
                ((PhotoViewHolder)holder).list.setAdapter(photoContainerAdapter);


                break;

            case MainItem.TYPE_STRING:
                ((EditTextViewHolder)holder).textInputLayout.setHint(item.getName());
                break;

            case MainItem.TYPE_NUMBER:
                ((EditTextViewHolder)holder).textInputLayout.setHint(item.getName());
                break;

            case MainItem.TYPE_HEADER:
                ((HeaderViewHolder)holder).tvHeader.setText(item.getName());
                break;

            case MainItem.TYPE_FLAG:
                ((FlagViewHolder)holder).checkBox.setText(item.getName());
                break;



        }
        ((CustomViewHolder)holder).bind(item,listener);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public MainItem getItem(int position){
        return items.get(position);
    }
}
