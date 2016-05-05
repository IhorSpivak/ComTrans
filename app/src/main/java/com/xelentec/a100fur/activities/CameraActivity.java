package com.xelentec.a100fur.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xelentec.a100fur.R;
import com.xelentec.a100fur.fragments.CameraFragment;

public class CameraActivity extends AppCompatActivity {
    private Button capture;
    private CameraFragment cameraFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        cameraFragment = new CameraFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, cameraFragment).commit();

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);


    }

    View.OnClickListener captureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // cameraFragment.getCamera().takePicture(null, null, mPicture);
        }
    };



}
