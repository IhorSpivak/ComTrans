package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.comtrans.R;

/**
 * Created by Artco on 24.05.2016.
 */
public class CameraFragment extends Fragment {

    private Button capture;
    private CameraPreviewFragment cameraPreviewFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_camera,container,false);

        cameraPreviewFragment = new CameraPreviewFragment();

        getFragmentManager().beginTransaction().add(R.id.previewContainer, cameraPreviewFragment).commit();

        capture = (Button) v.findViewById(R.id.button_capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return v;
    }
}
