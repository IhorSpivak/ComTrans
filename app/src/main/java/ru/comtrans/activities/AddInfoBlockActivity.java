package ru.comtrans.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.MainFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.singlets.AppController;
import ru.comtrans.views.ConnectionProgressDialog;
import ru.comtrans.views.NonSwipeableViewPager;

public class AddInfoBlockActivity extends AppCompatActivity {
    ConnectionProgressDialog progressDialog;
    public NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_infoblock);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ConnectionProgressDialog(AddInfoBlockActivity.this);

 //       if(Utility.getSavedData(Const.JSON_PROP).equals("")){
            getProp();
//        }else {
//            openMainFragment();
//        }


    }

    private void getProp(){
        progressDialog.show();
        Call<JsonObject> call = AppController.apiInterface.getProp();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                if(response.body().get("status").getAsInt()==1){
                    Utility.saveData(Const.JSON_PROP,response.body().get("result").getAsJsonObject().toString());
                    openMainFragment();
                }else {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void openMainFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

}
