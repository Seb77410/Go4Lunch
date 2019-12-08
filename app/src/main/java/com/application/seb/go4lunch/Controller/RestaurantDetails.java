package com.application.seb.go4lunch.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.Fragment.SubscribersFragment;
import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.application.seb.go4lunch.Utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

public class RestaurantDetails extends AppCompatActivity {

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
    String currentDate = Helper.setCurrentDate();
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

        // Show restaurant details
        getActivityArgs();
        getPlaceDetails();

    }

    //----------------------------------------------------------------------------------------------
    // Showing restaurant details on UI
    //----------------------------------------------------------------------------------------------

    private void getActivityArgs() {
        Intent intent = getIntent();
        placeId = intent.getStringExtra(Constants.PLACE_DETAILS);
        Log.d("RestaurantDetails", "Activity args : " + placeId);
    }

    private void getPlaceDetails(){

        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, placeId);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

        GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceDetailsResponse>() {
                    @Override
                    public void onNext(GooglePlaceDetailsResponse value) {
                        Log.d("Map Fragment", "Le restau : " + value.getResult().getName());
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
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
    //--------------------------------------------
    // For Place Like Button
    //--------------------------------------------

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


    private void startSubscribersRecyclerView(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.restaurant_details_frameLayout, new SubscribersFragment(subscribers))
                .commit();
    }

    //--------------------------------------------
    // For Floating Button
    //--------------------------------------------

    private void setFloatingButton(){

        FireStoreUserRequest.getUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(documentSnapshot -> {
                    // Transform response into User instance
                    User user = documentSnapshot.toObject(User.class);
                    // If user is not null
                    if (user != null) {
                        // Configure floating button
                        onFloatingButtonClick(user);
                    }
                });

    }

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
    // For Place Image
    //--------------------------------------------

    private void setPlaceRatingBar(GooglePlaceDetailsResponse value) {

        float myPlaceRating = Helper.ratingConverter(value.getResult().getRating());
        Log.d("RestaurantDetails", "Place rate : " + myPlaceRating);
        placeRatingBar.setRating(myPlaceRating);

    }

    //--------------------------------------------
    // For Place call
    //--------------------------------------------

    public void setCallButton(GooglePlaceDetailsResponse value) {
        placeCallButton.setOnClickListener(v -> {
            if (value.getResult().getFormattedPhoneNumber() != null) {
                Log.d("RestaurantDetails", "setCallButton : place phone number : " + value.getResult().getFormattedPhoneNumber());

                String phoneNumber = Constants.TEL + value.getResult().getFormattedPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(phoneNumber));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_phone_number), Toast.LENGTH_LONG).show();
            }
        });
    }

    //--------------------------------------------
    // For Place website
    //--------------------------------------------

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

