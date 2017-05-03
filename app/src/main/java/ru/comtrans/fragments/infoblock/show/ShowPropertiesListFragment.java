package ru.comtrans.fragments.infoblock.show;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.activities.ShowInfoBlockActivity;
import ru.comtrans.adapters.InfoblockAdapter;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.managers.LinearLayoutManagerWithSmoothScroller;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 11.08.2016.
 */
public class ShowPropertiesListFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private LinearLayoutManagerWithSmoothScroller layoutManager;
    private InfoblockAdapter adapter;
    private InfoBlocksStorage storage;
    private InfoBlockHelper infoBlockHelper;
    private ShowInfoBlockActivity activity;
    private ArrayList<MainItem> items;
    private int page;
    private int totalPages;
    private String infoBlockId;
    private TextView tvHeader;
    private long propCode;
    private long inspectionCode;




    public static ShowPropertiesListFragment newInstance(int page, int totalPages, String infoBlockId) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE,page);
        args.putInt(Const.TOTAL_PAGES,totalPages);
        args.putString(Const.EXTRA_INFO_BLOCK_ID,infoBlockId);
        ShowPropertiesListFragment fragment = new ShowPropertiesListFragment();
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
        propCode = InfoBlocksStorage.getInfoBlockCategoryCode(infoBlockId);
        inspectionCode = InfoBlocksStorage.getInfoBlockInspectionCode(infoBlockId);

        initPage();


    }

    private void initUi(View v){
        activity = (ShowInfoBlockActivity) getActivity();
        recyclerView = (RecyclerView)v.findViewById(android.R.id.list);
        tvHeader = (TextView)v.findViewById(R.id.tv_header);
        layoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());
        infoBlockHelper = InfoBlockHelper.getInstance();
        storage = InfoBlocksStorage.getInstance();

    }






    private void initPage(){
        try {
            items = infoBlockHelper.getScreen(page);
        }catch (Exception ignored){}

        if(items!=null) {

            tvHeader.setText(items.get(0).getName());
            adapter = new InfoblockAdapter(getContext(), items, page, totalPages, false, infoBlockId, new InfoblockAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(MainItem item, View view, int position) {


                }

                @Override
                public void saveState() {
                    //stub
                }
            }, new InfoblockAdapter.OnBottomBarClickListener() {
                @Override
                public void onBottomBarClick(int type, int scrollPosition) {
                    switch (type) {
                        case 1:
                            activity.viewPager.setCurrentItem(page - 1);
                            break;
                        case 2:
                            if (page + 1 == totalPages) {
                                new SaveInfoBlockTask(infoBlockId, getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
                                    @Override
                                    public void onPostExecute() {
                                        getActivity().finish();
                                    }
                                });
                            } else {
                                activity.viewPager.setCurrentItem(page + 1);
                            }
                            break;
                    }
                }
            });
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Utility.hideKeyboard(getActivity(), view);
                    return false;
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int position,screenNum;

        switch (resultCode){
            case Const.SEARCH_VALUE_RESULT:
                ListItem item = data.getParcelableExtra(Const.EXTRA_VALUE);
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                infoBlockHelper.getItems().get(page).get(position).setListValue(item);
                if(adapter.getItem(position).getCode().equals("general_marka")){
                    adapter.getItem(position+1).setListValue(adapter.getItem(position+1).getListValues().get(0));
                }
                adapter.getItem(position).setListValue(item);
                adapter.notifyDataSetChanged();
                break;
            case Const.CAMERA_PHOTO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
                adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                adapter.notifyDataSetChanged();

                break;
            case Const.CAMERA_VIDEO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
                adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                adapter.notifyDataSetChanged();

                break;
        }
    }

}
