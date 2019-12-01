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
import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.application.seb.go4lunch.Utils.Helper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

public class RestaurantDetails extends AppCompatActivity {

    FloatingActionButton subscribeButton;
    GooglePlacesResponse.Result place;
    ImageView placeImage;
    TextView placeName;
    TextView placeAddress;
    TextView placeTimes;
    RatingBar placeRatingBar;
    ImageButton placeCallButton;
    ImageButton placeLikeButton;
    ImageButton placeWebSiteButton;
    ArrayList<String> subscribers = new ArrayList<>();
    SubscribersCollection subscribersCollection;
    String currentDate = Helper.setCurrentDate();
    Boolean userAlreadySubscribeOnePlace = false;
    ArrayList<String> placeLikeList = new ArrayList<>();



    //----------------------------------------------------------------------------------------------
    // OnCreate
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        subscribeButton = findViewById(R.id.restaurant_details_subscribe_button);
        placeImage = findViewById(R.id.restaurant_details_image);
        placeName = findViewById(R.id.restaurant_details_name);
        placeAddress = findViewById(R.id.restaurant_details_address);
        placeRatingBar = findViewById(R.id.restaurant_details_ratingBar);
        placeCallButton = findViewById(R.id.restaurant_details_call_imageView);
        placeLikeButton = findViewById(R.id.restaurant_details_like_image);
        placeWebSiteButton = findViewById(R.id.restaurant_details_website_image);


