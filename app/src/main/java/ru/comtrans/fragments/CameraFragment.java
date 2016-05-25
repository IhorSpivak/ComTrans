package ru.comtrans.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.camera.CameraPreview;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment {
    ListView listView;
    Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_camera,container,false);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        listView = (ListView)v.findViewById(android.R.id.list);
        ArrayList<PhotoItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new PhotoItem());
        }
        CameraPhotoAdapter photoAdapter = new CameraPhotoAdapter(items,getActivity());
        listView.setAdapter(photoAdapter);

        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,new CameraPreviewFragment()).commit();


        return v;
    }



}
