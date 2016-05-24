package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.comtrans.R;


public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Intent i  = new Intent(LaunchActivity.this,LoginActivity.class);
        startActivity(i);


    }
}
