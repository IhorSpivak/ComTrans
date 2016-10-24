package ru.comtrans.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.UUID;

import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.add.AddInfoBlockFragment;
import ru.comtrans.fragments.infoblock.add.InfoBlockTutorialFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.views.NonSwipeableViewPager;

public class AddInfoBlockActivity extends BaseActivity {

    public NonSwipeableViewPager viewPager;
    private String infoBlockId;
    private int page;
    private InfoBlockHelper helper;




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
        boolean isNew = false;
        if(infoBlockId==null){
            isNew = true;
            infoBlockId = UUID.randomUUID().toString();
        }
        helper = InfoBlockHelper.getInstance();

        if(Utility.getBoolean(Const.IS_FIRST_ADD_INFOBLOCK_LAUNCH))
            openMainFragment(isNew);
        else
            openTutorialFragment(isNew);


    }

    protected void onPause(){
        super.onPause();
    }

    private void openMainFragment(boolean isNew){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, AddInfoBlockFragment.newInstance(infoBlockId, page, isNew)).commit();
    }

    private void openTutorialFragment(boolean isNew){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, InfoBlockTutorialFragment.newInstance(infoBlockId, page, isNew)).commit();
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



}
