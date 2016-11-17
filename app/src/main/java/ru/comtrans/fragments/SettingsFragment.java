package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ru.comtrans.BuildConfig;
import ru.comtrans.R;
import ru.comtrans.helpers.Utility;

/**
 * Created by Artco on 02.06.2016.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        TextView tvBuildNumber = (TextView)v.findViewById(R.id.tv_build_num);
        Switch swAllowBigData = (Switch) v.findViewById(R.id.sw_allow_big_data);
        Switch swAllowMobileConnection = (Switch) v.findViewById(R.id.sw_allow_mob_connection);

        swAllowMobileConnection.setOnCheckedChangeListener(this);
        swAllowBigData.setOnCheckedChangeListener(this);

        swAllowBigData.setChecked(Utility.isAllowBigData());
        swAllowMobileConnection.setChecked(Utility.isAllowMobileConnection());



        tvBuildNumber.setText(BuildConfig.VERSION_NAME+"("+BuildConfig.VERSION_CODE+")");


        return v;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.sw_allow_big_data:
                Utility.setIsAllowBigData(isChecked);
                break;
            case R.id.sw_allow_mob_connection:
                Utility.setIsAllowMobileConnection(isChecked);
                break;
        }
    }
}
