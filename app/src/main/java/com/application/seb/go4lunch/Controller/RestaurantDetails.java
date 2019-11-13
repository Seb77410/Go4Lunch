package com.application.seb.go4lunch.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;

public class RestaurantDetails extends AppCompatActivity {

    FloatingActionButton subscribeButton;
    GooglePlacesResponse.Result place;
    ImageView placeImage;
    TextView placeName;
    TextView placeAddress;
    TextView placeTimes;
    RatingBar placeRatingBar;
    int currentDay;
    Calendar currentDayCldr;
    Calendar openingHours;
    Calendar closingHours;

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
        placeTimes = findViewById(R.id.restaurant_details_time);

        getActivityArgs();
        onFloatingButtonClick();
        setPlaceImage();
        placeName.setText(place.getName());
        placeAddress.setText(place.getVicinity());
        setPlaceRatingBar();

        Handler h =new Handler() ;
        h.postDelayed(new Runnable() {
            public void run() {
                setPlaceTimes();
            }

        }, 2000);
    }

    //----------------------------------------------------------------------------------------------
    // Showing restaurant details on UI
    //----------------------------------------------------------------------------------------------

    /**
     * This method get place rating and show it on UI
     */
    private void setPlaceRatingBar(){
        float percentagePlaceRating = (float) ((place.getRating()*100)/5);
        float myPlaceRating = (3*percentagePlaceRating)/100;
        Log.e("RestaurantDetails" ,"Place rate : " + myPlaceRating);
        placeRatingBar.setRating(myPlaceRating);
    }

    /**
     * This method show place image on UI
     */
    private void setPlaceImage(){
        if (place.getPhotos()!=null && place.getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + place.getPhotos().get(0).getPhotoReference() + "&key=" + "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc";
            Log.e("PlaceDetails.activity", "Photo url : " + photoUrl);

            Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(placeImage);
        }
        else {
            placeImage.setImageResource(R.drawable.no_image);
            Log.e("PlaceDetails.activity", "Photo url : " + "Pas d'url");

        }
    }


    /**
     * This method get arguments from intent activity
     */
    private void getActivityArgs(){
        Intent intent = getIntent();
        String response = intent.getStringExtra("PLACE_DETAILS");
        Log.d("RestaurantDetails", "Activity args : " + response);

        Gson gson = new Gson();
        Type type = new TypeToken<GooglePlacesResponse.Result>() {}.getType();

        place = gson.fromJson(response, type );
    }

    /**
     * This method define app comportment when user click on Floating button
     */
    private void onFloatingButtonClick() {

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("RestaurantDetails", "User just click on Floating button");
                subscribeButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                subscribeButton.setImageResource(R.drawable.green_check);
            }
        });
    }

    /**
     * This method modify place time on UI according current time :
     * - if restaurant close within 30 min : app show "Closing soon"
     * - else : app show place closing hour
     */
    private void setPlaceTimes(){
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
     * @param place
     * @param optionsMap
     */
    private void configurePlaceDetailsRequest(GooglePlacesResponse.Result place, HashMap<String, String> optionsMap){
        optionsMap.put("place_id", place.getPlaceId());
        optionsMap.put("fields","opening_hours");
        optionsMap.put("key","AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc");

    }

    /**
     * Execute a PlaceDetails request according current place
     * @param optionsMap
     */
    private void executePlaceDetailsRequest(HashMap<String, String> optionsMap){
        GooglePlacesStream.streamFetchDetailsRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceOpeningHoursResponse>() {
                    @Override
                    public void onNext(GooglePlaceOpeningHoursResponse value) {
                        // Set current day into Integer
                        currentDay = convertCurrentDayIntoInteger(currentDay);

                        // Control response value
                        Gson gson = new Gson();
                        String mValue = gson.toJson(value);
                        Log.e("SetPlacesTimes", "La réponse de la requete des details : " + mValue);

                        // If response is successful
                        whenResponseSuccessful(value);
                        // else
                        whenResponseNotSuccessful(value);
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
     * Define app comportment if PlaceDetails is successful
     * @param value is a GooglePlaceOpeningHoursResponse instance that PlaceDetails request return
     */
    private void whenResponseSuccessful(GooglePlaceOpeningHoursResponse value){

        if (value.getStatus().equals("OK")) {
            // We convert open/close String values into Calendar value
            openingHours = convertStringTimesIntoDate(value.getResult().getOpeningHours().getPeriods().get(currentDay).getOpen().getTime());
            closingHours = convertStringTimesIntoDate(value.getResult().getOpeningHours().getPeriods().get(currentDay).getClose().getTime());

            // Calculate difference between closing hour
            currentDayCldr = Calendar.getInstance();
            int hoursDifference = closingHours.get(Calendar.HOUR_OF_DAY) - currentDayCldr.get(Calendar.HOUR_OF_DAY);
            int minutesDifferences = closingHours.get(Calendar.MINUTE) - currentDayCldr.get(Calendar.MINUTE);
            Log.e("RestaurantDetails", "setPlaceTimes : horaires de fermeture : " + hoursDifference+"h"+ minutesDifferences);

            // And we show restaurant closing hours to UI
            selectPlaceTimesMessage(hoursDifference, minutesDifferences);
        }
    }

    /**
     * Define app comportment if PlaceDetails request is not successful
     * @param value is a GooglePlaceOpeningHoursResponse instance that PlaceDetails request return
     */
    private void whenResponseNotSuccessful(GooglePlaceOpeningHoursResponse value){
        // Si on a atteint la limite de requete, on affiche qu'on a atteint cette limite
        if (value.getStatus().equals("OVER_QUERY_LIMIT")){
            placeTimes.setText("OVER_QUERY_LIMIT");
            Log.e("RestaurantDetails", "setPlacesTimes : place request OVER_QUERY_LIMIT");
        }
    }

    /**
     * This method select a message to show as place time
     * - If restaurant will close in next 30 min, message will be : "Closing soon"
     * - Else message will be : "Open until  + closing hour "
     * @param hoursDifference is difference hour between current hour and closing hour
     * @param minutesDifferences is difference minute between current minute and closing minute
     */
    private void selectPlaceTimesMessage(int hoursDifference, int minutesDifferences){
        // Si le restaurant ferme dans moins de 30 min
        if (hoursDifference == 0 && minutesDifferences >= 30) {
            placeTimes.setTextColor(Color.RED);
            placeTimes.setText("Closing Soon " + hoursDifference + "h" + minutesDifferences);
        }
        // Sinon
        // Si les minutes de fermeture = 0 = on affiche uniquement l'heure
        // de fermeture
        else if(closingHours.get(Calendar.MINUTE) == 0){
            placeTimes.setText("Ouvert jus'qu'à " + closingHours.get(Calendar.HOUR_OF_DAY)+"h");
        }
        // Sinon, on affiche l'heure et les minutes de fermeture
        else {
            placeTimes.setText("Ouvert jus'qu'à " + closingHours.get(Calendar.HOUR_OF_DAY) + "h" + closingHours.get(Calendar.MINUTE));
        }
    }

    /**
     * This method convert the current day into Integer
     * Monday = 0
     * Sunday = 6
     * @param x is the integer value
     * @return x after convert
     */
    private int convertCurrentDayIntoInteger(int x){

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                x = 0;
                break;
            case Calendar.TUESDAY:
                x = 1;
                break;
            case Calendar.WEDNESDAY:
                x = 2;
                break;
            case Calendar.THURSDAY:
                x = 3;
                break;
            case Calendar.FRIDAY:
                x = 4;
                break;
            case Calendar.SATURDAY:
                x = 5;
                break;
            case Calendar.SUNDAY:
                x = 6;
                break;
        }
    return x;
    }


    /**
     * This method convert String date value with format "HHmm" within Calendar value
     * @param placeTime is the String date value
     * @return Calendar value
     */
    private Calendar convertStringTimesIntoDate(String placeTime){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(placeTime));
            Log.e("RestaurantDetails", "convertStringTimesIntoDate : convert date is : " + calendar );
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("RestaurantDetails", "setPlaceTimes : disable to convert place time into date");
            return null;
        }

    }
}
