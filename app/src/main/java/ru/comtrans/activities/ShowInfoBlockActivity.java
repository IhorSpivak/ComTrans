package ru.comtrans.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.comtrans.R;
import ru.comtrans.fragments.infoblock.show.ShowInfoBlockFragment;
import ru.comtrans.helpers.Const;

public class ShowInfoBlockActivity extends BaseActivity {
    String infoBlockId;
    int page;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info_block);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        infoBlockId = getIntent().getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        page = getIntent().getIntExtra(Const.EXTRA_INFO_BLOCK_PAGE,0);
        openMainFragment();
    }
    private void openMainFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ShowInfoBlockFragment.newInstance(infoBlockId, page)).commit();
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
