package com.xelentec.a100fur.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xelentec.a100fur.R;
import com.xelentec.a100fur.helpers.RequestTask;
import com.xelentec.a100fur.items.ResponseItem;

/**
 * Created by Artco on 15.04.2016.
 */
public class ProfileFragment extends Fragment {

    EditText etFio, etCompany,etPosition,etTelephone;
    Button btnSaveProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        etFio = (EditText) v.findViewById(R.id.et_fio);
        etCompany = (EditText)v.findViewById(R.id.et_company);
        etPosition = (EditText)v.findViewById(R.id.et_company_position);
        etTelephone = (EditText)v.findViewById(R.id.et_telephone);
        btnSaveProfile =(Button)v.findViewById(R.id.btn_save);

        getProfile();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        return v;
    }

    private void getProfile(){


        String token = "111";
        String url = getString(R.string.api_url)+getString(R.string.profile);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,token,new JsonObject(),RequestTask.HTTP_GET_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);

                etFio.setText(response.get("name").getAsString());
                etCompany.setText(response.get("company").getAsString());
                etPosition.setText(response.get("position").getAsString());
                etTelephone.setText(response.get("phone").getAsString());

            }
        }).errorListener(new RequestTask.OnRequestErrorListener() {
            @Override
            public void onRequestError(String error) {

            }
        }).buildRequestTask();
        task.setNeedDialog(true);
        task.execute();
    }

    private void saveProfile(){
        JsonObject object = new JsonObject();
        object.addProperty("name",etFio.getText().toString().trim());
        object.addProperty("company",etCompany.getText().toString().trim());
        object.addProperty("position",etPosition.getText().toString().trim());
        object.addProperty("phone",etTelephone.getText().toString().trim());



        String url = getString(R.string.api_url)+getString(R.string.profile);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,null,object,RequestTask.HTTP_PUT_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);

            }
        }).errorListener(new RequestTask.OnRequestErrorListener() {
            @Override
            public void onRequestError(String error) {

            }
        }).buildRequestTask();
        task.setNeedDialog(true);
        task.execute();
    }
}
