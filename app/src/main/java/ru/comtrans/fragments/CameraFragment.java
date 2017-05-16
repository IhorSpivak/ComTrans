package ru.comtrans.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.tutoshowcase.TutoShowcase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.ImageHelper;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.listeners.SimpleOrientationListener;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.tasks.SaveInfoBlockTask;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView defectsCount;
    private TextView photosCount;
    private EditText defectName;
    private ImageView takePhoto, takeDefect, done;
    private CameraPreviewFragment cameraPreviewFragment;
    private PhotoViewerFragment photoViewerFragment;
    private CountUpdateReceiver countUpdateReceiver = null;
    private RePhotoReceiver rePhotoReceiver = null;
    private TakePhotoReceiver takePhotoReceiver = null;
    private ProgressBar progressBar;
    private CameraActivity activity;
    private MenuItem menuItem;
    private SimpleOrientationListener mOrientationListener;
    private RelativeLayout rlPortraitBlocked;
    private boolean isRePhoto = false;
    private int currentRePhotoPosition = -1;
    InfoBlockHelper helper = InfoBlockHelper.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_camera, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setSelected(true);


        activity = (CameraActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        takeDefect = (ImageView) v.findViewById(R.id.take_defect);
        takePhoto = (ImageView) v.findViewById(R.id.take_photo);
        done = (ImageView) v.findViewById(R.id.btn_done);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        defectsCount = (TextView) v.findViewById(R.id.defects_count);
        photosCount = (TextView) v.findViewById(R.id.photos_count);
        defectName = (EditText) v.findViewById(R.id.defect_name);
        rlPortraitBlocked = (RelativeLayout) v.findViewById(R.id.rlPortraitBlocked);

        toolbarTitle.setOnClickListener(this);
        takeDefect.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        done.setOnClickListener(this);


        listView = (ListView) v.findViewById(android.R.id.list);


        if (!activity.getPhotoAdapter().isPositionDefect(0)) {
            defectsCount.setVisibility(View.GONE);
            takeDefect.setVisibility(View.INVISIBLE);
        }
        setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());
        setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
        setProgressCount(activity.getPhotoAdapter().getPhotosCount());


        countUpdateReceiver = new CountUpdateReceiver();
        rePhotoReceiver = new RePhotoReceiver();
        takePhotoReceiver = new TakePhotoReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(takePhotoReceiver, new IntentFilter(Const.TAKE_PHOTO_BROADCAST));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(countUpdateReceiver, new IntentFilter(Const.RECEIVE_UPDATE_COUNT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rePhotoReceiver, new IntentFilter(Const.RE_PHOTO));


        listView.setAdapter(activity.getPhotoAdapter());
        activity.getPhotoAdapter().setSelectedPosition(activity.imagePosition);


        //   Log.d("TAG","img pos "+activity.imagePosition+" count "+activity.getPhotoAdapter().getCount());
        listView.post(new Runnable() {
            @Override
            public void run() {
                if (activity.imagePosition != activity.getPhotoAdapter().getCount() - 1)
                    listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition(), 0);
                else
                    listView.smoothScrollToPosition(activity.getPhotoAdapter().getSelectedPosition());
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentRePhotoPosition != position) {
                    isRePhoto = false;
                    currentRePhotoPosition = -1;
                }
                activity.getPhotoAdapter().setSelectedPosition(position);
                PhotoItem photoItem = activity.getPhotoAdapter().getItem(position);
                defectName.setVisibility(View.INVISIBLE);
                Utility.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                boolean isDefect = photoItem.isDefect();
                Log.d("TAG", "imagepath " + photoItem.getImagePath());
                Log.d("TAG", "camera preview " + getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW));
                Log.d("TAG", "photo preview " + getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER));

                if (!isDefect) {
                    toolbarTitle.setText(photoItem.getTitle());
                } else {
                    if (photoItem.getImagePath() != null) {
                        if (photoItem.isEdited()) {
                            toolbarTitle.setText(photoItem.getTitle());
                        } else {
                            toolbarTitle.setText(photoItem.getTitle() + " " + getString(R.string.default_defect_name_suffix));
                        }
                    } else {
                        toolbarTitle.setText(photoItem.getTitle());
                    }

                }


                if (photoItem.getImagePath() == null) {
                    if (getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW) == null)
                        replaceWithCamera();
                } else {
                    if (getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER) == null) {
                        replaceWithPhotoViewer(position);
                    } else if (photoViewerFragment != null && !photoViewerFragment.getItem().getImagePath().equals(photoItem.getImagePath())) {
                        replaceWithPhotoViewer(position);
                    }
                }
            }
        });
        PhotoItem item = activity.getPhotoAdapter().getItem(activity.imagePosition);
        if (item.getImagePath() != null) {
            replaceWithPhotoViewer(activity.imagePosition);
        } else {
            replaceWithCamera();
        }

        if (!item.isDefect()) {
            toolbarTitle.setText(item.getTitle());
        } else {
            if (item.getImagePath() != null) {
                if (item.isEdited()) {
                    toolbarTitle.setText(item.getTitle());
                } else {
                    toolbarTitle.setText(item.getTitle() + " " + getString(R.string.default_defect_name_suffix));
                }
            } else {
                toolbarTitle.setText(item.getTitle());
            }
        }

        mOrientationListener = new SimpleOrientationListener(
                getActivity()) {

            @Override
            public void onSimpleOrientationChanged(int orientation) {
                try {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Log.d("TAG", "landscape");
                        switchButtons(true, true);
                        rlPortraitBlocked.setVisibility(View.GONE);
                    } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        switchButtons(false, true);
                        rlPortraitBlocked.setVisibility(View.VISIBLE);
                        Log.d("TAG", "portrait");
                    }
                } catch (Exception ignored) {
                }
            }
        };


        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity.isFromDefect) {
            if (!Utility.getBoolean(Const.IS_FIRST_CAMERA_DEFECT_LAUNCH)) {
                final TutorialPagerFragment fragment = TutorialPagerFragment.newInstance(Const.EXTRA_TUTORIAL_PHOTO_DEFECT);
                fragment.setListener(new TutorialPagerFragment.OnTutorialListener() {
                    @Override
                    public void onPositionChanged(int position) {

                    }

                    @Override
                    public void onOkClick() {
                        getFragmentManager().beginTransaction().remove(fragment).commit();
                        mOrientationListener.enable();
                        Utility.saveBoolean(Const.IS_FIRST_CAMERA_DEFECT_LAUNCH, true);
                    }
                });
                getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commitAllowingStateLoss();

            } else {
                mOrientationListener.enable();
            }
        } else {
            if (!Utility.getBoolean(Const.IS_FIRST_CAMERA_LAUNCH)) {
                final TutorialPagerFragment fragment = TutorialPagerFragment.newInstance(Const.EXTRA_TUTORIAL_PHOTO);
                fragment.setListener(new TutorialPagerFragment.OnTutorialListener() {
                    @Override
                    public void onPositionChanged(int position) {

                    }

                    @Override
                    public void onOkClick() {
                        getFragmentManager().beginTransaction().remove(fragment).commit();
                        mOrientationListener.enable();
                        Utility.saveBoolean(Const.IS_FIRST_CAMERA_LAUNCH, true);

                    }
                });
                getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commitAllowingStateLoss();
            } else {
                mOrientationListener.enable();
            }
        }

    }


    private void replaceWithCamera() {
        if (isRePhoto) {
            currentRePhotoPosition = activity.getPhotoAdapter().getSelectedPosition();
        }
        toolbarTitle.setText(activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition()).getTitle());
        cameraPreviewFragment = CameraPreviewFragment.newInstance(false);
        photoViewerFragment = null;
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, cameraPreviewFragment, Const.CAMERA_PREVIEW).commitAllowingStateLoss();

        if (menuItem != null)
            menuItem.setVisible(true);
    }

    private void replaceWithPhotoViewer(int position) {
        photoViewerFragment = PhotoViewerFragment.newInstance(activity.getPhotoAdapter().getItem(position), position);
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, photoViewerFragment, Const.PHOTO_VIEWER).commitAllowingStateLoss();
        if (menuItem != null)
            menuItem.setVisible(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_defect:
                if (photoViewerFragment != null) {
                    Toast.makeText(getActivity(), R.string.photo_blocked, Toast.LENGTH_SHORT).show();
                } else {
                    takePicture(true);
                }

                break;
            case R.id.take_photo:
                if (photoViewerFragment != null) {
                    Toast.makeText(getActivity(), R.string.photo_blocked, Toast.LENGTH_SHORT).show();
                } else {
                    if (!activity.getPhotoAdapter().isPositionDefect(activity.getPhotoAdapter().getSelectedPosition()))
                        takePicture(false);
                }
                break;
            case R.id.btn_done:
                done(true);
                break;
            case R.id.toolbarTitle:
                final PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                if (item.isDefect() && item.getImagePath() != null) {
                    defectName.setVisibility(View.VISIBLE);
                    toolbarTitle.setVisibility(View.INVISIBLE);
                    defectName.setText(item.getTitle());
                    defectName.requestFocus();
                    defectName.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                String enteredName = defectName.getText().toString().trim();
                                if (!enteredName.equals("")) {
                                    if (!enteredName.equals(item.getTitle())) {
                                        item.setTitle(enteredName);
                                        toolbarTitle.setText(enteredName);
                                        item.setEdited(true);
                                        activity.getPhotoAdapter().setTitleForItem(item, activity.getPhotoAdapter().getSelectedPosition());
                                    }
                                }
                                toolbarTitle.setVisibility(View.VISIBLE);
                                defectName.setVisibility(View.INVISIBLE);
                                return true;

                            }
                            return false;
                        }
                    });
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(defectName, InputMethodManager.SHOW_IMPLICIT);

                    if (defectName.getText().length() > 0) {
                        defectName.setSelection(defectName.getText().length());
                    }


                }
                break;


        }
    }


    private void setDefectsCount(int count) {
        defectsCount.setText(getResources().getQuantityString(R.plurals.defects_count, count, count));
    }

    private void setPhotosCount(int count) {
        photosCount.setText(String.format(getString(R.string.photos_count), count, activity.getPhotoAdapter().getNonDefectPhotosCount()));
    }

    private void setProgressCount(int count) {
        if (count != 0) {
            int percent = (int) ((count * 100.0f) / activity.getPhotoAdapter().getNonDefectPhotosCount());
            if (percent == 100) {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vertical_progressbar_green));
            } else {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vertical_progressbar_red));
            }
            Log.d("TAG", "currentProgress " + percent);
            progressBar.setProgress(percent);
        } else {
            progressBar.setProgress(0);
        }
    }

    private void takePicture(final boolean isDefect) {
        switchButtons(false, false);
        try {
            cameraPreviewFragment.getCamera().takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {


                    if (isDefect) {
                        isRePhoto = false;
                        currentRePhotoPosition = -1;
                        activity.getPhotoAdapter().setSelectedPosition(0);
                        createFileFromData(data, true);
                        int suffix = activity.getPhotoAdapter().getDefectsCount();
                        activity.getPhotoAdapter().setDefectsCount(suffix);
                        setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());
                        activity.getPhotoAdapter().setSelectedPosition(0);
                        toolbarTitle.setText(activity.getPhotoAdapter().getItem(0).getTitle() + " " + getString(R.string.default_defect_name_suffix));

                        try {
                            listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition() - 2, 0);
                        } catch (Exception ignored) {
                        }

                        replaceWithCamera();

                    } else {
                        if (isRePhoto) {
                            PhotoItem item = activity.getPhotoAdapter().getItem(currentRePhotoPosition);
                            item.incrementRePhotoCount();
                            activity.getPhotoAdapter().setItem(item, currentRePhotoPosition);
                            currentRePhotoPosition = -1;
                            isRePhoto = false;
                        }
                        createFileFromData(data, false);
                        setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
                        setProgressCount(activity.getPhotoAdapter().getPhotosCount());

                        int selectedPosition = activity.getPhotoAdapter().getSelectedPosition();
                        if (selectedPosition != 0) {
                            selectedPosition--;
                            while (activity.getPhotoAdapter().isImagePathNull(selectedPosition)) {
                                if (selectedPosition != 0) {
                                    selectedPosition--;
                                } else {
                                    break;
                                }
                            }

                        }

                        activity.getPhotoAdapter().setSelectedPosition(selectedPosition);
                        replaceWithCamera();

                        try {
                            listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition() - 2, 0);
                        } catch (Exception ignored) {
                        }

                    }

                    done(false);


                }
            });
        } catch (Exception e) {
            switchButtons(true, false);
            e.printStackTrace();
        }


        switchButtons(true, false);
    }

    /**
     * Method helps to block/unblock buttons when camera is in takePicture mode.
     *
     * @param disableOrEnable pass true if you want all buttons to be enable
     *                        or false to disable.
     */
    private void switchButtons(boolean disableOrEnable, boolean isFromOrientation) {
        if (disableOrEnable) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(takePhotoReceiver, new IntentFilter(Const.TAKE_PHOTO_BROADCAST));
        } else {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(takePhotoReceiver);
        }

        takeDefect.setClickable(disableOrEnable);
        takePhoto.setClickable(disableOrEnable);
        if (!isFromOrientation)
            done.setClickable(disableOrEnable);
    }


    public void done(boolean isDone) {
        if (isDone && isRePhoto) {
            replaceWithPhotoViewer(currentRePhotoPosition);
            currentRePhotoPosition = -1;
            isRePhoto = false;
        } else if (isDone) {
            Intent i = new Intent();
            i.putExtra(Const.EXTRA_POSITION, activity.position);
            i.putExtra(Const.EXTRA_IMAGE_POSITION, activity.imagePosition);
            i.putExtra(Const.EXTRA_SCREEN_NUM, activity.screenNum);
            i.putExtra(Const.EXTRA_IS_DEFECT, activity.isFromDefect);
            ArrayList<PhotoItem> items = new ArrayList<>(activity.getPhotoAdapter().getItems());
            Collections.reverse(items);
            saveData(items);
            if (getActivity().getParent() == null) {
                getActivity().setResult(Const.CAMERA_PHOTO_RESULT, i);
            } else {
                getActivity().getParent().setResult(Const.CAMERA_PHOTO_RESULT, i);
            }
            getActivity().finish();
            SaveInfoBlockTask.getInstance(helper.getId(), getContext());

        } else {
            ArrayList<PhotoItem> items = new ArrayList<>(activity.getPhotoAdapter().getItems());
            Collections.reverse(items);
            saveData(items);
            SaveInfoBlockTask.getInstance(helper.getId(), getContext());
        }
    }

    private void saveData(ArrayList<PhotoItem> items){
        if (activity.defectSize == 0) {
            helper.savePhotos(activity.screenNum, activity.position, items);
        } else {
            ArrayList<PhotoItem> photoItems = new ArrayList<>();
            ArrayList<PhotoItem> defectItems = new ArrayList<>();
            for (int j = 0; j < activity.itemsSize; j++) {
                photoItems.add(items.get(j));
            }
            for (int j = activity.itemsSize; j < items.size(); j++) {
                defectItems.add(items.get(j));
            }
            if (activity.isFromDefect) {
                helper.savePhotos(activity.screenNum, activity.position - 2, photoItems);
                helper.savePhotos(activity.screenNum, activity.position, defectItems);
            }else{
                helper.savePhotos(activity.screenNum, activity.position, photoItems);
                helper.savePhotos(activity.screenNum, activity.position + 2, defectItems);
            }
        }
    }

    private void createFileFromData(byte[] data, boolean isDefect) {
        try {
            File directory = new File(Environment.getExternalStorageDirectory(), "Android/data/ru.comtrans/files/Pictures");
            if (directory.mkdirs() || directory.exists()) {
                Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap resized = ImageHelper.scaleDown(original);
                //  original.recycle();


                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String prefix = isDefect ? getString(R.string.prefix_defect) : getString(R.string.prefix_photo);
                File photoFile = new File(directory, prefix + timeStamp + ".jpg");
                FileOutputStream fos = new FileOutputStream(photoFile);
                resized.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                fos.flush();
                fos.close();

                ImageHelper.compressBitmap(photoFile.getAbsolutePath());


                PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                item.setDefect(isDefect);
                item.setImagePath(photoFile.getAbsolutePath());
                activity.getPhotoAdapter().setItem(item, activity.getPhotoAdapter().getSelectedPosition());


            } else {
                Toast.makeText(getActivity(), R.string.photo_save_error, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private class CountUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());
            setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
            setProgressCount(activity.getPhotoAdapter().getPhotosCount());
            PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
            toolbarTitle.setText(item.getTitle());

            if (item.getImagePath() == null) {
                replaceWithCamera();
            } else {
                replaceWithPhotoViewer(activity.getPhotoAdapter().getSelectedPosition());
            }

        }
    }

    private class RePhotoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isRePhoto = true;
            replaceWithCamera();

        }
    }

    private class TakePhotoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (photoViewerFragment != null) {
                Toast.makeText(getActivity(), R.string.photo_blocked, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(takePhotoReceiver);
                    takePicture(activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition()).isDefect());

                } catch (Exception e) {
                }
            }


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrientationListener.disable();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(countUpdateReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(takePhotoReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rePhotoReceiver);
        rePhotoReceiver = null;
        countUpdateReceiver = null;
        takePhotoReceiver = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_camera, menu);
        menuItem = menu.findItem(R.id.action_flash);
        if (Utility.getBoolean(Const.IS_FLASH_ENABLED)) {
            menuItem.setIcon(R.drawable.ic_action_flash_on);
        } else {
            menuItem.setIcon(R.drawable.ic_action_flash_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (defectName.getVisibility() != View.VISIBLE) {
                    done(true);
                } else {
                    toolbarTitle.setVisibility(View.VISIBLE);
                    defectName.setVisibility(View.INVISIBLE);
                }

                return true;
            case R.id.action_flash:
                if (Utility.getBoolean(Const.IS_FLASH_ENABLED)) {
                    Utility.saveBoolean(Const.IS_FLASH_ENABLED, false);
                    cameraPreviewFragment.enableFlashLight();
                    item.setIcon(R.drawable.ic_action_flash_off);
                } else {
                    Utility.saveBoolean(Const.IS_FLASH_ENABLED, true);
                    cameraPreviewFragment.enableFlashLight();
                    item.setIcon(R.drawable.ic_action_flash_on);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
