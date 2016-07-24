package ru.comtrans.fragments.infoblock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.adapters.SectionsPagerAdapter;
import ru.comtrans.views.NonSwipeableViewPager;

/**
 * Created by Artco on 14.07.2016.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener{

    private AddInfoBlockActivity activity;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private SectionsPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main,container,false);
        activity = (AddInfoBlockActivity) getActivity();
        activity.viewPager = (NonSwipeableViewPager) v.findViewById(R.id.pager);


        pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);



        mAdapter = new SectionsPagerAdapter(getFragmentManager(),getActivity());
        activity.viewPager.setAdapter(mAdapter);
        activity.viewPager.setCurrentItem(0);
        activity.viewPager.setOffscreenPageLimit(6);
        activity.viewPager.addOnPageChangeListener(this);




        setUiPageViewController();

        return v;
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.main_not_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.main_selected_item_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.main_not_selected_item_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.main_selected_item_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
