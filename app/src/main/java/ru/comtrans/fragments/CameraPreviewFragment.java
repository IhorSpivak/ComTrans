package ru.comtrans.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.comtrans.R;
import ru.comtrans.camera.CameraPreview;


public class CameraPreviewFragment extends BaseFragment {
    private static Camera camera;
    private CameraPreview mPreview;
    private FrameLayout mainLayout;




    public Camera getCamera(){
        return camera;
    }

    public CameraPreview getPreview() {
        return mPreview;
    }

    public void enableFlashLight(boolean isVideo,boolean enable){
        try {
            Camera.Parameters p = camera.getParameters();
            if(!enable){
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }else {
                if(isVideo) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }else {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                }
            }

            camera.setParameters(p);
            camera.startPreview();
        }catch (Exception ignored){}
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_preview,container,false);

        mainLayout = (FrameLayout)v.findViewById(R.id.main_layout);
//        mPreview = new CameraPreview(getActivity());
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER;
//        mPreview.setLayoutParams(params);
//
//        mainLayout.addView(mPreview);

        mPreview = (CameraPreview) v.findViewById(R.id.camera_preview);




        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG","onPause");
        releaseCamera();
    }



    public void onResume() {
        super.onResume();
        Log.d("TAG","onResume");

        if (!hasCamera(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            getActivity().finish();
        }
        if (camera == null) {
            camera = Camera.open(0);

            mPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mPreview.focusOnTouch(event);
                    }
                    return true;
                }
            });

            getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = getView().getWidth();
                    int height = getView().getHeight();
                    if(width!=0){

                        mPreview.setScreenSize(width,height);
                        mPreview.refreshCamera(camera);
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }
    }




    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    private void releaseCamera() {

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }






}
