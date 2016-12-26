package ru.comtrans.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.comtrans.fragments.infoblock.show.ShowPropertiesListFragment;

/**
 * Created by Artco on 11.08.2016.
 */
public class ShowInfoBlockPagerAdapter extends FragmentStatePagerAdapter {
    Context c;
    int pagesCount;
    String infoBlockId;

    public ShowInfoBlockPagerAdapter(FragmentManager fm, Context c, String infoBlockId, int pagesCount) {
        super(fm);
        this.c = c;
        this.pagesCount = pagesCount;
        this.infoBlockId = infoBlockId;
    }

    @Override
    public Fragment getItem(int position) {
        return ShowPropertiesListFragment.newInstance(position,pagesCount,infoBlockId);
    }

    @Override
    public int getCount() {
        return pagesCount;
    }


}
