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
import ru.comtrans.helpers.Const;
import ru.comtrans.helpers.Utility;
import ru.comtrans.items.User;
import ru.comtrans.singlets.AppController;
import ru.comtrans.views.ConnectionProgressDialog;


/**
 * Created by Artco on 15.04.2016.
 */
public class ProfileFragment extends BaseFragment {

    EditText etName, etLastName, etCompany,etPosition,etTelephone,etEmail;
    Button btnSaveProfile;
    boolean isFromRegistration;
    ConnectionProgressDialog progressDialog;

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


             User user = createUser();


                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container,RegistrationFragment.newInstance(user))
                            .addToBackStack(null)
                            .commit();
                }
            });
            etEmail.setVisibility(View.GONE);
        }else {
            progressDialog = new ConnectionProgressDialog(getActivity());
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
        Log.d("TAG",Utility.getToken());

        Call<User> call = AppController.apiInterface.getUser(Utility.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.body().getStatus()==1){
                    etName.setText(response.body().getFirstName());
                    etLastName.setText(response.body().getLastName());
                    etCompany.setText(response.body().getCompany());
                    etPosition.setText(response.body().getCompanyPosition());
                    etTelephone.setText(response.body().getPhone());
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

    private void saveProfile(){
        progressDialog.show();
        User user = createUser();
        Call<User> call = AppController.apiInterface.saveProfile(Utility.getToken(),user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.body().getStatus()==1){
                    Toast.makeText(getActivity(),R.string.profile_saved,Toast.LENGTH_SHORT).show();
                }else {
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



    private User createUser(){
        return new User(etName.getText().toString().trim(),
                etName.getText().toString().trim(),
                etCompany.getText().toString().trim(),
                etPosition.getText().toString().trim(),
                etTelephone.getText().toString().trim());
    }
}
