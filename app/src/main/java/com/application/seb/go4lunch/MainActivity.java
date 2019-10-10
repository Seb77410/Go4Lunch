package com.application.seb.go4lunch;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    // References
    Toolbar mToolbar;
    BottomNavigationView bottomNavigationView;

    //----------------------------------------------------------------------------------------------
    // On Create
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureToolbar();
        this.configureBottomView();
    }

    //----------------------------------------------------------------------------------------------
    // Configure Toolbar
    //----------------------------------------------------------------------------------------------

    private void configureToolbar(){
        mToolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
    }

    //----------------------------------------------------------------------------------------------
    // Configure BottomVie
    //----------------------------------------------------------------------------------------------

    private void configureBottomView(){
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.action_map : selectedFragment = new Fragment(); //TODO : create MapFragment
                case R.id.action_list: selectedFragment = new Fragment();   //TODO : create MyListFragment
                case R.id.action_workmates: selectedFragment = new Fragment();  //TODO : create WorkmatesFragment
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_frame_layout, selectedFragment)
                    .commit();
            return true;
        }
    };



}
