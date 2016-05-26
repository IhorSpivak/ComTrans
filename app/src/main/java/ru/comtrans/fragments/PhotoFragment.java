package ru.comtrans.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.BitmapCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 26.05.2016.
 */
public class PhotoFragment extends Fragment {
    private ImageView photoView;
    private PhotoItem item;

    public static PhotoFragment newInstance(PhotoItem item) {

        Bundle args = new Bundle();
        args.putParcelable(Const.EXTRA_PHOTO_ITEM,item);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoItem getItem() {
        return item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo,container,false);

        photoView = (ImageView)v.findViewById(R.id.img_photo);
        item = getArguments().getParcelable(Const.EXTRA_PHOTO_ITEM);

        File imgFile = new File(item.getImagePath());
        if(imgFile.exists()){
            photoView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }


        return v;
    }
}
