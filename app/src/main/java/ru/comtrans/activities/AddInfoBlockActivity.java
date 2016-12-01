package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.UUID;

import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.add.AddInfoBlockFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.services.AudioRecordService;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.views.NonSwipeableViewPager;

public class AddInfoBlockActivity extends BaseActivity {

    public NonSwipeableViewPager viewPager;
    private String infoBlockId;
    private int page;
    private long propCode;
    private InfoBlockHelper helper;
    boolean isNew = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_infoblock);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        infoBlockId = getIntent().getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        page = getIntent().getIntExtra(Const.EXTRA_INFO_BLOCK_PAGE,0);
        propCode = getIntent().getLongExtra(Const.EXTRA_PROP_CODE,0);


        if(infoBlockId==null){
            isNew = true;
            infoBlockId = UUID.randomUUID().toString();
        }
        Intent i = new Intent(this, AudioRecordService.class);
        i.putExtra(Const.EXTRA_INFO_BLOCK_ID,infoBlockId);
        startService(i);
        helper = InfoBlockHelper.getInstance();


        openMainFragment();



    }

    protected void onPause(){
        super.onPause();
    }

    private void openMainFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, AddInfoBlockFragment.newInstance(infoBlockId, page,propCode, isNew)).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG","onActivity result in activity");
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
        new SaveInfoBlockTask(infoBlockId,AddInfoBlockActivity.this, new SaveInfoBlockTask.OnPostExecuteListener() {
            @Override
            public void onPostExecute() {
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, AudioRecordService.class));
        super.onDestroy();

    }
}
