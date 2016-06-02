package ru.comtrans.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.fragments.CameraFragment;
import ru.comtrans.fragments.VideoFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.PhotoItem;


public class CameraActivity extends AppCompatActivity {

    private CameraPhotoAdapter photoAdapter;
    String[] titles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        int flag = getIntent().getIntExtra(Const.CAMERA_MODE,0);
        Fragment fragment = null;
        final ArrayList<PhotoItem> items = new ArrayList<>();
        switch (flag){
            case 0:
                finish();
                break;
            case Const.MODE_PHOTO:
                titles = getResources().getStringArray(R.array.photo_general);
                for (int i=titles.length-1; i>=0; i--) {
                    if(i==titles.length-1){
                        PhotoItem defectItem = new PhotoItem(String.format(getString(R.string.defect_n),1));
                        defectItem.setDefect(true);
                        items.add(defectItem);
                    }
                    PhotoItem item = new PhotoItem(titles[i]);
                    items.add(item);

                }
                photoAdapter = new CameraPhotoAdapter(items,CameraActivity.this);
                fragment = new CameraFragment();
                 break;
            case Const.MODE_VIDEO:

                titles = getResources().getStringArray(R.array.video_functionality_test);
                for (int i=titles.length-1; i>=0; i--) {
                    items.add(new PhotoItem(titles[i]));
                }
                photoAdapter = new CameraPhotoAdapter(items,CameraActivity.this);
                fragment = new VideoFragment();
                break;

        }


        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }

    public CameraPhotoAdapter getPhotoAdapter() {
        return photoAdapter;
    }


    public void updatePositionInAdapter(int position){
        photoAdapter.setSelectedPosition(position);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
