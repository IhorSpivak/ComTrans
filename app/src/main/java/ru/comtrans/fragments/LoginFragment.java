package ru.comtrans.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import ru.comtrans.activities.ForgotPasswordActivity;
import ru.comtrans.activities.MainActivity;
import ru.comtrans.activities.RegistrationActivity;
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.User;
import ru.comtrans.singlets.AppController;
import ru.comtrans.views.ConnectionProgressDialog;

/**
 * Created by Artco on 08.06.2016.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    EditText etEmail,etPassword;
    Button btnLogin,btnRegister,btnForgotPassword;
    ConnectionProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login,container,false);

        etEmail = (EditText)v.findViewById(R.id.et_email);
        etPassword = (EditText)v.findViewById(R.id.et_password);
        btnLogin = (Button)v.findViewById(R.id.btn_login);
        btnRegister = (Button)v.findViewById(R.id.btn_register);
        btnForgotPassword = (Button)v.findViewById(R.id.btn_forgot_password);

        etEmail.setText(Utility.getSavedData(Const.EMAIL));
        etPassword.setText(Utility.getSavedData(Const.PASSWORD));

        progressDialog = new ConnectionProgressDialog(getActivity());

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.btn_login:
                if(!Utility.isEmailValid(etEmail.getText().toString())){
                    Toast.makeText(getActivity(), R.string.email_correction_toast, Toast.LENGTH_SHORT).show();
                }else {
                    if(etPassword.getText().toString().length()<5){
                        Toast.makeText(getActivity(), R.string.password_length_toast, Toast.LENGTH_SHORT).show();
                    }else {
                        signIn();
                    }
                }
                break;
            case R.id.btn_register:
                i = new Intent(getActivity(),RegistrationActivity.class);
                startActivity(i);
                break;
            case R.id.btn_forgot_password:
                i = new Intent(getActivity(),ForgotPasswordActivity.class);
                startActivity(i);
                break;

        }
    }

    private void signIn(){
        progressDialog.show();
        final User user = new User(etEmail.getText().toString().trim(),etPassword.getText().toString());
        Call<User> call = AppController.apiInterface.signIn(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.body().getStatus()==1){
                    Utility.saveData(Const.EMAIL,etEmail.getText().toString());
                    Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
                    Utility.saveToken(response.body().getToken());
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
