package ru.comtrans.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.comtrans.R;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.RequestTask;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ResponseItem;


/**
 * Created by Artco on 15.04.2016.
 */
public class ProfileFragment extends BaseFragment {

    EditText etName, etLastName, etCompany,etPosition,etTelephone,etEmail;
    Button btnSaveProfile;
    boolean isFromRegistration;

    public static ProfileFragment newInstance(boolean isFromRegistration){
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Const.IS_FROM_REGISTRATION,isFromRegistration);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,container,false);

        etName = (EditText) v.findViewById(R.id.et_first_name);
        etLastName = (EditText)v.findViewById(R.id.et_last_name);
        etCompany = (EditText)v.findViewById(R.id.et_company);
        etPosition = (EditText)v.findViewById(R.id.et_company_position);
        etTelephone = (EditText)v.findViewById(R.id.et_telephone);
        btnSaveProfile =(Button)v.findViewById(R.id.btn_save_profile);
        etEmail = (EditText)v.findViewById(R.id.et_email);

        isFromRegistration = getArguments().getBoolean(Const.IS_FROM_REGISTRATION);

        if(isFromRegistration){
            btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObject object = new JsonObject();
                    object.addProperty("firstName", etName.getText().toString().trim());
                    object.addProperty("lastName", etName.getText().toString().trim());
                    object.addProperty("company",etCompany.getText().toString().trim());
                    object.addProperty("companyPosition",etPosition.getText().toString().trim());
                    object.addProperty("phone",etTelephone.getText().toString().trim());

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container,RegistrationFragment.newInstance(object))
                            .addToBackStack(null)
                            .commit();
                }
            });
            etEmail.setVisibility(View.GONE);
        }else {
            etEmail.setVisibility(View.VISIBLE);
            getProfile();
            btnSaveProfile.setText(R.string.hint_save);
            btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProfile();
                }
            });
        }





        return v;
    }

    private void getProfile(){

        etEmail.setText(Utility.getSavedData(Const.EMAIL));

        String url = getString(R.string.api_url)+getString(R.string.profile);
        Log.d("TAG",Utility.getToken());
      //  JsonObject object = new JsonObject();
      //  object.addProperty("token",Utility.getToken());

        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url, Utility.getToken(),new JsonObject(),RequestTask.HTTP_GET_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);

                etName.setText(response.get("firstName").getAsString());
                etLastName.setText(response.get("lastName").getAsString());
                etCompany.setText(response.get("company").getAsString());
                etPosition.setText(response.get("companyPosition").getAsString());
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
        object.addProperty("firstName", etName.getText().toString().trim());
        object.addProperty("lastName", etName.getText().toString().trim());
        object.addProperty("company",etCompany.getText().toString().trim());
        object.addProperty("companyPosition",etPosition.getText().toString().trim());
        object.addProperty("phone",etTelephone.getText().toString().trim());



        String url = getString(R.string.api_url)+getString(R.string.profile);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
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
