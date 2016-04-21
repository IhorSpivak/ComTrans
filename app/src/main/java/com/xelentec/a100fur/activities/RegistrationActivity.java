package com.xelentec.a100fur.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class RegistrationActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etConfirmPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        etConfirmPassword = (EditText)findViewById(R.id.et_confirm_password);
        btnRegister = (Button)findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Utility.isEmailValid(etEmail.getText().toString())){
                    Toast.makeText(RegistrationActivity.this, R.string.email_correction_toast, Toast.LENGTH_SHORT).show();
                }else {
                    if(etPassword.getText().toString().length()<5){
                        Toast.makeText(RegistrationActivity.this, R.string.password_length_toast, Toast.LENGTH_SHORT).show();
                    }else {
                        if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                            Toast.makeText(RegistrationActivity.this, R.string.password_confirmation_toast, Toast.LENGTH_SHORT).show();
                        }else {
                            signUp();
                        }
                    }
                }

            }
        });


    }


    private void signUp(){
        JsonObject object = new JsonObject();
        object.addProperty("email",etEmail.getText().toString().trim());
        object.addProperty("password",etPassword.getText().toString());


        String url = getString(R.string.api_url)+getString(R.string.sign_up);
        RequestTask task = new RequestTask.RequestTaskBuilder(RegistrationActivity.this,
                url,null,object,RequestTask.HTTP_POST_REQUEST).obtainListener(new RequestTask.OnRequestObtainedListener() {
            @Override
            public void onRequestObtained(ResponseItem responseItem) {
                JsonObject response = new Gson().fromJson(responseItem.getResponse(),JsonObject.class);
                    if(!response.get("status").isJsonNull()&&response.get("status").getAsString().equals("1")){
                        Utility.saveData(Const.EMAIL,etEmail.getText().toString());
                        Utility.saveData(Const.PASSWORD,etPassword.getText().toString());
                        Intent i = new Intent(RegistrationActivity.this,MainActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
