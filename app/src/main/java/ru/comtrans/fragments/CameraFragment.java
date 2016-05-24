package ru.comtrans.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.comtrans.R;
import ru.comtrans.adapters.CameraPhotoAdapter;
import ru.comtrans.camera.CameraPreview;
import ru.comtrans.items.PhotoItem;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment {



    ListView listView;
    private static Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_camera,container,false);

        mPreview = (CameraPreview) v.findViewById(R.id.camera_preview);
        mPreview.setCamera(mCamera);


        listView = (ListView)v.findViewById(android.R.id.list);
        ArrayList<PhotoItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new PhotoItem());
        }
        CameraPhotoAdapter photoAdapter = new CameraPhotoAdapter(items,getActivity());
        listView.setAdapter(photoAdapter);




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
        if (mCamera == null) {
            mCamera = Camera.open(findBackFacingCamera());

            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
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

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(getActivity(), "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }



    //make picture and save to a folder
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "JCG Camera");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                boolean cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

}
