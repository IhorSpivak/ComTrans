package ru.comtrans.fragments.infoblock.show;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import ru.comtrans.R;
import ru.comtrans.activities.ShowInfoBlockActivity;
import ru.comtrans.adapters.ShowInfoBlockPagerAdapter;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;

/**
 * Created by Artco on 11.08.2016.
 */
public class ShowInfoBlockFragment extends BaseFragment implements ViewPager.OnPageChangeListener{

    private ProgressBar emptyBar;
    private String infoBlockId;
    private int page;
    private LinearLayout pager_indicator;
    private InfoBlocksStorage storage;
    private InfoBlockHelper infoBlockHelper;
    private int dotsCount;
    private ImageView[] dots;
    private ShowInfoBlockPagerAdapter adapter;
    private ShowInfoBlockActivity activity;

    public static ShowInfoBlockFragment newInstance(String id,int page) {

        Bundle args = new Bundle();
        args.putString(Const.EXTRA_INFO_BLOCK_ID,id);
        args.putInt(Const.EXTRA_INFO_BLOCK_PAGE,page);
        ShowInfoBlockFragment fragment = new ShowInfoBlockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_infoblock,container,false);


        activity = (ShowInfoBlockActivity)getActivity();
        activity.viewPager = (ViewPager)v.findViewById(R.id.pager);
        pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);
        emptyBar = (ProgressBar)v.findViewById(R.id.empty_bar);
        infoBlockId = getArguments().getString(Const.EXTRA_INFO_BLOCK_ID);
        page = getArguments().getInt(Const.EXTRA_INFO_BLOCK_PAGE);

        new AsyncTaskForShowInfoBlock().execute();

        return v;
    }

    private class AsyncTaskForShowInfoBlock extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptyBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            storage = InfoBlocksStorage.getInstance();
            infoBlockHelper = InfoBlockHelper.getInstance();
            infoBlockHelper.getAllItems(infoBlockId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setAdapter();
            emptyBar.setVisibility(View.GONE);
        }
    }


    private void setAdapter(){

        adapter = new ShowInfoBlockPagerAdapter(getFragmentManager(),getContext(),infoBlockId,infoBlockHelper.getItemsSize());
        setUiPageViewController();
        activity.viewPager.setAdapter(adapter);
        activity.viewPager.setOffscreenPageLimit(1);
        activity.viewPager.addOnPageChangeListener(this);
        activity.viewPager.setCurrentItem(page);
    }

    private void setUiPageViewController() {

        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.main_not_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.main_selected_item_dot));
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
