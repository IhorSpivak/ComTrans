package ru.comtrans.fragments;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 26.05.2016.
 */
public class VideoViewerFragment extends Fragment implements View.OnClickListener {
    private ImageView photoView;
    private ImageView rePhoto;
    private ImageView deletePhoto;
    private ImageView playVideo;
    private PhotoItem item;
    private CameraActivity activity;
    int selectedPosition;
    File imgFile;

    public static VideoViewerFragment newInstance(PhotoItem item, int selectedPosition) {
        Bundle args = new Bundle();
        args.putParcelable(Const.EXTRA_PHOTO_ITEM,item);
        args.putInt(Const.EXTRA_SELECTED_POSITION,selectedPosition);
        VideoViewerFragment fragment = new VideoViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoItem getItem() {
        return item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_viewer,container,false);

        activity = (CameraActivity)getActivity();

        photoView = (ImageView)v.findViewById(R.id.img_photo);
        rePhoto = (ImageView)v.findViewById(R.id.re_photo);
        deletePhoto = (ImageView)v.findViewById(R.id.delete_photo);
        playVideo = (ImageView)v.findViewById(R.id.play_video);

        rePhoto.setOnClickListener(this);
        deletePhoto.setOnClickListener(this);
        playVideo.setOnClickListener(this);

        item = getArguments().getParcelable(Const.EXTRA_PHOTO_ITEM);
        selectedPosition = getArguments().getInt(Const.EXTRA_SELECTED_POSITION);

        imgFile = new File(item.getImagePath());
        if(imgFile.exists()){
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgFile.getAbsolutePath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            photoView.setImageBitmap(thumb);
        }


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_photo:
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Const.RE_PHOTO));
                break;

            case R.id.delete_photo:
                deletePhotoAndClose();
                break;

            case R.id.play_video:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getImagePath()));
                Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,"1");
                intent.setDataAndType(Uri.parse(item.getImagePath()), "video/mp4");

                Intent chooser = Intent.createChooser(intent,"Открыть видео");
                startActivity(chooser);



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
            Toast.makeText(getActivity(),R.string.video_deleted,Toast.LENGTH_SHORT).show();
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Const.RECEIVE_UPDATE_COUNT));
        }else {
            Toast.makeText(getActivity(),R.string.video_not_deleted,Toast.LENGTH_SHORT).show();
        }
    }


}
