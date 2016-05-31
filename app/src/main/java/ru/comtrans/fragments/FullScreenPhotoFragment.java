package ru.comtrans.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Artco on 31.05.2016.
 */
public class FullScreenPhotoFragment extends Fragment {
    ImageView imgPhoto;
    PhotoItem item;
    File imgFile;
    PhotoViewAttacher mAttacher;


    public static FullScreenPhotoFragment newInstance(PhotoItem item) {
        Bundle args = new Bundle();
        args.putParcelable(Const.EXTRA_PHOTO_ITEM,item);
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_full_screen_photo,container,false);

        item = getArguments().getParcelable(Const.EXTRA_PHOTO_ITEM);
        imgPhoto = (ImageView)v.findViewById(R.id.img_photo);
        imgFile = new File(item.getImagePath());
        if(imgFile.exists()){
            imgPhoto.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }

        mAttacher = new PhotoViewAttacher(imgPhoto);
        return v;
    }
}
