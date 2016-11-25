package ru.comtrans.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.PhotoItem;
import ru.comtrans.listeners.SimpleOrientationListener;
import ru.comtrans.services.AudioRecordService;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.tasks.SaveInfoBlockTask;
import ru.comtrans.views.CircleProgressBar;

/**
 * Created by Artco on 01.06.2016.
 */
public class VideoFragment extends Fragment implements View.OnClickListener{
    ListView listView;
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView videosCount;
    ImageView takeVideo, done;
    CameraPreviewFragment cameraPreviewFragment;
    ProgressBar progressBar;
    private TakePhotoReceiver takePhotoReceiver = null;

    private CameraActivity activity;
    VideoViewerFragment videoViewerFragment;
    MediaRecorder mediaRecorder;
    boolean isVideoRecording = false;
    private Chronometer chronometer;
    int currentPosition;
    File videoFile;
    CountUpdateReceiver countUpdateReceiver = null;
    RePhotoReceiver rePhotoReceiver = null;
    private MenuItem menuItem;
    private SimpleOrientationListener mOrientationListener;
    private RelativeLayout rlPortraitBlocked;
    private CircleProgressBar videoProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().stopService(new Intent(getContext(), AudioRecordService.class));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_video,container,false);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        toolbarTitle  = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setSelected(true);

        Log.d("TAG","video fragment");


        activity = (CameraActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");

        takeVideo = (ImageView) v.findViewById(R.id.take_video);
        done = (ImageView) v.findViewById(R.id.btn_done);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        videosCount = (TextView)v.findViewById(R.id.videos_count);
        chronometer = (Chronometer) v.findViewById(R.id.chronometer);
        rlPortraitBlocked = (RelativeLayout)v.findViewById(R.id.rlPortraitBlocked);
        videoProgressBar = (CircleProgressBar)v.findViewById(R.id.videoProgressBar);

        toolbarTitle.setOnClickListener(this);
        takeVideo.setOnClickListener(this);
        done.setOnClickListener(this);





        listView = (ListView)v.findViewById(android.R.id.list);

        PhotoItem item = activity.getPhotoAdapter().getItem(activity.imagePosition);

        toolbarTitle.setText(item.getTitle());
        setVideosCount(activity.getPhotoAdapter().getPhotosCount());
        setProgressCount(activity.getPhotoAdapter().getPhotosCount());
        currentPosition = activity.imagePosition;


        listView.setAdapter(activity.getPhotoAdapter());
        activity.getPhotoAdapter().setSelectedPosition(activity.imagePosition);
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition(), 0);
            }
        });

        countUpdateReceiver = new CountUpdateReceiver();
        rePhotoReceiver = new RePhotoReceiver();
        takePhotoReceiver = new TakePhotoReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(takePhotoReceiver,new IntentFilter(Const.TAKE_PHOTO_BROADCAST));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(countUpdateReceiver,new IntentFilter(Const.RECEIVE_UPDATE_COUNT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(rePhotoReceiver,new IntentFilter(Const.RE_PHOTO));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.getPhotoAdapter().setSelectedPosition(position);
                PhotoItem photoItem = activity.getPhotoAdapter().getItem(position);
                currentPosition = position;



                toolbarTitle.setText(photoItem.getTitle());
                if(photoItem.getImagePath()==null){
                    if(getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW)==null)
                        replaceWithCamera();
                }else {
                    if(getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER)==null){
                        replaceWithVideoViewer(photoItem,position);
                    }else if(videoViewerFragment !=null&&!videoViewerFragment.getItem().getImagePath().equals(photoItem.getImagePath())){
                        replaceWithVideoViewer(photoItem,position);
                    }
                }
            }
        });

        PhotoItem photoItem = activity.getPhotoAdapter().getItem(activity.imagePosition);
        if(photoItem.getImagePath()==null){
            if(getFragmentManager().findFragmentByTag(Const.CAMERA_PREVIEW)==null)
                replaceWithCamera();
        }else {
            if(getFragmentManager().findFragmentByTag(Const.PHOTO_VIEWER)==null){
                replaceWithVideoViewer(photoItem,activity.imagePosition);
            }else if(videoViewerFragment !=null&&!videoViewerFragment.getItem().getImagePath().equals(photoItem.getImagePath())){
                replaceWithVideoViewer(photoItem,activity.imagePosition);
            }
        }



        mOrientationListener = new SimpleOrientationListener(
                getActivity()) {

            @Override
            public void onSimpleOrientationChanged(int orientation) {
                try {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Log.d("TAG", "landscape");
                        switchButtons(true,true);
                        rlPortraitBlocked.setVisibility(View.GONE);
                    } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        switchButtons(false,true);
                        if(isVideoRecording){
                            takeVideo();
                            isVideoRecording = false;
                            chronometer.setBase(SystemClock.elapsedRealtime());
                        }
                        rlPortraitBlocked.setVisibility(View.VISIBLE);
                        Log.d("TAG", "portrait");
                    }
                }catch (Exception ignored){}
            }
        };
        mOrientationListener.enable();



