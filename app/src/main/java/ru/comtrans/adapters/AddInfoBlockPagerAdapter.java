package ru.comtrans.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.comtrans.fragments.infoblock.add.AddPropertiesListFragment;

/**
 * Created by Artco on 14.07.2016.
 */
public class AddInfoBlockPagerAdapter extends FragmentStatePagerAdapter {

    Context c;
    int pagesCount;
    String infoBlockId;
    long propCode;

    public AddInfoBlockPagerAdapter(FragmentManager fm, Context c, String infoBlockId, int pagesCount, long propCode) {
        super(fm);
        this.c = c;
        this.pagesCount = pagesCount;
        this.infoBlockId = infoBlockId;
        this.propCode = propCode;
    }

    @Override
    public Fragment getItem(int position) {
        return AddPropertiesListFragment.newInstance(position,pagesCount,infoBlockId, propCode);
    }

    @Override
    public int getCount() {
        return pagesCount;
    }


}

