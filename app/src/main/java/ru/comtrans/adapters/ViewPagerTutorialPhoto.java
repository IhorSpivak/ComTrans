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
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        switch (position){
            case 1:
                imageView.setImageResource(R.drawable.photo2);
                break;
            case 0:
                imageView.setImageResource(R.drawable.photo3);
                break;
            default:
                imageView.setImageResource(R.drawable.photo2);
        }
        container.addView(imageView);






        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}