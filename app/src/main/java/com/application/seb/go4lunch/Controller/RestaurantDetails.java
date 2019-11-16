package com.application.seb.go4lunch.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.Fragment.SubscribersFragment;
import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    Calendar calendar;
    SimpleDateFormat df;
    String currentDate;
    Boolean userSubscribeThisPlace = false;
    ArrayList<String> placeLikeList = new ArrayList<>();
    Restaurant restaurant;

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

        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("dd-MM-yyyy");
        // Convert current date into string value
        currentDate = df.format(calendar.getTime());
        Log.e("RestaurantDetails", "currentDate = " + currentDate);

        //startSubscribersRecyclerView();
        getActivityArgs();
        getPlaceLikedList();
        getSubscribersList();
        onFloatingButtonClick();
        setPlaceImage();
        placeName.setText(place.getName());
        placeAddress.setText(place.getVicinity());
        setPlaceRatingBar();
        setPlaceTimes();
    }

    //----------------------------------------------------------------------------------------------
    // Showing restaurant details on UI
    //----------------------------------------------------------------------------------------------
    /**
     * This method define app comportment when user click on Floating button
     */
    private void onFloatingButtonClick() {

        // Si l'utilisateur s'est deja inscris à ce restau
        if(userSubscribeThisPlace){
            // On modifie le bouton
            subscribeButton.setClickable(false);
            subscribeButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            subscribeButton.setImageResource(R.drawable.green_check);
        }
        // Si l'utilisateur ne s'est pas encore inscrit à ce restau
        else {
            subscribeButton.setClickable(true);
            subscribeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Modify floating button view
                    Log.e("RestaurantDetails", "User just click on Floating button");
                    subscribeButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    subscribeButton.setImageResource(R.drawable.green_check);

                    // Si l'utilisateur ne fais pas partie de ceux qui se sont inscrit à ce restau
                    if (!subscribers.contains(FirebaseAuth.getInstance().getUid())) {
                        // Ajoute l'utilisateur à la liste des subscribers du restau
                        subscribers.add(FirebaseAuth.getInstance().getUid());
                        HashMap<String, ArrayList<String>> data = new HashMap<>();
                        data.put("subscribersList", subscribers);
                        FireStoreRestaurantRequest
                                .getRestaurantSubscribersCollection(place.getPlaceId())
                                .document(currentDate)
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startSubscribersRecyclerView();
                                    }
                                });
                    }
                }
            });
        }
    }

    private void setPlaceLikeButton() {
        placeLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Like Button", "onClick ! ");
                // Si l'utilisateur ne fais pas partie de ceux qui se sont inscrit à ce restau
                if (!placeLikeList.contains(FirebaseAuth.getInstance().getUid())) {
                    placeLikeList.add(FirebaseAuth.getInstance().getUid());
                    HashMap<String, ArrayList<String>> data = new HashMap<>();
                    data.put("userLikeList", placeLikeList);

                    FireStoreRestaurantRequest
                            .getRestaurantsCollection()
                            .document(place.getPlaceId())
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                Log.e("Like Button", "Liste mise à jour");
                                Toast.makeText(getApplicationContext(), "You just like this restaurant", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    private void setCallButton(GooglePlaceOpeningHoursResponse value) {
        placeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.getResult().getFormattedPhoneNumber() != null) {
                    Log.e("RestaurantDetails", "setCallButton : place phone number : " + value.getResult().getFormattedPhoneNumber());

                    String phoneNumber = "tel:" +value.getResult().getFormattedPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(phoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "This restaurant have no phone number", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void setWebSiteButton(GooglePlaceOpeningHoursResponse value) {
        String webSite = value.getResult().getWebsite();
        Log.e("RestaurantDetails", "setWebSiteButton : Website url  : " + webSite);


        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra("url", webSite);
        placeWebSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSite != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "This restaurant have not website", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * This method get place rating and show it on UI
     */
    private void setPlaceRatingBar() {
        float percentagePlaceRating = (float) ((place.getRating() * 100) / 5);
        float myPlaceRating = (3 * percentagePlaceRating) / 100;
        Log.e("RestaurantDetails", "Place rate : " + myPlaceRating);
        placeRatingBar.setRating(myPlaceRating);
    }

    /**
     * This method show place image on UI
     */
    private void setPlaceImage() {
        if (place.getPhotos() != null && place.getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + place.getPhotos().get(0).getPhotoReference() + "&key=" + "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc";
            Log.e("PlaceDetails.activity", "Photo url : " + photoUrl);

            Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(placeImage);
        } else {
            placeImage.setImageResource(R.drawable.no_image);
            Log.e("PlaceDetails.activity", "Photo url : " + "Pas d'url");
        }
    }

    /**
     * This method get arguments from intent activity
     */
    private void getActivityArgs() {
        Intent intent = getIntent();
        String response = intent.getStringExtra("PLACE_DETAILS");
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
        Log.e("RestaurantDetails", "SetPlaceTimes : Place id is " + place.getId());
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
     * @param place
     * @param optionsMap
     */
    private void configurePlaceDetailsRequest(GooglePlacesResponse.Result place, HashMap<String, String> optionsMap) {
        optionsMap.put("place_id", place.getPlaceId());
        optionsMap.put("fields", "name,website,opening_hours,formatted_phone_number");
        optionsMap.put("key", "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc");

    }

    /**
     * Execute a PlaceDetails request according current place
     *
     * @param optionsMap
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
                        } else {
                            whenResponseNotSuccessful(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * Define app comportment if PlaceDetails request is not successful
     *
     * @param value is a GooglePlaceOpeningHoursResponse instance that PlaceDetails request return
     */
    private void whenResponseNotSuccessful(GooglePlaceOpeningHoursResponse value) {
        // Si on a atteint la limite de requete, on affiche qu'on a atteint cette limite
        if (value.getStatus().equals("OVER_QUERY_LIMIT")) {
            placeTimes.setText("OVER_QUERY_LIMIT");
            Log.e("RestaurantDetails", "setPlacesTimes : place request OVER_QUERY_LIMIT");
        }
    }
    //----------------------------------------------------------------------------------------------
    // Get place subscribers list
    //----------------------------------------------------------------------------------------------
    private void getSubscribersList(){

        // Execute firestore request
        FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(place.getPlaceId())
                .collection("subscribers")
                .document(currentDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // On transform la réponse en objet subscribersCollection
                        subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                        // Si le document existe
                        if (subscribersCollection != null ) {
                            // Et que la taille de son tableau de subscribers est superieur à 0
                            if (subscribersCollection.getSubscribersList().size() > 0) {
                                subscribers = subscribersCollection.getSubscribersList();
                                startSubscribersRecyclerView();
                                Toast.makeText(getApplicationContext(), "Les subscribers du 15/10/2019 sont recup", Toast.LENGTH_LONG).show();
                                verifyIfUserAsAlreadySubscribeThisPlace();
                            }else{
                                // Et que sa taille est inférieure à 0
                                Toast.makeText(getApplicationContext(), "La liste des subscribers est vide", Toast.LENGTH_LONG).show();
                            }
                        }
                        // Si le document n'existe pas
                        else {
                            Toast.makeText(getApplicationContext(), "L'objet subscribers est null", Toast.LENGTH_LONG).show();
                        }

                    }

                });
    }

    private void getPlaceLikedList(){
        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .document(place.getPlaceId())
                .get()
                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                         if(task.isSuccessful()){
                             Log.e("Error", "NO ERROR getting documents: ", task.getException());
                             restaurant = Objects.requireNonNull(task.getResult()).toObject(Restaurant.class);
                             if (restaurant != null) {
                                 if (restaurant.getUserLikeList() != null){
                                     if (restaurant.getUserLikeList().size() > 0){
                                         placeLikeList = restaurant.getUserLikeList();
                                     }
                                     else{
                                         Log.e("Liked List", "La liste de likeurs = 0 ");
                                     }
                                 }
                                 else{
                                     Log.e("Liked List", "La liste de likeurs est null ");
                                 }
                                 setPlaceLikeButton();
                             }
                         }
                         else{
                             Log.e("Error", "Error getting documents: ", task.getException());
                         }
                     }
                 });
    }


    //----------------------------------------------------------------------------------------------
    // Showing place subscribers list into RecyclerView
    //----------------------------------------------------------------------------------------------

    private void verifyIfUserAsAlreadySubscribeThisPlace(){
        if(!subscribers.contains(FirebaseAuth.getInstance().getUid())) {
            userSubscribeThisPlace = false;
            Toast.makeText(getApplicationContext(),"L'utilisateur ne fait pas partie des subscribers", Toast.LENGTH_LONG).show();
        }
        else{
            userSubscribeThisPlace = true;
            Toast.makeText(getApplicationContext(),"L'utilisateur fait partie des subscribers", Toast.LENGTH_LONG).show();
        }
        onFloatingButtonClick();
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
}

