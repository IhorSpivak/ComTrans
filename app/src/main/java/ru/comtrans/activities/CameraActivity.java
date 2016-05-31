package ru.comtrans.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.fragments.CameraFragment;
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

//        View decorView = getWindow().getDecorView();
//// Hide both the navigation bar and the status bar.
//// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//// a general rule, you should design your app to hide the status bar whenever you
//// hide the navigation bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//
//        decorView.setSystemUiVisibility(uiOptions);
        final ArrayList<PhotoItem> items = new ArrayList<>();
        titles = getResources().getStringArray(R.array.photo_general);
        for (int i=titles.length-1; i>=0; i--) {
            items.add(new PhotoItem(titles[i]));
        }
        photoAdapter = new CameraPhotoAdapter(items,CameraActivity.this);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new CameraFragment()).commit();
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
