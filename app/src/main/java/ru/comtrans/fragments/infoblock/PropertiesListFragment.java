package ru.comtrans.fragments.infoblock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.SearchValueActivity;
import ru.comtrans.adapters.InfoBlockAdapter;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.InfoBlockHelper;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 06.07.2016.
 */
public class PropertiesListFragment extends BaseFragment {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    InfoBlockAdapter adapter;
    InfoBlockHelper infoBlockHelper;
    AddInfoBlockActivity activity;
    ArrayList<MainItem> items;
    int page;
    int totalPages;
    String infoBlockId;




    public static PropertiesListFragment newInstance(int page,int totalPages,String infoBlockId) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE,page);
        args.putInt(Const.TOTAL_PAGES,totalPages);
        args.putString(Const.EXTRA_INFO_BLOCK_ID,infoBlockId);
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
        totalPages = getArguments().getInt(Const.TOTAL_PAGES);
        infoBlockId = getArguments().getString(Const.EXTRA_INFO_BLOCK_ID);

        initPage();


    }

    private void initUi(View v){
        activity = (AddInfoBlockActivity) getActivity();
        recyclerView = (RecyclerView)v.findViewById(android.R.id.list);
        layoutManager = new LinearLayoutManager(getActivity());
        infoBlockHelper = InfoBlockHelper.getInstance();

    }






    private void initPage(){

        items = infoBlockHelper.getScreen(page);


        adapter = new InfoBlockAdapter(getContext(), items,page,totalPages,infoBlockId, new InfoBlockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainItem item,View view,int position) {
                Intent i;
                switch (item.getType()){
                    case MainItem.TYPE_LIST:
                        if(!item.getCode().equals("general_model")){
                            i = new Intent(getActivity(), SearchValueActivity.class);
                            i.putExtra(Const.EXTRA_TITLE,item.getName());
                            i.putExtra(Const.EXTRA_POSITION,position);
                            i.putExtra(Const.EXTRA_SCREEN_NUM,page);
                            startActivityForResult(i,Const.SEARCH_VALUE_RESULT);
                        }else {
                            if(items.get(position-1).getListValue().getId()==-1){
                                Toast.makeText(getContext(),R.string.no_mark_toast,Toast.LENGTH_SHORT).show();
                            }else {
                                i = new Intent(getActivity(), SearchValueActivity.class);
                                i.putExtra(Const.EXTRA_TITLE,item.getName());
                                i.putExtra(Const.EXTRA_POSITION,position);
                                i.putExtra(Const.EXTRA_SCREEN_NUM,page);
                                i.putExtra(Const.EXTRA_MARK,items.get(position-1).getListValue().getId());
                                startActivityForResult(i,Const.SEARCH_VALUE_RESULT);
                            }

                        }

                        break;
                    case MainItem.TYPE_VIDEO:
                        i = new Intent(getActivity(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_VIDEO);
                        i.putExtra(Const.EXTRA_POSITION,adapter.getItems().indexOf(item));
                        i.putExtra(Const.EXTRA_IMAGE_POSITION,position);
                        i.putExtra(Const.EXTRA_SCREEN_NUM,page);
                        startActivityForResult(i,Const.CAMERA_VIDEO_RESULT);
                        break;
                    case MainItem.TYPE_PHOTO:
                        i = new Intent(getActivity(), CameraActivity.class);
                        i.putExtra(Const.CAMERA_MODE,Const.MODE_PHOTO);
                        i.putExtra(Const.EXTRA_POSITION,adapter.getItems().indexOf(item));
                        i.putExtra(Const.EXTRA_IMAGE_POSITION,position);
                        i.putExtra(Const.EXTRA_SCREEN_NUM,page);
                        startActivityForResult(i,Const.CAMERA_PHOTO_RESULT);
                        break;
                    case MainItem.TYPE_BOTTOM_BAR:
                        switch (position){
                            case 1:
                                activity.viewPager.setCurrentItem(page-1);
                                break;
                            case 2:
                                activity.viewPager.setCurrentItem(page+1);
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
                if(adapter.getItem(position).getCode().equals("general_marka")){
                  adapter.getItem(position+1).setListValue(adapter.getItem(position+1).getListValues().get(0));
                }
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
