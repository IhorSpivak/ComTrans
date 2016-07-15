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

    public SectionsPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.c = c;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PropertiesListFragment.newInstance(1);
            case 1:
                return PropertiesListFragment.newInstance(2);
            case 2:
                return PropertiesListFragment.newInstance(3);
            case 3:
                return PropertiesListFragment.newInstance(4);
            case 4:
                return PropertiesListFragment.newInstance(5);
            case 5:
                return PropertiesListFragment.newInstance(6);
            default:
                return PropertiesListFragment.newInstance(1);
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

}
