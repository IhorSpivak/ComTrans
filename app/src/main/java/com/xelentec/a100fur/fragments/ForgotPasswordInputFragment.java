package com.xelentec.a100fur.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xelentec.a100fur.R;
import com.xelentec.a100fur.helpers.RequestTask;
import com.xelentec.a100fur.helpers.Utility;
import com.xelentec.a100fur.items.ResponseItem;

/**
 * Created by Artco on 20.04.2016.
 */
public class ForgotPasswordInputFragment extends BaseFragment {

    EditText etEmail;
    Button btnForgotPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password_input,container,false);

        etEmail = (EditText)v.findViewById(R.id.et_email);
        btnForgotPassword = (Button)v.findViewById(R.id.btn_forgot_password);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utility.isEmailValid(etEmail.getText().toString())){
                    forgotPassword();
                }else {
                    Toast.makeText(getActivity(), R.string.email_correction_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    private void forgotPassword(){
        JsonObject object = new JsonObject();
        object.addProperty("email",etEmail.getText().toString().trim());



        String url = getString(R.string.api_url)+getString(R.string.forgot_password);
        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                Log.d("TAG","status");
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
                if(response.get("status").getAsInt()==1){
                    Log.d("TAG","status");
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,new ForgotPasswordDoneFragment())
                            .commit();
                }
            }
        }).errorListener(new RequestTask.OnRequestErrorListener() {
            @Override
            public void onRequestError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }).buildRequestTask();
        task.setNeedDialog(true);
        task.execute();
    }
}
