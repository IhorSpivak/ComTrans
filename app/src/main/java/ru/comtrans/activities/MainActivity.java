package ru.comtrans.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.comtrans.R;
import ru.comtrans.fragments.MyInfoblocksFragment;
import ru.comtrans.fragments.ProfileFragment;
import ru.comtrans.fragments.SettingsFragment;
import ru.comtrans.helpers.Const;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AddOrEditInfoBlockActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        getSupportActionBar().setTitle(getString(R.string.nav_infoblocks));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, new MyInfoblocksFragment());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fab.hide();
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Intent i;

        switch (id){
            case R.id.nav_infoblocks:
                fab.show();
                getSupportActionBar().setTitle(getString(R.string.nav_infoblocks));
                transaction.replace(R.id.container,new MyInfoblocksFragment());
                transaction.commit();
                break;
            case R.id.nav_profile:
                getSupportActionBar().setTitle(getString(R.string.nav_profile));
                transaction.replace(R.id.container,ProfileFragment.newInstance(false));
                transaction.commit();
                break;
            case R.id.nav_photo:
                checkCameraPermission(false);
                break;
            case R.id.nav_video:
               checkCameraPermission(true);
                break;
            case R.id.nav_settings:
                getSupportActionBar().setTitle(getString(R.string.nav_settings));
                transaction.replace(R.id.container,new SettingsFragment());
                transaction.commit();
                break;
            case R.id.nav_logout:
                i = new Intent(MainActivity.this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @TargetApi(Build.VERSION_CODES.M)
    private void checkCameraPermission(boolean isVideo){
        int hasCameraPermission = 0;
        int hasStoragePermission = 0;
        int hasRecorderPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            hasRecorderPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            List<String> permissions = new ArrayList<>();
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(isVideo){
                if(hasRecorderPermission!= PackageManager.PERMISSION_GRANTED){
                    permissions.add(Manifest.permission.RECORD_AUDIO);
                }
            }
            if (!permissions.isEmpty()) {
                if(isVideo){
                    requestPermissions(permissions.toArray(new String[permissions.size()]),
                            Const.REQUEST_PERMISSION_VIDEO);
                }else {
                    requestPermissions(permissions.toArray(new String[permissions.size()]),
                            Const.REQUEST_PERMISSION_CAMERA);
                }

            } else {
                openCameraFragment(isVideo);
            }
        } else {
            openCameraFragment(isVideo);
        }
    }

    private void openCameraFragment(boolean isVideo){
        Intent i = new Intent(MainActivity.this,CameraActivity.class);
        if(isVideo)
        i.putExtra(Const.CAMERA_MODE,Const.MODE_VIDEO);
        else
        i.putExtra(Const.CAMERA_MODE,Const.MODE_PHOTO);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allow = true;
        switch (requestCode){
            case Const.REQUEST_PERMISSION_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        allow = false;
                    }
                }
                if(allow)
                    openCameraFragment(false);
                else
                    Toast.makeText(MainActivity.this,R.string.not_all_permissions_granted,Toast.LENGTH_SHORT).show();
                break;

            case Const.REQUEST_PERMISSION_VIDEO:
                for (int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        allow = false;
                    }
                }
                if(allow)
                    openCameraFragment(true);
                else
                    Toast.makeText(MainActivity.this,R.string.not_all_permissions_granted,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
