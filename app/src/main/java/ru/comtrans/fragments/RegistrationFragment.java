package ru.comtrans.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.comtrans.R;
import ru.comtrans.activities.MainActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.User;
import ru.comtrans.singlets.AppController;
import ru.comtrans.views.ConnectionProgressDialog;


/**
 * Created by Artco on 25.04.2016.
 */
public class RegistrationFragment extends BaseFragment {
    EditText etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    User user;
    ConnectionProgressDialog progressDialog;

   public static RegistrationFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable(Const.EXTRA_USER_OBJECT,user);
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

        user = getArguments().getParcelable(Const.EXTRA_USER_OBJECT);

        progressDialog = new ConnectionProgressDialog(getActivity());



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
        progressDialog.show();
        final User user = new User(etEmail.getText().toString().trim(),etPassword.getText().toString());
        Call<User> call = AppController.apiInterface.signUp(user);
        call.enqueue(new Callback<User>() {
                         @Override
                         public void onResponse(Call<User> call, Response<User> response) {
                             if(response.body().getStatus()==1){
                                 Utility.saveData(Const.EMAIL,etEmail.getText().toString());
                                 Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
                                 Utility.saveToken(response.body().getToken());
                                 saveProfile();
                             }else {
                                 progressDialog.hide();
                                 Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                             }

                         }

                         @Override
                         public void onFailure(Call<User> call, Throwable t) {
                             progressDialog.hide();
                             Log.d("TAG","fail",t);
                             Toast.makeText(getActivity(),R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
                         }
                     });

    }

    private void saveProfile(){
        Call<User> call = AppController.apiInterface.saveProfile(Utility.getToken(),user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.body().getStatus()==1){
                    Intent i = new Intent(getActivity(),MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else {
                    Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("TAG","fail",t);
                Toast.makeText(getActivity(),R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
            }
        });
    }

}


//        JsonObject object = new JsonObject();
//        object.addProperty("email",etEmail.getText().toString().trim());
//        object.addProperty("password",etPassword.getText().toString());
//
//
//        String url = getString(R.string.api_url)+getString(R.string.sign_up);
//        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
//                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
//            @Override
//            public void onRequestObtained(ResponseItem responseItem) {
//                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
//                if(!response.get("status").isJsonNull()&&response.get("status").getAsString().equals("1")){
//                    Utility.saveData(Const.EMAIL,etEmail.getText().toString());
//                    Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
//                    Utility.saveToken(response.get("token").getAsString());
//                    saveProfile();
//                }
//            }
//        }).errorListener(new RequestTask.OnRequestErrorListener() {
//            @Override
//            public void onRequestError(String error) {
//
//            }
//        }).buildRequestTask();
//        task.setNeedDialog(true);
//        task.execute();



//        JsonObject object = new Gson().fromJson(getArguments().getString(Const.EXTRA_USER_OBJECT),JsonObject.class);
//        object.addProperty("token",Utility.getToken());
//
//        String url = getString(R.string.api_url)+getString(R.string.profile);
//        RequestTask task = new RequestTask.RequestTaskBuilder(getActivity(),
//                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
//            @Override
//            public void onRequestObtained(ResponseItem responseItem) {
//                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
//                Intent i = new Intent(getActivity(),MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//
//            }
//        }).errorListener(new RequestTask.OnRequestErrorListener() {
//            @Override
//            public void onRequestError(String error) {
//
//            }
//        }).buildRequestTask();
//        task.setNeedDialog(true);
//        task.execute();
