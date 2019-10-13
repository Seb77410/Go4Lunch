package com.application.seb.go4lunch.Controler;

import androidx.annotation.NonNull;

import com.application.seb.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Objects;


import static com.application.seb.go4lunch.Utils.Constants.RC_SIGN_IN;

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

        this.startSignInActivity();
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
    // Configure BottomView
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

    //----------------------------------------------------------------------------------------------
    // FireBase Login
    //----------------------------------------------------------------------------------------------

    // Launch Sign-In Activity
    private void startSignInActivity(){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_go4lunch)
                        .build(),
                RC_SIGN_IN);
    }

    // Result after sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(), "result code = " + resultCode, Toast.LENGTH_LONG).show();
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Toast.makeText(getApplicationContext(), "Connexion successful", Toast.LENGTH_LONG).show();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Erreur : authentification annulée", Toast.LENGTH_LONG).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() ==  ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), "Erreur : pas d'interet ", Toast.LENGTH_LONG).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), "Erreur inconnue", Toast.LENGTH_LONG).show();
                }
            }
        }
    }




}
