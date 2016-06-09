package ru.comtrans.fragments;

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
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.User;
import ru.comtrans.singlets.AppController;
import ru.comtrans.views.ConnectionProgressDialog;


/**
 * Created by Artco on 20.04.2016.
 */
public class ForgotPasswordInputFragment extends BaseFragment {

    EditText etEmail;
    Button btnForgotPassword;
    ConnectionProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password_input,container,false);

        etEmail = (EditText)v.findViewById(R.id.et_email);
        btnForgotPassword = (Button)v.findViewById(R.id.btn_forgot_password);

        progressDialog = new ConnectionProgressDialog(getActivity());

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
        progressDialog.show();
        final User user = new User(etEmail.getText().toString().trim());
        Call<User> call = AppController.apiInterface.forgotPassword(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.body().getStatus()==1){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,new ForgotPasswordDoneFragment())
                            .commit();
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
