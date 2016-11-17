package ru.comtrans.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;


/**
 * Created by Wasim on 11-06-2015.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Fragment fragment;
    private int[] mResources;
    private ViewPager viewPager;

    public ViewPagerAdapter(Fragment fragment, int[] mResources, ViewPager viewPager) {
        this.fragment = fragment;
        this.mResources = mResources;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.pager_image_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        imageView.setImageResource(mResources[position]);

        TextView skip = (TextView)itemView.findViewById(R.id.skip);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.saveBoolean(Const.IS_FIRST_CAMERA_LAUNCH,true);
               fragment.getFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        if(position==2){
            skip.setText(R.string.start);
        }else {
            skip.setText(R.string.skip);
        }


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}