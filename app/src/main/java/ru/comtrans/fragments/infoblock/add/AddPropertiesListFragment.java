package ru.comtrans.fragments.infoblock.add;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.SearchValueActivity;
import ru.comtrans.adapters.InfoBlockAdapter;
import ru.comtrans.dialogs.DatePickerDialogFragment;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.items.MyInfoBlockItem;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.tasks.SendingService;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 06.07.2016.
 */
public class AddPropertiesListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private InfoBlockAdapter adapter;
    private InfoBlocksStorage storage;
    private InfoBlockHelper infoBlockHelper;
    private AddInfoBlockActivity activity;
    private ArrayList<MainItem> items;
    private int page;
    private int totalPages;
    private String infoBlockId;




    public static AddPropertiesListFragment newInstance(int page, int totalPages, String infoBlockId) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE,page);
        args.putInt(Const.TOTAL_PAGES,totalPages);
        args.putString(Const.EXTRA_INFO_BLOCK_ID,infoBlockId);
        AddPropertiesListFragment fragment = new AddPropertiesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        storage = InfoBlocksStorage.getInstance();

    }

    private void initPage(){
        items = infoBlockHelper.getScreen(page);

        adapter = new InfoBlockAdapter(getContext(), items,page,totalPages,true, new InfoBlockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainItem item, View view, final int position) {
                final Intent i;
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

                    case MainItem.TYPE_CALENDAR:
                        try {
                            Bundle args = new Bundle();
                            args.putString(Const.EXTRA_DATE,item.getValue());

                            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                            DatePickerDialogFragment dialogFragment = new DatePickerDialogFragment();
                            dialogFragment.setArguments(args);
                            dialogFragment.setListener(new DatePickerDialogFragment.DateListener() {
                                @Override
                                public void setDate(Calendar date) {
                                    saveData();
                                    SimpleDateFormat sdf = new SimpleDateFormat(Const.INFO_BLOCK_DATE_FORMAT,Locale.getDefault());
                                    String formattedDate = sdf.format(date.getTime());
                                    infoBlockHelper.getItems().get(page).get(position).setValue(formattedDate);
                                    adapter.getItem(position).setValue(formattedDate);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            dialogFragment.show(ft, "dialogFragmentDateAddOrder");
                        } catch (Exception ex) {
                            Log.e(TAG, Log.getStackTraceString(ex));
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
                                storage.updateInfoBlockPage(infoBlockId, page-1);
                                activity.viewPager.setCurrentItem(page-1);
                                break;
                            case 2:
                                if(page+1==totalPages){
                                    new SaveInfoBlockTask(infoBlockId,getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
                                        @Override
                                        public void onPostExecute() {
                                           getActivity().finish();
                                        }
                                    });
                                }else {
                                    storage.updateInfoBlockPage(infoBlockId, page+1);
                                    activity.viewPager.setCurrentItem(page+1);
                                }
                                break;
                        }
                        break;

                }

            }

            @Override
            public void saveState() {
                saveData();
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

    private void saveData(){
        new SaveInfoBlockTask(infoBlockId, getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
            @Override
            public void onPostExecute() {
            }
        },false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("WTF","onActivityResult addPropertiesListFragment requestCode="+requestCode+" resultCode="+resultCode);
        int position,screenNum;

        switch (resultCode){
            case Const.SEARCH_VALUE_RESULT:
                saveData();
                ListItem item = data.getParcelableExtra(Const.EXTRA_VALUE);
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                infoBlockHelper.getItems().get(page).get(position).setListValue(item);
                if(adapter.getItem(position).getCode().equals("general_marka")){
                  adapter.getItem(position+1).setListValue(adapter.getItem(position+1).getListValues().get(0));
                }
                adapter.getItem(position).setListValue(item);
                adapter.notifyItemChanged(position);
//                adapter.notifyDataSetChanged();
                break;
            case Const.CAMERA_PHOTO_RESULT:
                Log.e("WTF","CAMERA_PHOTO_RESULT");
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
                adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                adapter.notifyItemChanged(position);
//                adapter.notifyDataSetChanged();
                //initPage();
                break;
            case Const.CAMERA_VIDEO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION,-1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
                adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                adapter.notifyItemChanged(position);
//                adapter.notifyDataSetChanged();
                break;
        }
    }


}
