package ru.comtrans.adapters;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;

import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 12.07.2016.
 */
public class InfoBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MainItem> items;
    Context context;
    private final OnItemClickListener listener;
    int page;
    DividerItemDecoration decoration;

    public interface OnItemClickListener {
        void onItemClick(MainItem item, View view,int position);
    }

    public InfoBlockAdapter(Context context, ArrayList<MainItem> items, int page, OnItemClickListener listener){
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.page = page;
    }

    public ArrayList<MainItem> getItems() {
        return items;
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder{


        public CustomViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(final MainItem item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item,v,getAdapterPosition());
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

    private static class TireSchemeViewHolder extends CustomViewHolder{
        public ImageView schemeImage;

        public TireSchemeViewHolder(View itemView) {
            super(itemView);
            schemeImage = (ImageView) itemView.findViewById(R.id.tire_scheme_image);

        }

    }

    private static class BottomBarViewHolder extends CustomViewHolder{
        public Button btnPrevious,btnNext;
        public RelativeLayout previousLayout, nextLayout;


        public BottomBarViewHolder(View itemView) {
            super(itemView);
            btnNext = (Button) itemView.findViewById(R.id.btn_next);
            btnPrevious = (Button) itemView.findViewById(R.id.btn_previous);
            previousLayout = (RelativeLayout)itemView.findViewById(R.id.previous_layout);
            nextLayout = (RelativeLayout)itemView.findViewById(R.id.next_layout);
        }

    }

    private static class EditTextViewHolder extends CustomViewHolder{
        public EditText editText;
        public TextInputLayout textInputLayout;
        public InfoBlockTextWatcher textWatcher;


        public EditTextViewHolder(View itemView,InfoBlockTextWatcher textWatcher) {
            super(itemView);
            textInputLayout = (TextInputLayout)itemView.findViewById(R.id.text_input_layout);
            editText = (EditText) itemView.findViewById(R.id.edit_text);
            this.textWatcher = textWatcher;
            this.editText.addTextChangedListener(textWatcher);
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
        public RecyclerView photoList;
        public RecyclerView defectsList;
        public ProgressBar progressBar;



        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoList = (RecyclerView) itemView.findViewById(R.id.list_photo);
            defectsList = (RecyclerView)itemView.findViewById(R.id.list_defects);
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

                return new EditTextViewHolder(v,new InfoBlockTextWatcher());
            case MainItem.TYPE_NUMBER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_text_item, parent, false);
                EditText editText = (EditText) v.findViewById(R.id.edit_text);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                return new EditTextViewHolder(v,new InfoBlockTextWatcher());
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

            case MainItem.TYPE_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_container_recycler_view, parent, false);

                return new PhotoViewHolder(v);

            case MainItem.TYPE_TIRE_SCHEME:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tire_scheme_item, parent, false);

                return new TireSchemeViewHolder(v);

            case MainItem.TYPE_BOTTOM_BAR:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bottom_bar_list_item, parent, false);

                return new BottomBarViewHolder(v);



            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MainItem item = getItem(position);
        PhotoContainerAdapter adapter;
        PhotoContainerAdapter defectsAdapter;
        int count;


        switch (getItemViewType(position)){
            case MainItem.TYPE_LIST:
                ((ListViewHolder)holder).title.setText(item.getName());
                ((ListViewHolder)holder).tvList.setText(item.getListValue().getName());

                break;


            case MainItem.TYPE_PHOTO:
                ArrayList<PhotoItem> photoItems = new ArrayList<>();
                ArrayList<PhotoItem> defects = new ArrayList<>();

                for(PhotoItem photoItem:item.getPhotoItems()){
                    if(!photoItem.isDefect()){
                        photoItems.add(photoItem);
                    }
                }

                LinearLayoutManager manager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
                ((PhotoViewHolder)holder).photoList.setLayoutManager(manager);
                 adapter = new PhotoContainerAdapter(context, photoItems, new PhotoContainerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PhotoItem item, View view) {
                        MainItem mainItem = getItem(holder.getAdapterPosition());
                       listener.onItemClick(mainItem,view,mainItem.getPhotoItems().indexOf(item));
                    }
                },MainItem.TYPE_PHOTO);
                ((PhotoViewHolder)holder).photoList.setAdapter(adapter);

                count = 0;
                for(PhotoItem photoItem:item.getPhotoItems()){
                    if(photoItem.getImagePath()!=null&&!photoItem.isDefect())
                        count++;
                }
                if(count!=0) {
                    int percent = (int)((count * 100.0f) / item.getPhotosCount());
                    if(percent==100){
                        ((PhotoViewHolder)holder).progressBar.setProgressDrawable(ContextCompat.getDrawable(context,R.drawable.progressbar_green));
                    }else {
                        ((PhotoViewHolder)holder).progressBar.setProgressDrawable(ContextCompat.getDrawable(context,R.drawable.progressbar_red));
                    }

                    ((PhotoViewHolder)holder).progressBar.setProgress(percent);
                }else {
                    ((PhotoViewHolder)holder).progressBar.setProgress(0);
                }



                for(PhotoItem photoItem:item.getPhotoItems()){
                    if(photoItem.isDefect()){
                        defects.add(photoItem);
                    }
                }

                if(defects.size()>0){
                    LinearLayoutManager defectsManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
                    ((PhotoViewHolder)holder).defectsList.setLayoutManager(defectsManager);
                    defectsAdapter = new PhotoContainerAdapter(context, defects, new PhotoContainerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(PhotoItem item, View view) {
                            MainItem mainItem = getItem(holder.getAdapterPosition());
                            listener.onItemClick(mainItem,view,mainItem.getPhotoItems().indexOf(item));
                        }
                    },MainItem.TYPE_PHOTO);
                    ((PhotoViewHolder)holder).defectsList.setAdapter(defectsAdapter);
                }




                break;

            case MainItem.TYPE_VIDEO:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
                ((PhotoViewHolder)holder).photoList.setLayoutManager(gridLayoutManager);
                adapter = new PhotoContainerAdapter(context, item.getPhotoItems(), new PhotoContainerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PhotoItem item, View view) {
                        MainItem mainItem = getItem(holder.getAdapterPosition());
                        listener.onItemClick(mainItem,view,mainItem.getPhotoItems().indexOf(item));
                    }
                },MainItem.TYPE_VIDEO);
                ((PhotoViewHolder)holder).photoList.setAdapter(adapter);
                if(decoration==null){
                    decoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL_LIST);
                    ((PhotoViewHolder)holder).photoList.addItemDecoration(decoration);
                }

                count = 0;
                for(PhotoItem photoItem:item.getPhotoItems()){
                    if(photoItem.getImagePath()!=null&&!photoItem.isDefect())
                        count++;
                }
                if(count!=0) {
                    int percent = (int)((count * 100.0f) / item.getPhotosCount());
                    if(percent==100){
                        ((PhotoViewHolder)holder).progressBar.setProgressDrawable(ContextCompat.getDrawable(context,R.drawable.progressbar_green));
                    }else {
                        ((PhotoViewHolder)holder).progressBar.setProgressDrawable(ContextCompat.getDrawable(context,R.drawable.progressbar_red));
                    }

                    ((PhotoViewHolder)holder).progressBar.setProgress(percent);
                }else {
                    ((PhotoViewHolder)holder).progressBar.setProgress(0);
                }



                break;

            case MainItem.TYPE_STRING:
                ((EditTextViewHolder)holder).textInputLayout.setHint(item.getName());
                ((EditTextViewHolder)holder).textWatcher.updatePosition(holder.getAdapterPosition());
                ((EditTextViewHolder)holder).editText.setText(item.getValue());
                break;

            case MainItem.TYPE_NUMBER:
                ((EditTextViewHolder)holder).textInputLayout.setHint(item.getName());
                ((EditTextViewHolder)holder).textWatcher.updatePosition(holder.getAdapterPosition());
                ((EditTextViewHolder)holder).editText.setText(item.getValue());
                break;

            case MainItem.TYPE_HEADER:
                ((HeaderViewHolder)holder).tvHeader.setText(item.getName());
                break;

            case MainItem.TYPE_FLAG:
                ((FlagViewHolder)holder).checkBox.setOnCheckedChangeListener(null);
                ((FlagViewHolder)holder).checkBox.setText(item.getName());
                ((FlagViewHolder)holder).checkBox.setChecked(item.isChecked());
                ((FlagViewHolder)holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        item.setChecked(b);
                    }
                });
                break;

           case MainItem.TYPE_BOTTOM_BAR:
               switch (page){
                   case 1:
                       ((BottomBarViewHolder)holder).previousLayout.setVisibility(View.GONE);
                       break;
                   case 6:
                       ((BottomBarViewHolder)holder).btnNext.setText(R.string.send_infoblock);
                       break;
               }
               ((BottomBarViewHolder)holder).btnNext.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       listener.onItemClick(item,view,2);
                   }
               });
               ((BottomBarViewHolder)holder).btnPrevious.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       listener.onItemClick(item,view,1);
                   }
               });
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

    private class InfoBlockTextWatcher implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            items.get(position).setValue(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
