package ru.comtrans.fragments.infoblock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.SearchValueActivity;
import ru.comtrans.adapters.InfoBlockAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.PropHelper;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropertiesListFragment extends Fragment {

    PropHelper propHelper;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    InfoBlockAdapter adapter;
    ProgressBar emptyBar;
    AddInfoBlockActivity activity;
    int page;




    public static PropertiesListFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE,page);
        PropertiesListFragment fragment = new PropertiesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        initUi(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        page = getArguments().getInt(Const.PAGE);
        emptyBar.setVisibility(View.VISIBLE);
        new AsyncTaskForGetProp().execute();

    }

    private void initUi(View v){
        activity = (AddInfoBlockActivity) getActivity();
        recyclerView = (RecyclerView)v.findViewById(android.R.id.list);
        layoutManager = new LinearLayoutManager(getActivity());
        emptyBar = (ProgressBar)v.findViewById(R.id.empty_bar);
    }




    private void initPage(){

        adapter = new InfoBlockAdapter((AppCompatActivity)getActivity(), propHelper.getScreenItems(page),page, new InfoBlockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainItem item,View view,int position) {
                Intent i;
                switch (item.getType()){
                    case MainItem.TYPE_LIST:
                        i = new Intent(getActivity(), SearchValueActivity.class);
                        i.putExtra(Const.EXTRA_TITLE,item.getName());
                        i.putExtra(Const.EXTRA_POSITION,position);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,item.getListValues());
                        startActivityForResult(i,Const.SEARCH_VALUE_RESULT);
                        break;
                    case MainItem.TYPE_VIDEO:
                        i = new Intent(getActivity(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_VIDEO);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,item.getPhotoItems());
                        i.putExtra(Const.EXTRA_POSITION,adapter.getItems().indexOf(item));
                        i.putExtra(Const.EXTRA_IMAGE_POSITION,position);
                        startActivityForResult(i,Const.CAMERA_VIDEO_RESULT);
                        break;
                    case MainItem.TYPE_PHOTO:
                        i = new Intent(getActivity(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_PHOTO);
                        i.putExtra(Const.EXTRA_POSITION,adapter.getItems().indexOf(item));
                        i.putExtra(Const.EXTRA_IMAGE_POSITION,position);
                        i.putParcelableArrayListExtra(Const.EXTRA_VALUES,item.getPhotoItems());
                        startActivityForResult(i,Const.CAMERA_PHOTO_RESULT);
                        break;
                    case MainItem.TYPE_BOTTOM_BAR:
                        switch (position){
                            case 1:
                                activity.viewPager.setCurrentItem(page-2);
                                break;
                            case 2:
                                activity.viewPager.setCurrentItem(page);
                                break;
                        }
                        break;

                }

            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utility.hideKeyboard(getActivity(),view);
                return false;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
        emptyBar.setVisibility(View.GONE);

    }

    private class AsyncTaskForGetProp extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            propHelper = PropHelper.getInstance();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initPage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int position;
        ArrayList<PhotoItem> items;
        switch (resultCode){
            case Const.SEARCH_VALUE_RESULT:
                ListItem item = data.getParcelableExtra(Const.EXTRA_VALUE);
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                adapter.getItem(position).setListValue(item);
                adapter.notifyDataSetChanged();
                break;
            case Const.CAMERA_PHOTO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                items = data.getParcelableArrayListExtra(Const.EXTRA_VALUES);
                adapter.getItem(position).setPhotoItems(items);
                adapter.notifyDataSetChanged();

                break;
            case Const.CAMERA_VIDEO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                items = data.getParcelableArrayListExtra(Const.EXTRA_VALUES);
                adapter.getItem(position).setPhotoItems(items);
                adapter.notifyDataSetChanged();

                break;
        }
    }


}
