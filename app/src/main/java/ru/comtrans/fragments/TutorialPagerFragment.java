package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.florent37.tutoshowcase.TutoShowcase;

import ru.comtrans.R;
import ru.comtrans.adapters.ViewPagerTutorialPhoto;
import ru.comtrans.adapters.ViewPagerTutorialPhotoDefect;
import ru.comtrans.adapters.ViewPagerTutorialVideo;
import ru.comtrans.helpers.Const;

/**
 * Created by Artco on 31.05.2016.
 */
public class TutorialPagerFragment extends Fragment implements ViewPager.OnPageChangeListener{


    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ImageView btnClose;

    private int tutorial;
    private OnTutorialListener listener;

    public interface OnTutorialListener{
        void onPositionChanged(int position);
        void onOkClick();
    }

    public static TutorialPagerFragment newInstance(int tutorial) {
        Bundle args = new Bundle();
        args.putInt(Const.EXTRA_TUTORIAL,tutorial);
        TutorialPagerFragment fragment = new TutorialPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(OnTutorialListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager_photo_demo,container,false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.pager);
        btnClose = (ImageView)v.findViewById(R.id.btn_clear);

        pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);

        tutorial = getArguments().getInt(Const.EXTRA_TUTORIAL);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOkClick();
            }
        });




        PagerAdapter adapter;

        switch (tutorial){
            case Const.EXTRA_TUTORIAL_PHOTO:
                adapter = new ViewPagerTutorialPhoto(this);
                break;
            case Const.EXTRA_TUTORIAL_PHOTO_DEFECT:
                adapter = new ViewPagerTutorialPhotoDefect(this);
                break;
            case Const.EXTRA_TUTORIAL_VIDEO:
                adapter = new ViewPagerTutorialVideo(this);
                break;
            default:
                adapter = new ViewPagerTutorialPhoto(this);
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
        viewPager.addOnPageChangeListener(this);

        setUiPageViewController(adapter);

        return v;
    }





    private void setUiPageViewController(PagerAdapter adapter) {

        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.not_selected_item_dot));

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                   width,width
            );

            params.setMargins(8, 0, 8, 0);

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

        listener.onPositionChanged(position);

        switch (position){
            case 0:
                dots[1].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
                break;
            case 1:
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
                break;
//            case 2:
//                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
//                break;
        }






    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
