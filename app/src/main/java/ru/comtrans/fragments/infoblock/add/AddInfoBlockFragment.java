package ru.comtrans.fragments.infoblock.add;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.adapters.AddInfoBlockPagerAdapter;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.singlets.AppController;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.singlets.PropHelper;
import ru.comtrans.views.ConnectionProgressDialog;
import ru.comtrans.views.NonSwipeableViewPager;

/**
 * Created by Artco on 14.07.2016.
 */
public class AddInfoBlockFragment extends BaseFragment implements ViewPager.OnPageChangeListener{

    private ConnectionProgressDialog progressDialog;
    private LinearLayout pager_indicator;
    private InfoBlocksStorage storage;
    private InfoBlockHelper infoBlockHelper;
    private int dotsCount;
    private ImageView[] dots;
    private AddInfoBlockPagerAdapter adapter;
    private PropHelper propHelper;
    private AddInfoBlockActivity activity;
    private ProgressBar emptyBar;
    private String infoBlockId;
    boolean isNew;

    public static AddInfoBlockFragment newInstance(String id, boolean isNew) {
        Bundle args = new Bundle();
        args.putString(Const.EXTRA_INFO_BLOCK_ID,id);
        args.putBoolean(Const.IS_NEW_INFO_BLOCK,isNew);
        AddInfoBlockFragment fragment = new AddInfoBlockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_infoblock,container,false);
        activity = (AddInfoBlockActivity) getActivity();

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);
        emptyBar = (ProgressBar)v.findViewById(R.id.empty_bar);
        infoBlockId = getArguments().getString(Const.EXTRA_INFO_BLOCK_ID);
        isNew = getArguments().getBoolean(Const.IS_NEW_INFO_BLOCK);

        progressDialog = new ConnectionProgressDialog(getContext());

        emptyBar.setVisibility(View.VISIBLE);
        activity.viewPager = (NonSwipeableViewPager) v.findViewById(R.id.pager);



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!Utility.containsData(Const.JSON_PROP))
                    getProp();
                else
                    new AsyncTaskForGetProp().execute();
            }
        });
        t.start();
        return v;
    }

    private void getProp(){
       getActivity().runOnUiThread(new Runnable() {
           @Override
           public void run() {
               progressDialog.show();
           }
       });

        Call<JsonObject> call = AppController.apiInterface.getProp();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.body().get("status").getAsInt()==1){
                    Utility.saveData(Const.JSON_PROP,response.body().get("result").getAsJsonArray().toString());
                    new AsyncTaskForGetProp().execute();
                }else {

                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }




    private class AsyncTaskForGetProp extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptyBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            storage = InfoBlocksStorage.getInstance();
            infoBlockHelper = InfoBlockHelper.getInstance();
            if(isNew) {
                propHelper = PropHelper.getInstance();
                storage.saveInfoBlock(infoBlockId, propHelper.getScreens());
                storage.setInfoBlockStatus(infoBlockId, MyInfoBlockItem.STATUS_DRAFT);
            }
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

        adapter = new AddInfoBlockPagerAdapter(getFragmentManager(),getContext(),infoBlockId,infoBlockHelper.getItemsSize());
        activity.viewPager.setAdapter(adapter);
        activity.viewPager.setCurrentItem(0);
        activity.viewPager.setOffscreenPageLimit(1);
        activity.viewPager.addOnPageChangeListener(this);
        setUiPageViewController();
    }

    private void setUiPageViewController() {

        dotsCount = adapter.getCount();
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
