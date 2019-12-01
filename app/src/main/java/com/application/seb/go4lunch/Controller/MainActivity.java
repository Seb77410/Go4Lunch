package com.application.seb.go4lunch.Controller;

import androidx.annotation.NonNull;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.Fragment.ListViewFragment;
import com.application.seb.go4lunch.Fragment.MapFragment;
import com.application.seb.go4lunch.Fragment.WorkmatesFragment;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.MultiDex;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity
        extends AppCompatActivity
        implements MapFragment.OnFragmentInteractionListener,
                   NavigationView.OnNavigationItemSelectedListener{

    // For data
    Toolbar mToolbar;
    BottomNavigationView bottomNavigationView;
    GooglePlacesResponse googlePlacesResponse;
    LatLng userLocation;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView drawerUserPhoto;
    TextView drawerUserName;
    TextView drawerUserEmail;
    String userDate;
    Boolean x;
    Activity activity = this;

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

        // References
        navigationView = findViewById(R.id.activity_main_nav_view);
        mToolbar = findViewById(R.id.activity_main_toolbar);
        ConstraintLayout header = (ConstraintLayout) navigationView.getHeaderView(0);
        drawerUserPhoto = header.findViewById(R.id.nav_header_user_photo);
        drawerUserName = header.findViewById(R.id.nav_header_user_name);
        drawerUserEmail = header.findViewById(R.id.nav_header_user_email);

        //Update user if necessary
        this.updateUserValue();
        Helper.setSignInValue(getApplicationContext(), true);
        // Configure view
        this.configureToolbar();
        this.configureBottomView();
        this.configureNavigationView();
        this.configureDrawerLayout();
        this.configureFragment();
    }

    //----------------------------------------------------------------------------------------------
    // Update FireStore User date value if necessary
    //----------------------------------------------------------------------------------------------

    private void updateUserValue(){
        // Get currentDate
        String currentDate =  Helper.setCurrentDate();
        FireStoreUserRequest
                .getUser(FirebaseAuth.getInstance().getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    userDate = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getCurrentDate();
                    Log.d("SignIn Activity", "User current date is  : " + userDate);
                    if(!currentDate.equals(userDate)){
                        FireStoreUserRequest
                                .getUsersCollection()
                                .get()
                                .addOnCompleteListener(task -> {
                                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        User user = document.toObject(User.class);
                                        FireStoreUserRequest
                                                .getUsersCollection()
                                                .document(Objects.requireNonNull(user).getUid())
                                                .update(Constants.CURRENT_DATE, currentDate,Constants.ALREADY_SUBSCRIBE_RESTAURANT,false);
                                    }
                                });
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // Configure Toolbar
    //----------------------------------------------------------------------------------------------

    /**
     * This method configure activity toolbar
     */
    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This method configure Drawer Layout and add burger button on the left of toolbar
     */
    private void configureDrawerLayout(){
        FireStoreUserRequest
                .getUsersCollection()
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    x = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getAlreadySubscribeRestaurant();
                    Log.d("User boolean", Constants.ALREADY_SUBSCRIBE_RESTAURANT + "= " +x.toString());
                    // Glue drawerLayout to .xml file
                    drawerLayout =  findViewById(R.id.activity_main_drawer_layout);
                    // Glue drawer menu to MainActivity toolbar
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    // Add listener to the menu drawer
                    drawerLayout.addDrawerListener(toggle);
                    // Add animation on drawer menu button when Open/close
                    toggle.syncState();
                    setDrawerUserInfos();
                });
    }

    /**
     * This method start activity fragment and set default view to MapFragment
     */
    private void configureFragment(){
        // Default view is map fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout, new MapFragment())
                .commit();
    }

    //----------------------------------------------------------------------------------------------
    // For Navigation Drawer
    //----------------------------------------------------------------------------------------------

    private void setDrawerUserInfos(){
        // Set user name
        String userName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        drawerUserName.setText(userName);
        // Set user email
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        drawerUserEmail.setText(userEmail);
        // Set user photo
        Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        if (uri != null) {
            String userPhotoUrl = uri.toString();
            Log.d("Drawer menu", "User photo url : " + userPhotoUrl);
            Glide
                    .with(getApplicationContext())
                    .load(userPhotoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(drawerUserPhoto);
        }
        else {
            Glide
                    .with(getApplicationContext())
                    .load(R.drawable.no_image)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(drawerUserPhoto);
        }
    }

    /**
     * This method configure the Navigation view
     */
    private void configureNavigationView(){
        // Glue NavigationView to .xml file
        navigationView.setItemIconTintList(null);
        // Allow user tu click on Menu drawer item button
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.activity_main_drawer_you_lunch :
                if (x) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SUBSCRIBE_PLACE_PREF, MODE_PRIVATE);
                    String place = sharedPreferences.getString(Constants.SUBSCRIBE_PLACE_PREF_VALUE, null);
                    Intent intent = new Intent(this, RestaurantDetails.class);
                    intent.putExtra(Constants.PLACE_DETAILS, place);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No restaurant subscribe", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.activity_main_drawer_logout :
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;

            case R.id.activity_main_drawer_settings :
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
            Fragment selectedFragment;

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
    // For search button
    //----------------------------------------------------------------------------------------------


    /**We create a menu by inflate an .xml
     *
     * @param menu is a the new menu we will glue to our layout
     * @return true to show menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    /**This method will allow to manage the clicks on the menu
     * buttons.
     *
     * @param item is the selected button in the menu
     * @return true to able on click item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Search button ==> Start SearchActivity
        if (item.getItemId() == R.id.search_menu) {

            // Initialize the SDK
            Places.initialize(getApplicationContext(), BuildConfig.PLACE_API_KEY);

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setLocationRestriction(RectangularBounds.newInstance(
                            //set the restrict rectangular bounds for search
                            new com.google.android.gms.maps.model.LatLng(userLocation.longitude + 0.005, userLocation.longitude + 0.005),
                            new com.google.android.gms.maps.model.LatLng(userLocation.latitude - 0.005, userLocation.longitude - 0.005)
                            ))
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .build(this);
            startActivityForResult(intent, Constants.AUTOCOMPLETE_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //TODO : Mettre à jour les vues ou Afficher les details du restaurant ??
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("Autocomplete result", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("Autocomplete result", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Get data from MapFragment
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