        getActivityArgs();
        getPlaceLikedList();
        getSubscribersListByRestaurant();
        getRestaurantList();
        setPlaceImage();
        placeName.setText(place.getName());
        placeAddress.setText(place.getVicinity());
        setPlaceRatingBar();
        setPlaceTimes();
    }

    //----------------------------------------------------------------------------------------------
    // Showing restaurant details on UI
    //----------------------------------------------------------------------------------------------

    private void setPlaceLikeButton() {
        placeLikeButton.setOnClickListener(v -> {
            Log.d("Like Button", "onClick ! ");
            // If current user have not yet liked this place
            if (!placeLikeList.contains(FirebaseAuth.getInstance().getUid())) {
                placeLikeList.add(FirebaseAuth.getInstance().getUid());
                HashMap<String, ArrayList<String>> data = new HashMap<>();
                data.put(Constants.USER_LIKE_LIST, placeLikeList);

                FireStoreRestaurantRequest
                        .getRestaurantsCollection()
                        .document(place.getPlaceId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                        Log.d("Like Button", "Place liked list update");
                        Toast.makeText(getApplicationContext(), getString(R.string.place_just_like), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    public void setCallButton(GooglePlaceOpeningHoursResponse value) {
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

    private void setWebSiteButton(GooglePlaceOpeningHoursResponse value) {
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

    /**
     * This method get place rating and show it on UI
     */
    private void setPlaceRatingBar() {
        float percentagePlaceRating = (float) ((place.getRating() * 100) / 5);
        float myPlaceRating = (3 * percentagePlaceRating) / 100;
        Log.d("RestaurantDetails", "Place rate : " + myPlaceRating);
        placeRatingBar.setRating(myPlaceRating);
    }

    /**
     * This method show place image on UI
     */
    private void setPlaceImage() {
        if (place.getPhotos() != null && place.getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = Constants.HEAD_LINK + place.getPhotos().get(0).getPhotoReference() + Constants.KEY_PARAMETERS + BuildConfig.PLACE_API_KEY;
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

    /**
     * This method get arguments from intent activity
     */
    private void getActivityArgs() {
        Intent intent = getIntent();
        String response = intent.getStringExtra(Constants.PLACE_DETAILS);
        Log.d("RestaurantDetails", "Activity args : " + response);

        Gson gson = new Gson();
        Type type = new TypeToken<GooglePlacesResponse.Result>() {
        }.getType();

        place = gson.fromJson(response, type);
    }



    /**
     * This method modify place time on UI according current time :
     * - if restaurant close within 30 min : app show "Closing soon"
     * - else : app show place closing hour
     */
    private void setPlaceTimes() {
        Log.d("RestaurantDetails", "SetPlaceTimes : Place id is " + place.getId());
        HashMap<String, String> optionsMap = new HashMap<>();
        configurePlaceDetailsRequest(place, optionsMap);
        executePlaceDetailsRequest(optionsMap);
    }

    //----------------------------------------------------------------------------------------------
    // Utils for setPlaceTimes()
    //----------------------------------------------------------------------------------------------

    /**
     * Configure a HashMap as query for PlaceDetails request
     *
     * @param place is the place that we want to get details
     * @param optionsMap is HashMap<String> that contains api request parameters
     */
    private void configurePlaceDetailsRequest(GooglePlacesResponse.Result place, HashMap<String, String> optionsMap) {
        optionsMap.put(Constants.PLACE_ID, place.getPlaceId());
        optionsMap.put(Constants.FIELDS, Constants.FIELDS_VALUES);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

    }

    /**
     * Execute a PlaceDetails request according current place
     *
     * @param optionsMap is HashMap<String> that contains api request parameters
     */
    private void executePlaceDetailsRequest(HashMap<String, String> optionsMap) {
        GooglePlacesStream.streamFetchDetailsRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceOpeningHoursResponse>() {
                    @Override
                    public void onNext(GooglePlaceOpeningHoursResponse value) {

                        if (value.getStatus().equals("OK")) {
                            // Control response value
                            Gson gson = new Gson();
                            String mValue = gson.toJson(value);
                            Log.e("SetPlacesTimes", "La réponse de la requete des details : " + mValue);

                            setCallButton(value);
                            setWebSiteButton(value);
                        }
                        else {whenResponseNotSuccessful(value);}
                    }
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
    }

    /**
     * Define app comportment if PlaceDetails request is not successful
     *
     * @param value is a GooglePlaceOpeningHoursResponse instance that PlaceDetails request return
     */
    private void whenResponseNotSuccessful(GooglePlaceOpeningHoursResponse value) {
        // If google API request is over limit
        if (value.getStatus().equals(Constants.OVER_QUERY_LIMIT)) {
            placeTimes.setText(Constants.OVER_QUERY_LIMIT);
            Log.e("RestaurantDetails", "setPlacesTimes : place request OVER_QUERY_LIMIT");
        }
    }
    //----------------------------------------------------------------------------------------------
    // Get place subscribers list
    //----------------------------------------------------------------------------------------------
    private void getSubscribersListByRestaurant(){
        // Execute fireStore request
        FirebaseFirestore.getInstance()
                .collection(Constants.RESTAURANT_COLLECTION_NAME)
                .document(place.getPlaceId())
                .collection(Constants.SUBSCRIBERS_COLLECTION_NAME)
                .document(currentDate)
                .get()
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
                        }else{
                         // And his size < 0
                            Log.d("RestaurantDetails", "Subscribers list size = 0");
                        }
                    }
                    // If document no exist
                    else {
                        Log.d("RestaurantDetails", "SubscribersCollection instance = null");
                    }
                });
    }



    private void getPlaceLikedList(){
        // Get current restaurant
        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .document(place.getPlaceId())
                .get()
                 .addOnCompleteListener(task -> {
                     if(task.isSuccessful()){
                         Restaurant restaurant = Objects.requireNonNull(task.getResult()).toObject(Restaurant.class);
                         if (restaurant != null) {
                             if (restaurant.getUserLikeList() != null){
                                 if (restaurant.getUserLikeList().size() > 0){
                                     placeLikeList = restaurant.getUserLikeList();
                                 }
                                 else{
                                     Log.d("RestaurantDetails", "Liked list size = 0 ");
                                 }
                             }
                             else{
                                 Log.d("RestaurantDetails", "Liked list =  null ");
                             }
                             setPlaceLikeButton();
                         }
                     }
                     else{
                         Log.e("Error", "Error getting documents: ", task.getException());
                     }
                 });
    }

    //----------------------------------------------------------------------------------------------
    // Start SubscribersFragment
    //----------------------------------------------------------------------------------------------
    private void startSubscribersRecyclerView(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.restaurant_details_frameLayout, new SubscribersFragment(subscribers))
                .commit();
    }

    //----------------------------------------------------------------------------------------------
    // Configure FloatingButton
    //----------------------------------------------------------------------------------------------

    private void getRestaurantList(){
        // Get all FireStore Restaurants
        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("RestaurantDetails", "Get restaurant list");
                    // Get current SubscribersCollection for every restaurant
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())){
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                        getRestaurantSubscribersList(Objects.requireNonNull(restaurant));
                    }
                });
    }

    private void getRestaurantSubscribersList(Restaurant restaurant){
        // Get current SubscribersCollection from restaurant
        FireStoreRestaurantRequest
                .getRestaurantSubscribersCollection(restaurant.getId())
                .document(currentDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Verify if current user is in restaurant SubscribersCollection
                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);
                    verifyIfUserIsInSubscribersCollection(subscribersCollection, restaurant);
                    onFloatingButtonClick();
                });
    }

    private void saveSubscribePlace(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SUBSCRIBE_PLACE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        Intent intent = getIntent();
        String response = intent.getStringExtra(Constants.PLACE_DETAILS);
        prefEditor.putString(Constants.SUBSCRIBE_PLACE_PREF_VALUE, response);
        prefEditor.apply();

    }

    private void verifyIfUserIsInSubscribersCollection(SubscribersCollection subscribersCollection, Restaurant restaurant){
        if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(FirebaseAuth.getInstance().getUid())) {
            userAlreadySubscribeOnePlace = true;
            Log.d("RestaurantDetails", "Current user subscribed this restaurant : " + restaurant.getName());
        }
    }

    /**
     * This method define app comportment when user click on Floating button
     */
    private void onFloatingButtonClick() {

        // If user already subscribed any restaurant
        if(userAlreadySubscribeOnePlace){
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
                    FireStoreRestaurantRequest
                            .getRestaurantSubscribersCollection(place.getPlaceId())
                            .document(currentDate)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> startSubscribersRecyclerView());

                    // Update user value
                    FireStoreUserRequest
                            .getUsersCollection()
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .update(Constants.ALREADY_SUBSCRIBE_RESTAURANT, true);

                    // And save restaurant
                    saveSubscribePlace();
                }
            });
        }
    }

}

