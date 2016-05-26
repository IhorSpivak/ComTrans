package ru.comtrans.fragments;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment implements View.OnClickListener{
    ListView listView;
    Toolbar toolbar;
    ImageView takePhoto, takeDefect, done;
    CameraPreviewFragment cameraPreviewFragment;
    PhotoFragment photoFragment;
    CameraPhotoAdapter photoAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera,container,false);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        takeDefect = (ImageView) v.findViewById(R.id.take_defect);
        takePhoto = (ImageView) v.findViewById(R.id.take_photo);
        done = (ImageView) v.findViewById(R.id.btn_done);

        takeDefect.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        done.setOnClickListener(this);



        listView = (ListView)v.findViewById(android.R.id.list);
        ArrayList<PhotoItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new PhotoItem());
        }
        photoAdapter = new CameraPhotoAdapter(items,getActivity());
        listView.setAdapter(photoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoItem photoItem = photoAdapter.getItem(position);
                if(photoItem.getImagePath()==null){
                    if(getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW)==null)
                         replaceWithCamera();
                }else {
                    if(photoFragment != null
                            &&getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER)!=null
                            &&!photoFragment.getItem().getImagePath().equals(photoItem.getImagePath())){
                        replaceWithPhotoViewer(photoItem);
                    }else if(photoFragment==null){
                        replaceWithPhotoViewer(photoItem);
                    }

                }
            }
        });

        replaceWithCamera();


        return v;
    }

    private void replaceWithCamera(){
        cameraPreviewFragment = new CameraPreviewFragment();
        photoFragment = null;
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,cameraPreviewFragment, Const.CAMERA_PREVIEW).commit();
    }
    private void replaceWithPhotoViewer(PhotoItem item){
        photoFragment = PhotoFragment.newInstance(item);
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,photoFragment,Const.PHOTO_VIEWER).commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_defect:
                break;
            case R.id.take_photo:
                takePhoto.setEnabled(false);
                try {
                    cameraPreviewFragment.getCamera().takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            try {
                                File directory = new File(Environment.getExternalStorageDirectory(),"/Comtrans/photos/");
                                if(directory.mkdirs()||directory.exists()){
                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                                    File photoFile = new File(directory, "comtans" + timeStamp + ".jpg");
                                    FileOutputStream fos = new FileOutputStream(photoFile);
                                    fos.write(data);
                                    fos.close();
                                    PhotoItem item = new PhotoItem();
                                    item.setDefect(false);
                                    item.setImagePath(photoFile.getAbsolutePath());
                                    photoAdapter.addItem(item);
                                    replaceWithPhotoViewer(item);
                                }else {
                                    Toast.makeText(getActivity(),R.string.photo_save_error,Toast.LENGTH_SHORT).show();
                                }

                                takePhoto.setEnabled(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btn_done:
                break;

        }
    }
}
