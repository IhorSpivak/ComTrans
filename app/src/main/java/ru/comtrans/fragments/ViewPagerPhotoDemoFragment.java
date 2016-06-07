package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.comtrans.R;
import ru.comtrans.adapters.ViewPagerAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;

/**
 * Created by Artco on 31.05.2016.
 */
public class ViewPagerPhotoDemoFragment extends Fragment implements ViewPager.OnPageChangeListener{


    private ViewPager viewPager;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;


    private int[] mImageResources = {
            R.drawable.instruction1,
            R.drawable.instruction2,
            R.drawable.instruction3,

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager_photo_demo,container,false);

        viewPager = (ViewPager) v.findViewById(R.id.pager_introduction);


        pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);



        mAdapter = new ViewPagerAdapter(getActivity(), mImageResources,viewPager);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);

        setUiPageViewController();

        return v;
    }



    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.not_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.not_selected_item_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));




    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
