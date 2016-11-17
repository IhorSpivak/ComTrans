package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;

import ru.comtrans.R;
import ru.comtrans.helpers.Utility;


public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        Intent i;
        if(Utility.getToken().equals("")) {
            i = new Intent(LaunchActivity.this, LoginActivity.class);
        }else {
            i = new Intent(LaunchActivity.this, MainActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
