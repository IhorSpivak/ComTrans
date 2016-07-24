package ru.comtrans.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment implements View.OnClickListener{
    ListView listView;
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView defectsCount;
    TextView photosCount;
    EditText defectName;
    ImageView takePhoto, takeDefect, done;
    CameraPreviewFragment cameraPreviewFragment;
    PhotoViewerFragment photoViewerFragment;
    CountUpdateReceiver countUpdateReceiver = null;
    RePhotoReceiver rePhotoReceiver = null;
    ProgressBar progressBar;
    private CameraActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_camera,container,false);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        toolbarTitle  = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setSelected(true);




        activity = (CameraActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        takeDefect = (ImageView) v.findViewById(R.id.take_defect);
        takePhoto = (ImageView) v.findViewById(R.id.take_photo);
        done = (ImageView) v.findViewById(R.id.btn_done);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        defectsCount = (TextView)v.findViewById(R.id.defects_count);
        photosCount = (TextView)v.findViewById(R.id.photos_count);
        defectName  = (EditText)v.findViewById(R.id.defect_name);

        toolbarTitle.setOnClickListener(this);
        takeDefect.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        done.setOnClickListener(this);



        listView = (ListView)v.findViewById(android.R.id.list);




        setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());
        setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
        setProgressCount(activity.getPhotoAdapter().getPhotosCount());


        countUpdateReceiver = new CountUpdateReceiver();
        rePhotoReceiver = new RePhotoReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(countUpdateReceiver,new IntentFilter(Const.RECEIVE_UPDATE_COUNT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rePhotoReceiver,new IntentFilter(Const.RE_PHOTO));


        listView.setAdapter(activity.getPhotoAdapter());
        activity.getPhotoAdapter().setSelectedPosition(activity.imagePosition);
        Log.d("TAG","img pos"+activity.imagePosition);

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition(), 0);
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.getPhotoAdapter().setSelectedPosition(position);
                PhotoItem photoItem = activity.getPhotoAdapter().getItem(position);
                defectName.setVisibility(View.INVISIBLE);
                Utility.hideKeyboard(getActivity(),getActivity().getCurrentFocus());

                Log.d("TAG","imagepath "+photoItem.getImagePath());
                Log.d("TAG","camera preview "+getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW));
                Log.d("TAG","photo preview "+getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER));

                toolbarTitle.setText(photoItem.getTitle());


                if(photoItem.getImagePath()==null){
                    if(getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW)==null)
                         replaceWithCamera();
                }else {
                    if(getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER)==null){
                        replaceWithPhotoViewer(photoItem,position);
                    }else if(photoViewerFragment !=null&&!photoViewerFragment.getItem().getImagePath().equals(photoItem.getImagePath())){
                        replaceWithPhotoViewer(photoItem,position);
                    }
                }
            }
        });
        PhotoItem item = activity.getPhotoAdapter().getItem(activity.imagePosition);
        if(item.getImagePath()!=null){
            toolbarTitle.setText(item.getTitle());
            replaceWithPhotoViewer(item,activity.imagePosition);
        }else {
            replaceWithCamera();
        }


        if(!Utility.getBoolean(Const.IS_FIRST_CAMERA_LAUNCH))
        getFragmentManager().beginTransaction().add(R.id.container,new ViewPagerPhotoDemoFragment()).addToBackStack(null).commit();


        return v;
    }

    private void replaceWithCamera(){

        toolbarTitle.setText(activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition()).getTitle());
        cameraPreviewFragment = new CameraPreviewFragment();
        photoViewerFragment = null;
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,cameraPreviewFragment, Const.CAMERA_PREVIEW).commit();
    }
    private void replaceWithPhotoViewer(PhotoItem item,int position){

        photoViewerFragment = PhotoViewerFragment.newInstance(item,position);
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, photoViewerFragment,Const.PHOTO_VIEWER).commit();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_defect:
                if(photoViewerFragment!=null){
                    Toast.makeText(getActivity(),R.string.photo_blocked,Toast.LENGTH_SHORT).show();
                }else {
                    takePicture(true);
                }

                break;
            case R.id.take_photo:
                if(photoViewerFragment!=null){
                    Toast.makeText(getActivity(),R.string.photo_blocked,Toast.LENGTH_SHORT).show();
                }else {
                    if (!activity.getPhotoAdapter().isPositionDefect(activity.getPhotoAdapter().getSelectedPosition()))
                        takePicture(false);
                }
                break;
            case R.id.btn_done:
                done();
                break;
            case R.id.toolbarTitle:
                final PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                if(item.isDefect()&&item.getImagePath()!=null){
                    defectName.setVisibility(View.VISIBLE);
                    toolbarTitle.setVisibility(View.INVISIBLE);
                    defectName.setText(item.getTitle());
                    defectName.requestFocus();
                    defectName.setOnKeyListener(new View.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                if(!defectName.getText().toString().equals("")){
                                    item.setTitle(defectName.getText().toString());
                                    toolbarTitle.setText(defectName.getText().toString());
                                    toolbarTitle.setVisibility(View.VISIBLE);
                                    defectName.setVisibility(View.INVISIBLE);
                                    activity.getPhotoAdapter().setTitleForItem(item,activity.getPhotoAdapter().getSelectedPosition());
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    defectName.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(defectName, InputMethodManager.SHOW_IMPLICIT);

                            if (defectName.getText().length() > 0) {
                                defectName.setSelection(defectName.getText().length());
                            }
                        }
                    });
                }
                break;

        }
    }

    private void setDefectsCount(int count){
        defectsCount.setText(getResources().getQuantityString(R.plurals.defects_count,count,count));
    }

    private void setPhotosCount(int count){
        photosCount.setText(String.format(getString(R.string.photos_count),count,activity.getPhotoAdapter().getNonDefectPhotosCount()));
    }

    private void setProgressCount(int count){
        if(count!=0) {
            int percent = (int)((count * 100.0f) / activity.getPhotoAdapter().getCount());
            if(percent==100){
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.vertical_progressbar_green));
            }else {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.vertical_progressbar_red));
            }
            Log.d("TAG","currentProgress "+percent);
            progressBar.setProgress(percent);
        }else {
            progressBar.setProgress(0);
        }
    }

    private void takePicture(final boolean isDefect){
        switchButtons(false);
        try {
            cameraPreviewFragment.getCamera().takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    PhotoItem item = createFileFromData(data,isDefect);
                    switchButtons(true);
                    if(item!=null){
                        if(isDefect){

                            PhotoItem factItem = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                            Log.d("TAG","is defect "+factItem.isDefect()+" "+factItem.getImagePath()+" "+activity.getPhotoAdapter().getSelectedPosition());
                            if(factItem.getImagePath()==null) {
                                int suffix = activity.getPhotoAdapter().getDefectsCount();
                                activity.getPhotoAdapter().setDefectsCount(suffix);
                                String defectTitle = Utility.getSavedData(Const.DEFAULT_DEFECT_NAME)+" "+suffix;
                                item.setTitle(defectTitle);
                                activity.getPhotoAdapter().setItem(item, activity.getPhotoAdapter().getLastDefectPosition());
                                listView.setSelection(activity.getPhotoAdapter().getSelectedPosition());
                                setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());

                            }else {
                                factItem.setImagePath(item.getImagePath());
                                activity.getPhotoAdapter().setImagePathForItem(factItem,activity.getPhotoAdapter().getSelectedPosition());
                            }

                                activity.getPhotoAdapter().setSelectedPosition(1);

                                try {
                                    listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition() - 2, 0);
                                }catch (Exception ignored){}

                                replaceWithPhotoViewer(item,1);

                        }else {
                            item.setTitle(toolbarTitle.getText().toString());
                            activity.getPhotoAdapter().setItem(item,activity.getPhotoAdapter().getSelectedPosition());
                            setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
                            setProgressCount(activity.getPhotoAdapter().getPhotosCount());

                            int selectedPosition = activity.getPhotoAdapter().getSelectedPosition();
                            if(selectedPosition!=0)
                                selectedPosition--;
                            activity.getPhotoAdapter().setSelectedPosition(selectedPosition);
                            replaceWithCamera();

                            try {
                                listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition() - 2, 0);
                            }catch (Exception ignored){}

                        }



                    }
                }
            });
        }catch (Exception e){
            switchButtons(true);
            e.printStackTrace();
        }
    }

    /**
     * Method helps to block/unblock buttons when camera is in takePicture mode.
     * @param disableOrEnable
     *             pass true if you want all buttons to be enable
     *             or false to disable.
     */
    private void switchButtons(boolean disableOrEnable){
        takeDefect.setEnabled(disableOrEnable);
        takePhoto.setEnabled(disableOrEnable);
        done.setEnabled(disableOrEnable);
    }

    private void done(){
        Intent i = new Intent();
        i.putExtra(Const.EXTRA_POSITION,activity.position);
        i.putExtra(Const.EXTRA_IMAGE_POSITION,activity.imagePosition);
        Collections.reverse(activity.getPhotoAdapter().getItems());
        i.putExtra(Const.EXTRA_VALUES,activity.getPhotoAdapter().getItems());
        getActivity().setResult(Const.CAMERA_PHOTO_RESULT,i);
        getActivity().finish();
    }

    private PhotoItem createFileFromData(byte[] data,boolean isDefect){
        try {
            File directory = new File(Environment.getExternalStorageDirectory(),"/Comtrans/photos/");
            if(directory.mkdirs()||directory.exists()){
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String prefix = isDefect? getString(R.string.prefix_defect) : getString(R.string.prefix_photo);
                File photoFile = new File(directory, prefix + timeStamp + ".jpg");
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(data);
                fos.close();
                PhotoItem item = new PhotoItem();
                item.setDefect(isDefect);
                item.setImagePath(photoFile.getAbsolutePath());

                return item;

            }else {
                Toast.makeText(getActivity(),R.string.photo_save_error,Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class CountUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            setDefectsCount(activity.getPhotoAdapter().getFactDefectCount());
            setPhotosCount(activity.getPhotoAdapter().getPhotosCount());
            setProgressCount(activity.getPhotoAdapter().getPhotosCount());
            PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
            toolbarTitle.setText(item.getTitle());

            if(item.getImagePath()==null){
                replaceWithCamera();
            }else {
                replaceWithPhotoViewer(item,activity.getPhotoAdapter().getSelectedPosition());
            }

        }
    }

    private class RePhotoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("TAG","position "+listView.getSelectedItemPosition());
            replaceWithCamera();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(countUpdateReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rePhotoReceiver);
        rePhotoReceiver = null;
        countUpdateReceiver = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                done();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
