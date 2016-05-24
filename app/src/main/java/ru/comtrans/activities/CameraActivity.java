package ru.comtrans.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ru.comtrans.R;
import ru.comtrans.fragments.CameraFragment;


public class CameraActivity extends AppCompatActivity {

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

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new CameraFragment()).commit();
    }

}