//        if(!Utility.getBoolean(Const.IS_FIRST_CAMERA_LAUNCH))
//            getFragmentManager().beginTransaction().add(R.id.container,new ViewPagerPhotoDemoFragment()).addToBackStack(null).commit();


        return v;
    }

    private void replaceWithCamera(){
        cameraPreviewFragment = CameraPreviewFragment.newInstance(true);
        videoViewerFragment = null;
        toolbarTitle.setText(activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition()).getTitle());
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,cameraPreviewFragment, Const.CAMERA_PREVIEW).commit();
        if(menuItem!=null)
            menuItem.setVisible(true);
    }
    private void replaceWithVideoViewer(PhotoItem item, int position){
        videoViewerFragment = VideoViewerFragment.newInstance(item,position);
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, videoViewerFragment,Const.PHOTO_VIEWER).commit();
        if(menuItem!=null)
            menuItem.setVisible(false);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_video:
                if(videoViewerFragment!=null){
                    Toast.makeText(getActivity(),R.string.video_blocked,Toast.LENGTH_SHORT).show();
                }else {

                    takeVideo();
                }

                break;
            case R.id.btn_done:
                done(true);
                break;


        }
    }


    private void setVideosCount(int count){
        videosCount.setText(String.format(getString(R.string.videos_count),count,activity.getPhotoAdapter().getCount()));
    }

    private void setProgressCount(int count){
        if(count!=0) {
            int percent = (int)((count * 100.0f) / activity.getPhotoAdapter().getCount());
            if(percent==100){
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.vertical_progressbar_green));
            }else {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.vertical_progressbar_red));
            }
            progressBar.setProgress(percent);
        }else {
            progressBar.setProgress(0);
        }
    }

    private void takeVideo(){
        Log.d("TAG","take video");
        if(isVideoRecording){
            videoProgressBar.setProgressWithAnimation(0);
            if (mediaRecorder != null) {
                chronometer.setOnChronometerTickListener(null);
                isVideoRecording = false;
                try{
                    mediaRecorder.stop();
                }catch (Exception e){
                    Log.e("TAG","err",e);
                }

                releaseMediaRecorder();
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.setVisibility(View.INVISIBLE);
                PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                if(item.getImagePath()!=null&&!item.getImagePath().equals("")){
                    File file = new File(item.getImagePath());
                    file.delete();
                }

                item.setVideo(true);
                item.setImagePath(videoFile.getAbsolutePath());
                item.setTitle(toolbarTitle.getText().toString());
                activity.getPhotoAdapter().setItem(item,currentPosition);
                setVideosCount(activity.getPhotoAdapter().getPhotosCount());
                setProgressCount(activity.getPhotoAdapter().getPhotosCount());
                int selectedPosition = activity.getPhotoAdapter().getSelectedPosition();
                if(selectedPosition!=0){
                    selectedPosition--;
                    activity.getPhotoAdapter().setSelectedPosition(selectedPosition);
                    currentPosition = selectedPosition;
                    replaceWithCamera();
                }else {
                    activity.getPhotoAdapter().setSelectedPosition(selectedPosition);
                    currentPosition = selectedPosition;
                    replaceWithVideoViewer(item,currentPosition);
                }

                try {
                    listView.smoothScrollToPositionFromTop(activity.getPhotoAdapter().getSelectedPosition() - 2, 0);
                }catch (Exception ignored){}


                done(false);
              //  replaceWithVideoViewer(item,currentPosition);
            }
        }else {

            if (prepareVideoRecorder()) {
                isVideoRecording = true;
                mediaRecorder.start();
                chronometer.setVisibility(View.VISIBLE);
                chronometer.post(new Runnable() {
                    @Override
                    public void run() {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                    }
                });
                PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
                final double duration = item.getDuration()*1000;
                chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        Log.d("TAG", String.valueOf(elapsedMillis));
                        double progress = (elapsedMillis / duration)*100;
                        Log.d("TAG", "progress "+String.valueOf(progress));
                        videoProgressBar.setProgressWithAnimation((float) progress);

                        if(elapsedMillis>=duration&&chronometer.getBase()!=0){
                            takeVideo();
                        }
                    }
                });

            } else {
                releaseMediaRecorder();
            }
        }


    }


    @Override
    public void onPause() {
        super.onPause();
        if(isVideoRecording){
            takeVideo();
            isVideoRecording = false;
            chronometer.setBase(SystemClock.elapsedRealtime());
        }

    }

    private boolean prepareVideoRecorder() {

        try {
            File directory = new File(Environment.getExternalStorageDirectory(), "Android/data/ru.comtrans/files/Videos");
            if (directory.mkdirs() || directory.exists()) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String prefix = getString(R.string.prefix_video);
                File file = new File(directory, prefix + timeStamp + ".mp4");
                videoFile = file;

                cameraPreviewFragment.getCamera().unlock();

                mediaRecorder = new MediaRecorder();

                mediaRecorder.setCamera(cameraPreviewFragment.getCamera());
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setVideoFrameRate(24); //might be auto-determined due to lighting
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// MPEG_4_SP
                mediaRecorder.setVideoEncodingBitRate(3000000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setVideoSize(640, 480);
                mediaRecorder.setOutputFile(file.getAbsolutePath());
                mediaRecorder.setPreviewDisplay(cameraPreviewFragment.getPreview().getHolder().getSurface());

                try {
                    mediaRecorder.prepare();
                } catch (Exception e) {
                    Log.e("TAG","err",e);
                    releaseMediaRecorder();
                    return false;
                }
                return true;


            } else {
                Toast.makeText(getActivity(), R.string.photo_save_error, Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
            Log.e("TAG","err",e);
        }
        return false;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            cameraPreviewFragment.getCamera().lock();
        }
    }

    private void switchButtons(boolean disableOrEnable,boolean isFromOrientation){
        if(disableOrEnable){
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(takePhotoReceiver,new IntentFilter(Const.TAKE_PHOTO_BROADCAST));
        }else {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(takePhotoReceiver);
        }

        takeVideo.setClickable(disableOrEnable);
        if(!isFromOrientation)
        done.setClickable(disableOrEnable);
    }

    private class TakePhotoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(videoViewerFragment!=null){
                Toast.makeText(getActivity(),R.string.video_blocked,Toast.LENGTH_SHORT).show();
            }else {
                try{
                    LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(takePhotoReceiver);
                    takeVideo();

                }catch (Exception e){}
            }


        }
    }

    private class CountUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setVideosCount(activity.getPhotoAdapter().getPhotosCount());
            setProgressCount(activity.getPhotoAdapter().getPhotosCount());
            PhotoItem item = activity.getPhotoAdapter().getItem(activity.getPhotoAdapter().getSelectedPosition());
            toolbarTitle.setText(item.getTitle());

            if(item.getImagePath()==null){
                replaceWithCamera();
            }else {
                replaceWithVideoViewer(item,currentPosition);
            }
            done(false);


        }
    }

    public void done(boolean isDone){
        if(isDone) {
            Intent i = new Intent();
            i.putExtra(Const.EXTRA_POSITION, activity.position);
            i.putExtra(Const.EXTRA_IMAGE_POSITION, activity.imagePosition);
            i.putExtra(Const.EXTRA_SCREEN_NUM, activity.screenNum);
            ArrayList<PhotoItem> items = new ArrayList<>(activity.getPhotoAdapter().getItems());
            Collections.reverse(items);
            InfoBlockHelper helper = InfoBlockHelper.getInstance();
            helper.getItems().get(activity.screenNum).get(activity.position).setPhotoItems(items);
            getActivity().setResult(Const.CAMERA_PHOTO_RESULT, i);
            getActivity().finish();
        }else {
            ArrayList<PhotoItem> items = new ArrayList<>(activity.getPhotoAdapter().getItems());
            Collections.reverse(items);
            InfoBlockHelper helper = InfoBlockHelper.getInstance();
            helper.getItems().get(activity.screenNum).get(activity.position).setPhotoItems(items);
            new SaveInfoBlockTask(helper.getId(),getContext());
        }
    }

    private class RePhotoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("TAG","position "+listView.getSelectedItemPosition());
            currentPosition = activity.getPhotoAdapter().getSelectedPosition();
            replaceWithCamera();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOrientationListener.disable();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(countUpdateReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rePhotoReceiver);
        rePhotoReceiver = null;
        countUpdateReceiver = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_camera,menu);
        menuItem = menu.findItem(R.id.action_flash);
        if(Utility.getBoolean(Const.IS_FLASH_ENABLED)){
            menuItem.setIcon(R.drawable.ic_action_flash_on);
        }else {
            menuItem.setIcon(R.drawable.ic_action_flash_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                done(true);
                return true;
            case R.id.action_flash:
                if(Utility.getBoolean(Const.IS_FLASH_ENABLED)){
                    Utility.saveBoolean(Const.IS_FLASH_ENABLED,false);
                    cameraPreviewFragment.enableFlashLight();
                    item.setIcon(R.drawable.ic_action_flash_off);
                }else {
                    Utility.saveBoolean(Const.IS_FLASH_ENABLED,true);
                    cameraPreviewFragment.enableFlashLight();
                    item.setIcon(R.drawable.ic_action_flash_on);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
