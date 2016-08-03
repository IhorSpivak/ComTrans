package ru.comtrans.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.comtrans.fragments.infoblock.PropertiesListFragment;

/**
 * Created by Artco on 14.07.2016.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    Context c;
    int pagesCount;
    String infoBlockId;

    public SectionsPagerAdapter(FragmentManager fm, Context c,String infoBlockId, int pagesCount) {
        super(fm);
        this.c = c;
        this.pagesCount = pagesCount;
        this.infoBlockId = infoBlockId;
    }

    @Override
    public Fragment getItem(int position) {
        return PropertiesListFragment.newInstance(position,pagesCount,infoBlockId);
    }

    @Override
    public int getCount() {
        return pagesCount;
    }


}
