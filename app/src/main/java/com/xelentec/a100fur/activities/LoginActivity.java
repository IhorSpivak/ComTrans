package com.xelentec.a100fur.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xelentec.a100fur.R;
import com.xelentec.a100fur.helpers.Const;
import com.xelentec.a100fur.helpers.RequestTask;
import com.xelentec.a100fur.helpers.Utility;
import com.xelentec.a100fur.items.ResponseItem;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail,etPassword;
    Button btnLogin,btnRegister,btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnRegister = (Button)findViewById(R.id.btn_register);
        btnForgotPassword = (Button)findViewById(R.id.btn_forgot_password);

        etEmail.setText(Utility.getSavedData(Const.EMAIL));
        etPassword.setText(Utility.getSavedData(Const.PASSWORD));

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);




    }


    private void signIn(){
        JsonObject object = new JsonObject();
        object.addProperty("email",etEmail.getText().toString().trim());
        object.addProperty("password",etPassword.getText().toString());


        String url = getString(R.string.api_url)+getString(R.string.sign_in);

        RequestTask task = new RequestTask.RequestTaskBuilder(LoginActivity.this,
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
                if(!response.get("status").isJsonNull()&&response.get("status").getAsString().equals("1")){
                    Utility.saveData(Const.EMAIL,etEmail.getText().toString());
                    Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
                    Utility.saveToken(response.get("token").getAsString());
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
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

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.btn_login:
                if(!Utility.isEmailValid(etEmail.getText().toString())){
                    Toast.makeText(LoginActivity.this, R.string.email_correction_toast, Toast.LENGTH_SHORT).show();
                }else {
                    if(etPassword.getText().toString().length()<5){
                        Toast.makeText(LoginActivity.this, R.string.password_length_toast, Toast.LENGTH_SHORT).show();
                    }else {
                        signIn();
                    }
                }
                break;
            case R.id.btn_register:
                i = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(i);
                break;
            case R.id.btn_forgot_password:
                i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
                break;

        }
    }
}
