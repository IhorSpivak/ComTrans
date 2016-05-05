package com.xelentec.a100fur.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xelentec.a100fur.R;
import com.xelentec.a100fur.fragments.ForgotPasswordInputFragment;

public class ForgotPasswordActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,new ForgotPasswordInputFragment())
                .commit();




    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
