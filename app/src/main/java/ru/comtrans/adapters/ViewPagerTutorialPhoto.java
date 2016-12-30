package ru.comtrans.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;


/**
 * Created by Wasim on 11-06-2015.
 */
public class ViewPagerTutorialPhoto extends PagerAdapter {

    private Fragment fragment;

    public ViewPagerTutorialPhoto(Fragment fragment) {
        this.fragment = fragment;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView;
        switch (position){
            case 2:
                itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.fragment_tutorial_first_screen, container, false);
                container.addView(itemView);
                break;
            case 1:
                itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.fragment_tutorial_second_non_defect, container, false);
                container.addView(itemView);
                break;
            case 0:
                itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.fragment_tutorial_third_non_defect, container, false);
                container.addView(itemView);
                break;
            default:
                itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.fragment_tutorial_third_non_defect, container, false);
                container.addView(itemView);
        }






        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}