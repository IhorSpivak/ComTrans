package ru.comtrans.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.UUID;

import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.MainFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.InfoBlockHelper;
import ru.comtrans.views.ConnectionProgressDialog;
import ru.comtrans.views.NonSwipeableViewPager;

public class AddInfoBlockActivity extends AppCompatActivity {

    public NonSwipeableViewPager viewPager;
    String infoBlockId;
    InfoBlockHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_infoblock);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        infoBlockId = getIntent().getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        boolean isNew = false;
        if(infoBlockId==null){
            isNew = true;
            infoBlockId = UUID.randomUUID().toString();
        }
        helper = InfoBlockHelper.getInstance();
        openMainFragment(isNew);


    }


    private void openMainFragment(boolean isNew){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance(infoBlockId,isNew)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                saveAndExit();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
    }



    private void saveAndExit(){
        new AsyncTaskForSaveAndExit().execute();
    }


    private class AsyncTaskForSaveAndExit extends AsyncTask<Void,Void,Void>{
        ConnectionProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ConnectionProgressDialog(AddInfoBlockActivity.this,"Сохранение инфоблока");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helper.saveAllAndClear();
            SystemClock.sleep(900);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                dialog.dismiss();
            }catch (Exception ignored){}

            LocalBroadcastManager.getInstance(AddInfoBlockActivity.this).sendBroadcast(new Intent(Const.REFRESH_INFO_BLOCKS_FILTER));
            finish();
        }
    }
}
