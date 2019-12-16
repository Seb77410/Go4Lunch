package com.application.seb.go4lunch.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.seb.go4lunch.api.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.api.FireStoreUserRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.fragment.SubscribersFragment;
import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.model.Restaurant;
import com.application.seb.go4lunch.model.SubscribersCollection;
import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.utils.Constants;
import com.application.seb.go4lunch.utils.GooglePlacesStream;
import com.application.seb.go4lunch.utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

public class RestaurantDetails extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    FloatingActionButton subscribeButton;
    ImageView placeImage;
    TextView placeName;
    TextView placeAddress;
    RatingBar placeRatingBar;
    ImageButton placeCallButton;
    ImageButton placeLikeButton;
    ImageButton placeWebSiteButton;
    ArrayList<String> subscribers = new ArrayList<>();
    SubscribersCollection subscribersCollection;
    String currentDate = Helper.setCurrentDate(Calendar.getInstance());
    ArrayList<String> placeLikeList = new ArrayList<>();
    String placeId;

    //----------------------------------------------------------------------------------------------
    // OnCreate
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        // References
        subscribeButton = findViewById(R.id.restaurant_details_subscribe_button);
        placeImage = findViewById(R.id.restaurant_details_image);
        placeName = findViewById(R.id.restaurant_details_name);
        placeAddress = findViewById(R.id.restaurant_details_address);
        placeRatingBar = findViewById(R.id.restaurant_details_ratingBar);
        placeCallButton = findViewById(R.id.restaurant_details_call_imageView);
        placeLikeButton = findViewById(R.id.restaurant_details_like_image);
        placeWebSiteButton = findViewById(R.id.restaurant_details_website_image);

        askForCallPermission();
        // Show restaurant details
        getActivityArgs();
        getPlaceDetails();

    }

    //----------------------------------------------------------------------------------------------
    // Showing restaurant details on UI
    //----------------------------------------------------------------------------------------------

    /**
     * This method get activity argument into String value
     */
    private void getActivityArgs() {
        Intent intent = getIntent();
        placeId = intent.getStringExtra(Constants.PLACE_DETAILS);
        Log.d("RestaurantDetails", "Activity args : " + placeId);
    }

    /**
     * This method execute a google place details request as parameters the place id which
     * is into activity arguments. When result is receive, the view is update according result
     */
    private void getPlaceDetails(){
        // Places details request options
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, placeId);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);
        //Start place details request
        GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceDetailsResponse>() {
                    @Override
                    public void onNext(GooglePlaceDetailsResponse value) {
                        Log.d("Map Fragment", "Le restau : " + value.getResult().getName());
                        // Update view
                        placeName.setText(value.getResult().getName());
                        placeAddress.setText(value.getResult().getVicinity());
                        getPlaceLikedList();
                        getRestaurantSubscribersList();
                        setFloatingButton();
                        setPlaceImage(value);
                        setPlaceRatingBar(value);
                        setCallButton(value);
                        setWebSiteButton(value);
                    }

                    @Override
                    public void onError(Throwable e) {e.printStackTrace();}
                    @Override
                    public void onComplete() {}
                });
    }

    //--------------------------------------------
    // For Place Like Button
    //--------------------------------------------

    /**
     * This method get current place liked list with Restaurant collection into FireStore
     */
    private void getPlaceLikedList(){
        // Get current restaurant
        FireStoreRestaurantRequest
                .getRestaurant(placeId)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){ // Request successful
                        Restaurant restaurant = Objects.requireNonNull(task.getResult()).toObject(Restaurant.class);
                        if (restaurant != null) {
                            if (restaurant.getUserLikeList() != null){
                                if (restaurant.getUserLikeList().size() > 0){
                                    placeLikeList = restaurant.getUserLikeList();
                                } else{ Log.d("RestaurantDetails", "Liked list size = 0 "); }
                            } else{ Log.d("RestaurantDetails", "Liked list =  null ");}
                            setPlaceLikeButton();
                        }
                    } else{ // Request not successful
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * This method update current place liked list into FireStore
     */
    private void setPlaceLikeButton() {
        placeLikeButton.setOnClickListener(v -> {
            Log.d("Like Button", "onClick ! ");
            // If current user have not yet liked this place
            if (!placeLikeList.contains(FirebaseAuth.getInstance().getUid())) {
                // For data
                placeLikeList.add(FirebaseAuth.getInstance().getUid());
                HashMap<String, ArrayList<String>> data = new HashMap<>();
                data.put(Constants.USER_LIKE_LIST, placeLikeList);
                // Update place liked list
                FireStoreRestaurantRequest.updatePlaceLikedList(placeId, data)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Like Button", "Place liked list update");
                            Toast.makeText(getApplicationContext(), getString(R.string.place_just_like), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    //--------------------------------------------
    // For Place SubscribersList
    //--------------------------------------------

    /**
     * This method get current place subscribers list with Restaurant collection into FireStore
     */
    private void getRestaurantSubscribersList(){
        // Execute fireStore request
        FireStoreRestaurantRequest.getSubscriberList(placeId, currentDate)
                .addOnSuccessListener(documentSnapshot -> {
                    // Transform response to SubscribersCollection instance
                    subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);
                    // If document exist
                    if (subscribersCollection != null ) {
                        // And his size > 0
                        if (subscribersCollection.getSubscribersList().size() > 0) {
                            subscribers = subscribersCollection.getSubscribersList();
                            Log.d("RestaurantDetails", "Receive place subscribers list : " + subscribers);
                            startSubscribersRecyclerView();
                        }
                        // And his size < 0
                        else{ Log.d("RestaurantDetails", "Subscribers list size = 0"); }
                    }
                    // If document no exist
                    else { Log.d("RestaurantDetails", "SubscribersCollection instance = null"); }
                });
    }


    /**
     * This method add a recycler view, with subscribers list as parameters, on current view
     */
    private void startSubscribersRecyclerView(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.restaurant_details_frameLayout, new SubscribersFragment(subscribers))
                .commit();
    }

    //--------------------------------------------
    // For Floating Button
    //--------------------------------------------

    /**
     * This method configure current FloatingButton view
     */
    private void setFloatingButton(){
        // Get current FireStore user document
        FireStoreUserRequest.getUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(documentSnapshot -> {
                    // Transform response into User instance
                    User user = documentSnapshot.toObject(User.class);
                    // If user is not null
                    if (user != null) {onFloatingButtonClick(user);} // Configure floating button
                });
    }

    /**
     * This method configure FloatingButton click :
     *  - if user already subscribed a restaurant : Floating button is not clickable
     *  - else : Floating button is clickable. And when user click : update values on FireStore
     * @param user is current FireStore user document
     */
    private void onFloatingButtonClick(User user) {

        // If user already subscribed any restaurant
        if(user.getAlreadySubscribeRestaurant()){
            // Modify FloatingButton view
            subscribeButton.setClickable(false);
            subscribeButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            subscribeButton.setImageResource(R.drawable.green_check);
        }
        // If user didn't subscribed any restaurant
        else {
            subscribeButton.setClickable(true);
            // When user click on FloatingButton
            subscribeButton.setOnClickListener(v -> {
                // Modify floating button view
                Log.d("RestaurantDetails", "User just click on Floating button");
                subscribeButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                subscribeButton.setImageResource(R.drawable.green_check);

                // If user didn't subscribed this restaurant
                if (!subscribers.contains(FirebaseAuth.getInstance().getUid())) {
                    // Add user into restaurant subscribers list
                    subscribers.add(FirebaseAuth.getInstance().getUid());
                    HashMap<String, ArrayList<String>> data = new HashMap<>();
                    data.put(Constants.SUBSCRIBERS_LIST, subscribers);
                    FireStoreRestaurantRequest.updateSubscribersList(placeId, currentDate, data)
                            .addOnSuccessListener(aVoid -> startSubscribersRecyclerView());
                    // Update user value
                    FireStoreUserRequest.updateUserSubscribeBoolean(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                    // And save restaurant
                    saveSubscribePlace();
                }
            });
        }
    }

    /**
     * This method save current place id into sharedPreference
     */
    private void saveSubscribePlace(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SUBSCRIBE_PLACE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        Intent intent = getIntent();
        String response = intent.getStringExtra(Constants.PLACE_DETAILS);
        prefEditor.putString(Constants.SUBSCRIBE_PLACE_PREF_VALUE, response);
        prefEditor.apply();

    }

    //--------------------------------------------
    // For Place Image
    //--------------------------------------------

    /**
     * This method show place image on UI
     * @param value is google place details request response as GooglePlaceDetailsResponse instance
     */

    private void setPlaceImage(GooglePlaceDetailsResponse value) {
        if (value.getResult().getPhotos() != null && value.getResult().getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = Constants.BASE_PHOTO_API_REQUEST + value.getResult().getPhotos().get(0).getPhotoReference() + Constants.PHOTO_API_KEY_PARAMETERS + BuildConfig.PLACE_API_KEY;
            Log.d("PlaceDetails.activity", "Photo url : " + photoUrl);

            Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(placeImage);
        } else {
            placeImage.setImageResource(R.drawable.no_image);
            Log.d("PlaceDetails.activity", "Photo url : " + "Pas d'url");
        }
    }

    //--------------------------------------------
    // For Place Rate
    //--------------------------------------------

    /**
     * This method configure place rating bar view
     * @param value is google place details request response as GooglePlaceDetailsResponse instance
     */
    private void setPlaceRatingBar(GooglePlaceDetailsResponse value) {
        float myPlaceRating = Helper.ratingConverter(value.getResult().getRating());
        Log.d("RestaurantDetails", "Place rate : " + myPlaceRating);
        placeRatingBar.setRating(myPlaceRating);

    }

    //--------------------------------------------
    // For Place call
    //--------------------------------------------

    /**
     * This method configure call button click :
     *  - if place have a phone number : start ACTION_CALL
     *  - else : notify user, with toast, that place have no phone number
     * @param value is google place details request response as GooglePlaceDetailsResponse instance
     */
    public void setCallButton(GooglePlaceDetailsResponse value) {
        placeCallButton.setOnClickListener(v -> {
            if (value.getResult().getFormattedPhoneNumber() != null) {
                Log.d("RestaurantDetails", "setCallButton : place phone number : " + value.getResult().getFormattedPhoneNumber());
                // If user didn't authorize ACTION_CALL : make toast to asl him to authorize ACTION_CALL
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.action_call_not_accepted), Toast.LENGTH_LONG).show();
                }
                // If user Authorize ACTION_CALL : start call action
                else {
                    String phoneNumber = Constants.TEL + value.getResult().getFormattedPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(phoneNumber));
                    startActivity(intent);
                }
                // Notify user with toast that place have no phone number
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_phone_number), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method aks CALL_PHONE permission to user
     */
    private void askForCallPermission(){
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Ask for location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CODE);
            }
        }
    }

    //--------------------------------------------
    // For Place website
    //--------------------------------------------

    /**
     * This method configure website click :
     *      *  - if place have a website : start WebViewActivity
     *      *  - else : notify user, with toast, that place have no website
     * @param value is google place details request response as GooglePlaceDetailsResponse instance
     */
    private void setWebSiteButton(GooglePlaceDetailsResponse value) {
        // Get website url
        String webSite = value.getResult().getWebsite();
        Log.d("RestaurantDetails", "setWebSiteButton : Website url  : " + webSite);
        // Start WebViewActivity
        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra(Constants.URL, webSite);
        placeWebSiteButton.setOnClickListener(v -> {
            if (webSite != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_website), Toast.LENGTH_LONG).show();
            }
        });
    }


}

