package ru.comtrans.fragments;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.R;
import ru.comtrans.activities.CameraActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.PhotoItem;

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
    String[] titles;
    private CameraActivity activity;
    PhotoFragment photoFragment;
    MediaRecorder mediaRecorder;
    boolean isVideoRecording = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_video,container,false);
        toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        toolbarTitle  = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setSelected(true);




        activity = (CameraActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");

        takeVideo = (ImageView) v.findViewById(R.id.take_video);
        done = (ImageView) v.findViewById(R.id.btn_done);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        videosCount = (TextView)v.findViewById(R.id.videos_count);

        toolbarTitle.setOnClickListener(this);
        takeVideo.setOnClickListener(this);
        done.setOnClickListener(this);



        listView = (ListView)v.findViewById(android.R.id.list);

        titles = getResources().getStringArray(R.array.video_functionality_test);

        toolbarTitle.setText(titles[0]);
        setVideosCount(0);
        setProgressCount(0);


        listView.setAdapter(activity.getPhotoAdapter());

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(activity.getPhotoAdapter().getCount());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.updatePositionInAdapter(position);
                PhotoItem photoItem = activity.getPhotoAdapter().getItem(position);


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
                    }else if(photoFragment!=null&&!photoFragment.getItem().getImagePath().equals(photoItem.getImagePath())){
                        replaceWithPhotoViewer(photoItem,position);
                    }
                }
            }
        });

        replaceWithCamera();

        if(!Utility.getBoolean(Const.IS_FIRST_CAMERA_LAUNCH))
            getFragmentManager().beginTransaction().add(R.id.container,new ViewPagerPhotoDemoFragment()).addToBackStack(null).commit();


        return v;
    }

    private void replaceWithCamera(){
        cameraPreviewFragment = new CameraPreviewFragment();
        photoFragment = null;
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,cameraPreviewFragment, Const.CAMERA_PREVIEW).commit();
    }
    private void replaceWithPhotoViewer(PhotoItem item,int position){
        photoFragment = PhotoFragment.newInstance(item,position);
        getFragmentManager().beginTransaction().replace(R.id.cameraContainer,photoFragment,Const.PHOTO_VIEWER).commit();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_video:
                takeVideo(activity.getPhotoAdapter().getSelectedPosition());
                break;
            case R.id.btn_done:
                getActivity().finish();
                break;


        }
    }


    private void setVideosCount(int count){
        videosCount.setText(String.format(getString(R.string.photos_count),count,titles.length));
    }

    private void setProgressCount(int count){
        if(count!=0) {
            int percent = (int)((count * 100.0f) / titles.length);
            Log.d("TAG","currentProgress "+percent);
            progressBar.setProgress(percent);
        }else {
            progressBar.setProgress(0);
        }
    }

    private void takeVideo(final int selectedPosition){

        if(isVideoRecording){
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                releaseMediaRecorder();
            }
        }else {
            if (prepareVideoRecorder()) {
                isVideoRecording = true;
                mediaRecorder.start();
            } else {
                releaseMediaRecorder();
            }
        }


    }


    @Override
    public void onPause() {
        super.onPause();
        isVideoRecording = true;
    }

    private boolean prepareVideoRecorder() {

        File directory = new File(Environment.getExternalStorageDirectory(),"/Comtrans/videos/");
        if(directory.mkdirs()||directory.exists()){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String prefix = getString(R.string.prefix_video);
            File videoFile = new File(directory, prefix + timeStamp + ".mp4");

            cameraPreviewFragment.getCamera().unlock();

            mediaRecorder = new MediaRecorder();

            mediaRecorder.setCamera(cameraPreviewFragment.getCamera());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mediaRecorder.setPreviewDisplay(cameraPreviewFragment.getPreview().getHolder().getSurface());

            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                releaseMediaRecorder();
                return false;
            }
            return true;


        }else {
            Toast.makeText(getActivity(),R.string.photo_save_error,Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            cameraPreviewFragment.getCamera().lock();
        }
    }

}
