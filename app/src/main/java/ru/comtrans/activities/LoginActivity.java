package ru.comtrans.activities;

import android.os.Bundle;

import ru.comtrans.R;
import ru.comtrans.fragments.LoginFragment;


public class LoginActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new LoginFragment()).commit();

    }


}
