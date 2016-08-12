package ru.comtrans.activities;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.fragments.CameraFragment;
import ru.comtrans.fragments.VideoFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.singlets.InfoBlockHelper;


public class CameraActivity extends AppCompatActivity {

    private CameraPhotoAdapter photoAdapter;
    ArrayList<PhotoItem> items;
    public int position;
    public int imagePosition;
    public  int screenNum;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        int flag = getIntent().getIntExtra(Const.CAMERA_MODE,-1);
        screenNum = getIntent().getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
        position = getIntent().getIntExtra(Const.EXTRA_POSITION,-1);
        imagePosition = getIntent().getIntExtra(Const.EXTRA_IMAGE_POSITION,-1);

        InfoBlockHelper helper = InfoBlockHelper.getInstance();
        items = helper.getItems().get(screenNum).get(position).getPhotoItems();




        switch (flag){
            case 0:
                finish();
                break;
            case Const.MODE_PHOTO:
                checkCameraPermission(false);
                 break;
            case Const.MODE_VIDEO:
                checkCameraPermission(true);
                break;

        }



    }

    public CameraPhotoAdapter getPhotoAdapter() {
        return photoAdapter;
    }



    @TargetApi(Build.VERSION_CODES.M)
    private void checkCameraPermission(boolean isVideo){
        int hasCameraPermission;
        int hasStoragePermission;
        int hasRecorderPermission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            hasRecorderPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            List<String> permissions = new ArrayList<>();
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(isVideo){
                if(hasRecorderPermission!= PackageManager.PERMISSION_GRANTED){
                    permissions.add(Manifest.permission.RECORD_AUDIO);
                }
            }
            if (!permissions.isEmpty()) {
                if(isVideo){
                    requestPermissions(permissions.toArray(new String[permissions.size()]),
                            Const.REQUEST_PERMISSION_VIDEO);
                }else {
                    requestPermissions(permissions.toArray(new String[permissions.size()]),
                            Const.REQUEST_PERMISSION_CAMERA);
                }

            } else {
                openCameraFragment(isVideo);
            }
        } else {
            openCameraFragment(isVideo);
        }
    }

    private void openCameraFragment(boolean isVideo){
        Fragment fragment;
        PhotoItem item = items.get(imagePosition);
        Collections.reverse(items);
        if(isVideo){
            imagePosition = items.indexOf(item);
            photoAdapter = new CameraPhotoAdapter(items,CameraActivity.this);
            fragment = new VideoFragment();
        }else {
            imagePosition = items.indexOf(item);
            photoAdapter = new CameraPhotoAdapter(items,CameraActivity.this);
            fragment = new CameraFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commitAllowingStateLoss();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allow = true;
        switch (requestCode){
            case Const.REQUEST_PERMISSION_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        allow = false;
                    }
                }
                if(allow) {
                    openCameraFragment(false);
                }
                else {
                    Toast.makeText(CameraActivity.this, R.string.not_all_permissions_granted, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case Const.REQUEST_PERMISSION_VIDEO:
                for (int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        allow = false;
                    }
                }
                if(allow) {
                    openCameraFragment(true);
                }
                else {
                    Toast.makeText(CameraActivity.this, R.string.not_all_permissions_granted, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
