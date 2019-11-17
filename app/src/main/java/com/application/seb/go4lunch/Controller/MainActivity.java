package com.application.seb.go4lunch.Controller;

import androidx.annotation.NonNull;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Fragment.ListViewFragment;
import com.application.seb.go4lunch.Fragment.MapFragment;
import com.application.seb.go4lunch.Fragment.WorkmatesFragment;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.MultiDex;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Objects;

import static com.application.seb.go4lunch.Utils.Constants.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener{

    // References
    Toolbar mToolbar;
    BottomNavigationView bottomNavigationView;
    GooglePlacesResponse googlePlacesResponse;
    LatLng userLocation;
    DrawerLayout drawerLayout;
    ImageView drawerUserPhoto;
    TextView drawerUserName;
    TextView drawerUserEmail;
    NavigationView navigationView;
    RequestManager glide;

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

        this.configureToolbar();
        this.startSignInActivity();
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
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

    }

    //----------------------------------------------------------------------------------------------
    //For Navigation Drawer
    //----------------------------------------------------------------------------------------------

    private void setDrawerUserInfos(){
        // References
        drawerUserPhoto = navigationView.findViewById(R.id.nav_header_user_photo);
        drawerUserName = navigationView.findViewById(R.id.nav_header_user_name);
        drawerUserEmail = navigationView.findViewById(R.id.nav_header_user_email);
        // Set user name
        String userName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        drawerUserName.setText(userName);
        // Set user email
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        drawerUserEmail.setText(userEmail);
        // Set user photo
        String userPhotoUrl = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl()).toString();
        Log.e("La photo : ", "de l'utilisateur esr" + userPhotoUrl);

        Glide
                .with(getApplicationContext())
                .load(userPhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into((ImageView) navigationView.findViewById(R.id.nav_header_user_photo));
    }

    /**
     * This method configure Drawer Layout and add burger button on the left of toolbar
     */
    private void configureDrawerLayout(){
        // Glue drawerLayout to .xml file
        this.drawerLayout =  findViewById(R.id.activity_main_drawer_layout);
        // Glue drawer menu to MainActivity toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // Add listener to the menu drawer
        drawerLayout.addDrawerListener(toggle);
        // Add animation on drawer menu button when Open/close
        toggle.syncState();
    }

    /**
     * This method configure the Navigation view
     */
    private void configureNavigationView(){
        // Glue NavigationView to .xml file
        navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setItemIconTintList(null);
        // Allow user tu click on Menu drawer item button
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {



        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
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
                Log.d("Utilisateur identifié ","User id = " + Objects.requireNonNull(this.getCurrentUser()).getUid() + ", User name = " + this.getCurrentUser().getDisplayName() + ", User photo url : " + String.valueOf(this.getCurrentUser().getPhotoUrl()));
                verifyIfUserExist();
                this.configureDrawerLayout();
                this.configureNavigationView();
                setDrawerUserInfos();
               // setDrawerUserInfos();
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

    private void verifyIfUserExist(){
        FireStoreUserRequest
                .getUsersCollection()
                .document(Objects.requireNonNull(getCurrentUser()).getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            // We save User infos into FireStore database
                            if (getCurrentUser().getPhotoUrl() != null) {
                                FireStoreUserRequest.createUser(Objects.requireNonNull(getCurrentUser()).getUid(), getCurrentUser().getDisplayName(), Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString());
                            }else{
                                FireStoreUserRequest.createUser(Objects.requireNonNull(getCurrentUser()).getUid(), getCurrentUser().getDisplayName());
                            }
                        }
                    }
                })
                ;
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

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

