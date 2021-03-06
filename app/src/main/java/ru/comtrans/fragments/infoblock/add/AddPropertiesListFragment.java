package ru.comtrans.fragments.infoblock.add;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.AddInfoBlockActivity;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.activities.SearchValueActivity;
import ru.comtrans.adapters.InfoblockAdapter;
import ru.comtrans.adapters.ListAdapter;
import ru.comtrans.dialogs.DatePickerDialogFragment;
import ru.comtrans.fragments.BaseFragment;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.IdsRelationHelperItem;
import ru.comtrans.items.ListItem;
import ru.comtrans.items.MainItem;
import ru.comtrans.managers.LinearLayoutManagerWithSmoothScroller;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.views.DividerItemDecoration;

/**
 * Created by Artco on 06.07.2016.
 */
public class AddPropertiesListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private LinearLayoutManagerWithSmoothScroller layoutManager;
    private InfoblockAdapter adapter;
    private InfoBlocksStorage storage;
    private InfoBlockHelper infoBlockHelper;
    private AddInfoBlockActivity activity;
    private ArrayList<MainItem> items;
    private int page;
    private int totalPages;
    private long propCode;
    private long inspectionCode;
    private String infoBlockId;
    private TextView tvHeader;


    public static AddPropertiesListFragment newInstance(int page, int totalPages, String infoBlockId, long propCode, long inspectionCode) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE, page);
        args.putInt(Const.TOTAL_PAGES, totalPages);
        args.putString(Const.EXTRA_INFO_BLOCK_ID, infoBlockId);
        args.putLong(Const.EXTRA_PROP_CODE, propCode);
        args.putLong(Const.EXTRA_INSPECTION_CODE, inspectionCode);
        AddPropertiesListFragment fragment = new AddPropertiesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
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
//        propCode = getArguments().getLong(Const.EXTRA_PROP_CODE);
        propCode = InfoBlocksStorage.getInfoBlockCategoryCode(infoBlockId);
        inspectionCode = InfoBlocksStorage.getInfoBlockInspectionCode(infoBlockId);
        initPage();
    }

    private void initUi(View v) {
        activity = (AddInfoBlockActivity) getActivity();
        recyclerView = (RecyclerView) v.findViewById(android.R.id.list);
        tvHeader = (TextView) v.findViewById(R.id.tv_header);
        layoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());

        infoBlockHelper = InfoBlockHelper.getInstance();
        storage = InfoBlocksStorage.getInstance();

    }

    private void startTypeListCheck(MainItem item, int position) {

        Intent i = new Intent(getActivity(), SearchValueActivity.class);
        i.putExtra(Const.EXTRA_TITLE, item.getName());
        i.putExtra(Const.EXTRA_POSITION, position);
        i.putExtra(Const.EXTRA_SCREEN_NUM, page);
        startActivityForResult(i, Const.SEARCH_VALUE_RESULT);
    }

    private void initPage() {
        try {
            items = infoBlockHelper.getScreen(page);
        } catch (Exception ignored) {
        }

        if (items != null) {
//            recyclerView.addOnScrollListener(new HidingScrollListener() {
//                @Override
//                public void onHide() {
//                    tvHeader.animate().translationY(-tvHeader.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//                }
//
//                @Override
//                public void onShow() {
//                    tvHeader.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//                }
//            });
            tvHeader.setText(items.get(0).getName());
            adapter = new InfoblockAdapter(getContext(), items, page, totalPages, true, infoBlockId, new InfoblockAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(MainItem item, View view, final int position) {
                    final Intent i;
                    switch (item.getType()) {
                        case MainItem.TYPE_LIST:
                            i = new Intent(getActivity(), SearchValueActivity.class);
                            IdsRelationHelperItem idsRelationHelperItem = new IdsRelationHelperItem();
                            idsRelationHelperItem.setCode(item.getCode());
                            switch (item.getCode()) {
                                case IdsRelationHelperItem.CODE_GENERAL_TYPE_ID:
                                    //Category from resources
                                    break;
                                case IdsRelationHelperItem.CODE_GENERAL_MARK:
                                    //Category from resources
                                    break;
                                case IdsRelationHelperItem.CODE_GENERAL_MODEL:
                                    if(infoBlockHelper.getMarkValue() != null) {
                                        //Category from resources
                                        idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_MARK:
                                    if(infoBlockHelper.getMarkValue() != null && infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL:
                                    if(infoBlockHelper.getEngineMarkValue() != null && infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setEngineMark(infoBlockHelper.getEngineMarkValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_POWER:
                                    if(infoBlockHelper.getModelValue() != null && infoBlockHelper.getEngineModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setEngineModel(infoBlockHelper.getEngineModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_TYPE:
                                    if(infoBlockHelper.getModelValue() != null && infoBlockHelper.getEngineModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setEngineModel(infoBlockHelper.getEngineModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_VOLUME:
                                    if(infoBlockHelper.getModelValue() != null && infoBlockHelper.getEngineModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setEngineModel(infoBlockHelper.getEngineModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ECO_CLASS:
                                    if(infoBlockHelper.getModelValue() != null && infoBlockHelper.getEngineModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setEngineModel(infoBlockHelper.getEngineModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_SHAS_CAPACITY_TOTAL:
                                    if(infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_SHAS_WHEEL_FORMULA:
                                    if(infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_MARK_KPP:
                                    if(infoBlockHelper.getMarkValue() != null && infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_MODEL_KPP:
                                    if(infoBlockHelper.getMarkKppValue() != null && infoBlockHelper.getModelValue() != null) {
                                        idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                        idsRelationHelperItem.setKppMark(infoBlockHelper.getMarkKppValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_KPP_GEARS:
                                    if(infoBlockHelper.getModelKppValue() != null) {
                                        idsRelationHelperItem.setKppModel(infoBlockHelper.getModelKppValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_KPP_TYPE:
//                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelKppValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_FORM_ORGANIZATION:
                                    if(infoBlockHelper.getVehicleOwnerValue() != null) {
                                        idsRelationHelperItem.setVehicleOwner(infoBlockHelper.getVehicleOwnerValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_VEHICLE_OWNER:
                                    break;
                                case IdsRelationHelperItem.CODE_MODEL_KHOU:
                                    if(infoBlockHelper.getMarkKHOUValue() != null) {
                                        idsRelationHelperItem.setKhouMark(infoBlockHelper.getMarkKHOUValue().getId());
                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_MODEL_KMU:
                                    idsRelationHelperItem.setKmuMark(infoBlockHelper.getMarkKMUValue().getId());
//                                case IdsRelationHelperItem.CODE_INSPECTION_TYPE:
//                                    idsRelationHelperItem.setInspectionCode(infoBlockHelper.getInspectionCodeValue().getId());
//                                    break;
                                default:
                                    Log.d("TAG", "int position " + position);
                                    break;
                            }
//                            if (!item.getCode().equals("general_model")) {
//                                Log.d("TAG", "int position " + position);
//                                i = new Intent(getActivity(), SearchValueActivity.class);
//                                i.putExtra(Const.EXTRA_TITLE, item.getName());
//                                i.putExtra(Const.EXTRA_POSITION, position);
//                                i.putExtra(Const.EXTRA_SCREEN_NUM, page);
//                                startActivityForResult(i, Const.SEARCH_VALUE_RESULT);
//                            } else {
//                                if (infoBlockHelper.getMarkValue().getId() == -1) {
//                                    Toast.makeText(getContext(), R.string.no_mark_toast, Toast.LENGTH_SHORT).show();
//                                } else {
//                                    i = new Intent(getActivity(), SearchValueActivity.class);
//                                    i.putExtra(Const.EXTRA_TITLE, item.getName());
//                                    i.putExtra(Const.EXTRA_POSITION, position);
//                                    i.putExtra(Const.EXTRA_SCREEN_NUM, page);
//                                    i.putExtra(Const.EXTRA_MARK, infoBlockHelper.getMarkValue().getId());
//                                    startActivityForResult(i, Const.SEARCH_VALUE_RESULT);
//                                }
//
//                            }


                            if(item.getCode().contains(IdsRelationHelperItem.CODE_INSPECTION_TYPE) ||
                                    item.getCode().contains(IdsRelationHelperItem.CODE_GENERAL_CATEGORY_ID)){
                            }else {
                                i.putExtra(Const.EXTRA_IDS_HELPER, idsRelationHelperItem);
                                i.putExtra(Const.EXTRA_TITLE, item.getName());
                                i.putExtra(Const.EXTRA_POSITION, position);
                                i.putExtra(Const.EXTRA_SCREEN_NUM, page);
                                i.putExtra(Const.EXTRA_INFO_BLOCK_ID, infoBlockId);
                                i.putExtra(Const.EXTRA_PROP_CODE, propCode);
                                i.putExtra(Const.EXTRA_INSPECTION_CODE, inspectionCode);
                                startActivityForResult(i, Const.SEARCH_VALUE_RESULT);
                            }
                            break;

                        case MainItem.TYPE_CALENDAR:
                            try {
                                Bundle args = new Bundle();
                                args.putString(Const.EXTRA_DATE, item.getValue());

                                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                                DatePickerDialogFragment dialogFragment = new DatePickerDialogFragment();
                                dialogFragment.setArguments(args);
                                dialogFragment.setListener(new DatePickerDialogFragment.DateListener() {
                                    @Override
                                    public void setDate(Calendar date) {
                                        SimpleDateFormat sdf = new SimpleDateFormat(Const.INFO_BLOCK_DATE_FORMAT, Locale.getDefault());
                                        String formattedDate = sdf.format(date.getTime());
                                        infoBlockHelper.getItems().get(page).get(position).setValue(formattedDate);
                                        adapter.getItem(position).setValue(formattedDate);
                                        adapter.getItem(position).setError(false);
                                        adapter.notifyItemChanged(position);
                                        saveData();
                                    }
                                });
                                dialogFragment.show(ft, "dialogFragmentDateAddOrder");
                            } catch (Exception ex) {
                                Log.e(TAG, Log.getStackTraceString(ex));
                            }
                            break;
                        case MainItem.TYPE_VIDEO:
                            //    new SaveInfoBlockTask(infoBlockId,getContext());
                            i = new Intent(getActivity(), CameraActivity.class);
                            i.putExtra(Const.CAMERA_MODE, Const.MODE_VIDEO);
                            i.putExtra(Const.EXTRA_POSITION, adapter.getItems().indexOf(item));
                            i.putExtra(Const.EXTRA_IMAGE_POSITION, position);
                            i.putExtra(Const.EXTRA_SCREEN_NUM, page);
                            startActivityForResult(i, Const.CAMERA_VIDEO_RESULT);
                            break;
                        case MainItem.TYPE_PHOTO:

                            i = new Intent(getActivity(), CameraActivity.class);
                            i.putExtra(Const.CAMERA_MODE, Const.MODE_PHOTO);

                            try {
                                if (adapter.getItems().get(adapter.getItems().indexOf(item)).getPhotoItems().get(position).isDefect()) {
                                    i.putExtra(Const.EXTRA_IS_DEFECT, true);
                                } else {
                                    i.putExtra(Const.EXTRA_IS_DEFECT, false);
                                }
                            } catch (Exception ignored) {
                            }

                            i.putExtra(Const.EXTRA_POSITION, adapter.getItems().indexOf(item));
                            i.putExtra(Const.EXTRA_IMAGE_POSITION, position);
                            i.putExtra(Const.EXTRA_SCREEN_NUM, page);
                            startActivityForResult(i, Const.CAMERA_PHOTO_RESULT);
                            break;
                    }

                }

                @Override
                public void saveState() {
                    saveData();
                }

            }, new InfoblockAdapter.OnBottomBarClickListener() {
                @Override
                public void onBottomBarClick(int type, int scrollPosition) {
                    switch (type) {
                        case 1:
                            SaveInfoBlockTask.getInstance(infoBlockId,getContext());
                            storage.updateInfoBlockPage(infoBlockId, page - 1);
                            activity.viewPager.setCurrentItem(page - 1);
                            break;
                        case 2:
                            if (page + 1 == totalPages) {
                                SaveInfoBlockTask.getInstance(infoBlockId, getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
                                    @Override
                                    public void onPostExecute() {
                                        Log.e("Ighor", "onPostExecute() getActivity() = " + getActivity());
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
                                    }
                                });
                            } else {
                                SaveInfoBlockTask.getInstance(infoBlockId,getContext());
                                storage.updateInfoBlockPage(infoBlockId, page + 1);
                                activity.viewPager.setCurrentItem(page + 1);
                            }
                            break;
                        case 3:
                            if (scrollPosition != -1)
                                recyclerView.smoothScrollToPosition(scrollPosition);
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

    private void saveData() {
        SaveInfoBlockTask.getInstance(infoBlockId, getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
            @Override
            public void onPostExecute() {
            }
        }, false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "fragment result");
        int position, screenNum;

        switch (resultCode) {
            case Const.SEARCH_VALUE_RESULT:
                saveData();
                ListItem item = data.getParcelableExtra(Const.EXTRA_VALUE);
                position = data.getIntExtra(Const.EXTRA_POSITION, -1);
                Log.d("TAG", "position result " + data.getIntExtra(Const.EXTRA_POSITION, -1));
                infoBlockHelper.getItems().get(page).get(position).setListValue(item);
                if (infoBlockHelper.getItems().get(page).get(position).getCode().contains(IdsRelationHelperItem.CODE_INSPECTION_TYPE)) {
                    InfoBlocksStorage.setInfoBlockInspectionCode(infoBlockId, item.getId());
                }
                if (infoBlockHelper.getItems().get(page).get(position).getCode().contains(IdsRelationHelperItem.CODE_GENERAL_CATEGORY_ID)) {
                    InfoBlocksStorage.setInfoBlockCategoryCode(infoBlockId, item.getId());
                }
//                if (adapter.getItem(position).getCode().equals("general_marka")) {
//                    if (adapter.getPositionByCode("general_model") != -1) {
//                        adapter.getItem(adapter.getPositionByCode("general_model")).setListValue(new ListItem(-1, getString(R.string.not_chosen)));
//                        adapter.notifyItemChanged(adapter.getPositionByCode("general_model"));
//                    }
//                }
                switch (adapter.getItem(position).getCode()) {
                    case IdsRelationHelperItem.CODE_GENERAL_CATEGORY_ID:
                        dropElemValue(IdsRelationHelperItem.CODE_GENERAL_TYPE_ID);
                        dropElemValue(IdsRelationHelperItem.CODE_GENERAL_MODEL);
                        dropElemValue(IdsRelationHelperItem.CODE_GENERAL_MARK);
                        break;
                    case IdsRelationHelperItem.CODE_GENERAL_TYPE_ID:
//                        dropElemValue(IdsRelationHelperItem.CODE_GENERAL_TYPE_ID);
                        break;
                    case IdsRelationHelperItem.CODE_GENERAL_MARK:
                        dropElemValue(IdsRelationHelperItem.CODE_GENERAL_MODEL);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_MARK);
                        dropElemValue(IdsRelationHelperItem.CODE_MARK_KPP);
                        break;
                    case IdsRelationHelperItem.CODE_GENERAL_MODEL:
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_MARK);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_POWER);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_TYPE);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_VOLUME);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ECO_CLASS);
                        dropElemValue(IdsRelationHelperItem.CODE_SHAS_CAPACITY_TOTAL);
                        dropElemValue(IdsRelationHelperItem.CODE_SHAS_WHEEL_FORMULA);
                        dropElemValue(IdsRelationHelperItem.CODE_MARK_KPP);
                        dropElemValue(IdsRelationHelperItem.CODE_MODEL_KPP);
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ENGINE_MARK:
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL);
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL:
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ENGINE_POWER:
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ENGINE_TYPE:
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ENGINE_VOLUME:
                        break;
                    case IdsRelationHelperItem.CODE_TEC_ECO_CLASS:
                        break;
                    case IdsRelationHelperItem.CODE_SHAS_CAPACITY_TOTAL:
                        break;
                    case IdsRelationHelperItem.CODE_SHAS_WHEEL_FORMULA:
                        break;
                    case IdsRelationHelperItem.CODE_MARK_KPP:
                        dropElemValue(IdsRelationHelperItem.CODE_MODEL_KPP);
                        break;
                    case IdsRelationHelperItem.CODE_MODEL_KPP:
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_KPP_GEARS);
//                        dropElemValue(IdsRelationHelperItem.CODE_TEC_KPP_TYPE);
                        break;
                    case IdsRelationHelperItem.CODE_TEC_KPP_GEARS:
                        break;
                    case IdsRelationHelperItem.CODE_TEC_KPP_TYPE:
                        break;
                    case IdsRelationHelperItem.CODE_FORM_ORGANIZATION:
                        break;
                    case IdsRelationHelperItem.CODE_VEHICLE_OWNER:
                        dropElemValue(IdsRelationHelperItem.CODE_FORM_ORGANIZATION);
                        break;
                    case IdsRelationHelperItem.CODE_MARK_KHOU:
                        dropElemValue(IdsRelationHelperItem.CODE_MODEL_KHOU);
                        break;
                    case IdsRelationHelperItem.CODE_MARK_KMU:
                        dropElemValue(IdsRelationHelperItem.CODE_MODEL_KMU);
                        //Don't do this
//                    case IdsRelationHelperItem.CODE_INSPECTION_TYPE:
//                        dropElemValue(IdsRelationHelperItem.CODE_INSPECTION_TYPE);
                        break;
                    default:
                        Log.d("TAG", "int position " + position);
                        break;
                }


                adapter.getItem(position).setListValue(item);
                adapter.getItem(position).setError(false);
                adapter.notifyItemChanged(position);
//                adapter.notifyDataSetChanged();
                break;
            case Const.CAMERA_PHOTO_RESULT:
                Log.e("WTF", "CAMERA_PHOTO_RESULT");
                position = data.getIntExtra(Const.EXTRA_POSITION, -1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM, -1);
                boolean isDefect = data.getBooleanExtra(Const.EXTRA_IS_DEFECT, false);
                //  adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                try {
                    adapter.notifyItemChanged(position);
                    if(isDefect){
                        adapter.notifyItemChanged(position - 2);
                    }else{
                        adapter.notifyItemChanged(position + 2);
                    }
                } catch (Exception ignored) {
                }


                break;
            case Const.CAMERA_VIDEO_RESULT:
                position = data.getIntExtra(Const.EXTRA_POSITION, -1);
                screenNum = data.getIntExtra(Const.EXTRA_SCREEN_NUM, -1);
                //   adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                try {
                    adapter.notifyItemChanged(position);
                } catch (Exception ignored) {
                }
                break;
        }
    }

    private void dropElemValue(String key) {
        if (adapter.getPositionByCode(key) >= 0) {
            adapter.getItem(adapter.getPositionByCode(key)).setListValue(new ListItem(-1, getString(R.string.not_chosen)));
            adapter.notifyItemChanged(adapter.getPositionByCode(key));
        }
    }

}

