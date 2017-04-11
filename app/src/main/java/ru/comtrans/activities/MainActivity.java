package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.comtrans.R;
import ru.comtrans.fragments.MyInfoblocksFragment;
import ru.comtrans.fragments.ProfileFragment;
import ru.comtrans.fragments.SettingsFragment;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton)findViewById(R.id.fab);


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
        transaction.replace(R.id.container, new MyInfoblocksFragment());
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
                fab.hide();
                getSupportActionBar().setTitle(getString(R.string.nav_profile));
                transaction.replace(R.id.container,ProfileFragment.newInstance(false));
                transaction.commit();
                break;

            case R.id.nav_settings:
                fab.hide();
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



}
