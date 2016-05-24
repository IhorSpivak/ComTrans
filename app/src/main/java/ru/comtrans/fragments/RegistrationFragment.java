package ru.comtrans.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.comtrans.R;
import ru.comtrans.activities.MainActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.RequestTask;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.ResponseItem;


/**
 * Created by Artco on 25.04.2016.
 */
public class RegistrationFragment extends BaseFragment {
    EditText etEmail, etPassword, etConfirmPassword;
    Button btnRegister;

   public static RegistrationFragment newInstance(JsonObject object) {

        Bundle args = new Bundle();
        args.putString(Const.EXTRA_PROFILE_OBJECT,object.toString());
        RegistrationFragment fragment = new RegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration,container,false);

        etEmail = (EditText)v.findViewById(R.id.et_email);
        etPassword = (EditText)v.findViewById(R.id.et_password);
        etConfirmPassword = (EditText)v.findViewById(R.id.et_confirm_password);
        btnRegister = (Button)v.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Utility.isEmailValid(etEmail.getText().toString())){
                    Toast.makeText(getActivity(), R.string.email_correction_toast, Toast.LENGTH_SHORT).show();
                }else {
                    if(etPassword.getText().toString().length()<5){
                        Toast.makeText(getActivity(), R.string.password_length_toast, Toast.LENGTH_SHORT).show();
                    }else {
                        if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                            Toast.makeText(getActivity(), R.string.password_confirmation_toast, Toast.LENGTH_SHORT).show();
                        }else {
                            signUp();
                        }
                    }
                }

            }
        });

        return v;
    }

    private void signUp(){
        JsonObject object = new JsonObject();
        object.addProperty("email",etEmail.getText().toString().trim());
        object.addProperty("password",etPassword.getText().toString());


        String url = getString(R.string.api_url)+getString(R.string.sign_up);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
                if(!response.get("status").isJsonNull()&&response.get("status").getAsString().equals("1")){
                    Utility.saveData(Const.EMAIL,etEmail.getText().toString());
                    Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
                    Utility.saveToken(response.get("token").getAsString());
                    saveProfile();
                }
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
        JsonObject object = new Gson().fromJson(getArguments().getString(Const.EXTRA_PROFILE_OBJECT),JsonObject.class);
        object.addProperty("token",Utility.getToken());

        String url = getString(R.string.api_url)+getString(R.string.profile);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
                Intent i = new Intent(getActivity(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

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
