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
    private String infoBlockId;
    private TextView tvHeader;


    public static AddPropertiesListFragment newInstance(int page, int totalPages, String infoBlockId) {

        Bundle args = new Bundle();
        args.putInt(Const.PAGE, page);
        args.putInt(Const.TOTAL_PAGES, totalPages);
        args.putString(Const.EXTRA_INFO_BLOCK_ID, infoBlockId);
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
            adapter = new InfoblockAdapter(getContext(), items, page, totalPages, true, new InfoblockAdapter.OnItemClickListener() {
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
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_GENERAL_MARK:
                                    break;
                                case IdsRelationHelperItem.CODE_GENERAL_MODEL:
//                                    if (infoBlockHelper.getMarkValue().getId() == -1) {
//                                        Toast.makeText(getContext(), R.string.no_mark_toast, Toast.LENGTH_SHORT).show();
//                                    } else {
                                    idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
//                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_MARK:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_MODEL:
//                                    if (infoBlockHelper.getMarkValue().getId() == -1 ||
//                                            infoBlockHelper.getModelValue().getId() == -1 ||
//                                            infoBlockHelper.getEngineMarkValue().getId() == -1) {
//                                        Toast.makeText(getContext(), R.string.no_mark_model_enginemark_toast, Toast.LENGTH_SHORT).show();
//                                    } else {
                                    idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    idsRelationHelperItem.setEngineMark(infoBlockHelper.getEngineMarkValue().getId());
//                                    }
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_POWER:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getEngineModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_TYPE:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getEngineModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ENGINE_VOLUME:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getEngineModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_ECO_CLASS:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getEngineModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_SHAS_CAPACITY_TOTAL:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_SHAS_WHEEL_FORMULA:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_MARK_KPP:
                                    idsRelationHelperItem.setMark(infoBlockHelper.getMarkValue().getId());
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_MODEL_KPP:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_KPP_GEARS:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelKppValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_TEC_KPP_TYPE:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getModelKppValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_FORM_ORGANIZATION:
                                    idsRelationHelperItem.setModel(infoBlockHelper.getVehicleOwnerValue().getId());
                                    break;
                                case IdsRelationHelperItem.CODE_VEHICLE_OWNER:
                                    break;
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

                            i.putExtra(Const.EXTRA_IDS_HELPER, idsRelationHelperItem);
                            i.putExtra(Const.EXTRA_TITLE, item.getName());
                            i.putExtra(Const.EXTRA_POSITION, position);
                            i.putExtra(Const.EXTRA_SCREEN_NUM, page);
                            startActivityForResult(i, Const.SEARCH_VALUE_RESULT);
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
                            new SaveInfoBlockTask(infoBlockId, getContext());
                            storage.updateInfoBlockPage(infoBlockId, page - 1);
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
                                new SaveInfoBlockTask(infoBlockId, getContext());
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
        new SaveInfoBlockTask(infoBlockId, getContext(), new SaveInfoBlockTask.OnPostExecuteListener() {
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
//                if (adapter.getItem(position).getCode().equals("general_marka")) {
//                    if (adapter.getPositionByCode("general_model") != -1) {
//                        adapter.getItem(adapter.getPositionByCode("general_model")).setListValue(new ListItem(-1, getString(R.string.not_chosen)));
//                        adapter.notifyItemChanged(adapter.getPositionByCode("general_model"));
//                    }
//                }
                switch (adapter.getItem(position).getCode()) {
                    case IdsRelationHelperItem.CODE_GENERAL_TYPE_ID:
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
                        break;
                    case IdsRelationHelperItem.CODE_MODEL_KPP:
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_KPP_GEARS);
                        dropElemValue(IdsRelationHelperItem.CODE_TEC_KPP_TYPE);
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
                //  adapter.getItem(position).setPhotoItems(infoBlockHelper.getItems().get(screenNum).get(position).getPhotoItems());
                try {
                    adapter.notifyItemChanged(position);
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
        if (adapter.getPositionByCode(key) > 0) {
            adapter.getItem(adapter.getPositionByCode(key)).setListValue(new ListItem(-1, getString(R.string.not_chosen)));
            adapter.notifyItemChanged(adapter.getPositionByCode(key));
        }
    }

}
