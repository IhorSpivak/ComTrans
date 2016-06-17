package ru.comtrans.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.GalleryActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 26.05.2016.
 */
public class PhotoViewerFragment extends Fragment implements View.OnClickListener {
    private ImageView photoView;
    private ImageView rePhoto;
    private ImageView deletePhoto;
    private ImageView fullScreenPhoto;
    private PhotoItem item;
    private CameraActivity activity;
    int selectedPosition;
    File imgFile;

    public static PhotoViewerFragment newInstance(PhotoItem item, int selectedPosition) {
        Bundle args = new Bundle();
        args.putParcelable(Const.EXTRA_PHOTO_ITEM,item);
        args.putInt(Const.EXTRA_SELECTED_POSITION,selectedPosition);
        PhotoViewerFragment fragment = new PhotoViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoItem getItem() {
        return item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_viewer,container,false);

        activity = (CameraActivity)getActivity();

        photoView = (ImageView)v.findViewById(R.id.img_photo);
        rePhoto = (ImageView)v.findViewById(R.id.re_photo);
        fullScreenPhoto = (ImageView)v.findViewById(R.id.photo_full_screen);
        deletePhoto = (ImageView)v.findViewById(R.id.delete_photo);

        rePhoto.setOnClickListener(this);
        fullScreenPhoto.setOnClickListener(this);
        deletePhoto.setOnClickListener(this);
        photoView.setOnClickListener(this);

        item = getArguments().getParcelable(Const.EXTRA_PHOTO_ITEM);
        selectedPosition = getArguments().getInt(Const.EXTRA_SELECTED_POSITION);

        imgFile = new File(item.getImagePath());
        if(imgFile.exists()){
            photoView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }


        return v;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){

            case R.id.re_photo:
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Const.RE_PHOTO));
                break;
            case R.id.photo_full_screen:
                i = new Intent(getActivity(), GalleryActivity.class);
                i.putExtra(Const.EXTRA_PHOTO_ITEM,item);
                startActivityForResult(i,Const.GALLERY_RESULT);
                break;
            case R.id.img_photo:
                i = new Intent(getActivity(), GalleryActivity.class);
                i.putExtra(Const.EXTRA_PHOTO_ITEM,item);
                startActivityForResult(i,Const.GALLERY_RESULT);
                break;
            case R.id.delete_photo:
                deletePhotoAndClose();
                break;


        }
    }


    private void deletePhotoAndClose(){
        if(imgFile.delete()){
            if(!item.isDefect()) {
                item.setImagePath(null);
                activity.getPhotoAdapter().setItem(item, selectedPosition);
            }else {
                activity.getPhotoAdapter().removeItem(selectedPosition);
            }
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Const.RECEIVE_UPDATE_COUNT));
            Toast.makeText(getActivity(),R.string.photo_deleted,Toast.LENGTH_SHORT).show();
        //    replaceWithCamera();
        }else {
            Toast.makeText(getActivity(),R.string.photo_not_deleted,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Const.GALLERY_RESULT){
            switch (data.getIntExtra(Const.GALLERY_RESULT_STRING,0)){
                case Const.GALLERY_RESULT_RE_PHOTO:
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Const.RE_PHOTO));
                    break;
                case Const.GALLERY_RESULT_DELETE:
                    deletePhotoAndClose();
                    break;
            }
        }
    }
}
