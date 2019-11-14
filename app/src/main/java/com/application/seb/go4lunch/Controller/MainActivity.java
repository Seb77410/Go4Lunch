package com.application.seb.go4lunch.Controller;

import androidx.annotation.NonNull;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Fragment.ListViewFragment;
import com.application.seb.go4lunch.Fragment.MapFragment;
import com.application.seb.go4lunch.Fragment.WorkmatesFragment;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.MultiDex;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Objects;

import static com.application.seb.go4lunch.Utils.Constants.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener{

    // References
    Context context = this;
    Activity activity = this;
    Toolbar mToolbar;
    BottomNavigationView bottomNavigationView;
    GoogleMap mMap;
    Fragment mapFragment;
    GooglePlacesResponse googlePlacesResponse;
    LatLng userLocation;

    // For multidex error
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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
        // Default view is map fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout, new MapFragment())
                .commit();
    }

    //----------------------------------------------------------------------------------------------
    // Configure Toolbar
    //----------------------------------------------------------------------------------------------

    private void configureToolbar(){
        mToolbar = findViewById(R.id.activity_main_toolbar);
    }

    //----------------------------------------------------------------------------------------------
    // Configure BottomView
    //----------------------------------------------------------------------------------------------

    /**
     * This method glue the BottomView to MainActivity layout and add listener for his button
     */
    private void configureBottomView(){
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * This method configure tabs BottomView content
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            Fragment active = null;

            switch (menuItem.getItemId()){
                case R.id.action_map : selectedFragment = new MapFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main_frame_layout, selectedFragment)
                            .commit();
                    return true;

                case R.id.action_list :
                    selectedFragment = ListViewFragment.newInstance(googlePlacesResponse, userLocation);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main_frame_layout, selectedFragment)
                            .commit();
                    return true;

                case R.id.action_workmates:
                    selectedFragment = new WorkmatesFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main_frame_layout, selectedFragment)
                            .commit();

                    return true;



            }
            return true;

        }
    };

    //----------------------------------------------------------------------------------------------
    // FireBase Login
    //----------------------------------------------------------------------------------------------

    /**
     * Launch and configure FireBase Sign-In Activity
     */
    private void startSignInActivity(){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_go4lunch)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Result after sign in : add user data to FireStore database and notify user that sign-in is OK
     * or not OK with a Toast
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(), "result code = " + resultCode, Toast.LENGTH_LONG).show();
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Log.d("Utilisateur enregistré ","User id = " + Objects.requireNonNull(this.getCurrentUser()).getUid() + ", User name = " + this.getCurrentUser().getDisplayName() + ", User photo url : " + String.valueOf(this.getCurrentUser().getPhotoUrl()));
                // We save User infos into FireStore database
                if (this.getCurrentUser().getPhotoUrl() != null) {
                    FireStoreUserRequest.createUser(Objects.requireNonNull(this.getCurrentUser()).getUid(), this.getCurrentUser().getDisplayName(), Objects.requireNonNull(this.getCurrentUser().getPhotoUrl()).toString());
                }else{
                    FireStoreUserRequest.createUser(Objects.requireNonNull(this.getCurrentUser()).getUid(), this.getCurrentUser().getDisplayName());
                }

            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Erreur : authentification annulée", Toast.LENGTH_LONG).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() ==  ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), "Erreur : pas d'internet ", Toast.LENGTH_LONG).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), "Erreur inconnue", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //----------------------------------------------------------------------------------------------
    // Get nearby places list from MapFragment
    //----------------------------------------------------------------------------------------------

    @Override
    public void onFragmentSetGooglePlacesResponse(GooglePlacesResponse googlePlacesResponse) {
        this.googlePlacesResponse = googlePlacesResponse;
    }

    @Override
    public void onFragmentSetUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }
}
