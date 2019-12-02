package com.application.seb.go4lunch.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;


public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeTimes;
    private TextView placeDistance;
    private TextView placeSubscriberNumbers;
    private ImageView placeSubscriberImage;
    private RatingBar placeRatingBar;
    private HashMap<String, String> optionsMap = new HashMap<>();
    private String sOpeningMinute;
    private String sOpeningHour;
    private String sClosingMinute;
    private String sClosingHour;


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        placeImage = itemView.findViewById(R.id.place_image);
        placeName = itemView.findViewById(R.id.place_name);
        placeAddress = itemView.findViewById(R.id.place_address);
        placeTimes = itemView.findViewById(R.id.place_times);
        placeDistance = itemView.findViewById(R.id.place_distance);
        placeSubscriberNumbers = itemView.findViewById(R.id.place_subscribers_numbers);
        placeSubscriberImage = itemView.findViewById(R.id.place_subscribers_image);
        placeRatingBar = itemView.findViewById(R.id.place_ratingBar);

    }

    public void updateWithPlacesList(GooglePlacesResponse.Result place, LatLng userLocation, RequestManager glide) {
        placeName.setText(place.getName());

        Log.e("ListViewViewHolder", "coordonnées du resatu : " + place.getPlaceId());

        setPlaceAddress(place);
        setPlaceDistance(place, userLocation);
        setPlaceRatingBar(place);
        setPlaceImage(place, glide);
        setPlaceTime(place);
        setPlaceSubscribersNumberWithRestaurant(place);

    }

    private void setPlaceAddress(GooglePlacesResponse.Result place){
        String mPlaceAddress = place.getVicinity();
        mPlaceAddress =  mPlaceAddress.replace(", ", ",\n");
        placeAddress.setText(mPlaceAddress);

    }

    private void setPlaceDistance(GooglePlacesResponse.Result place, LatLng userLocation){
        float[] results = new float[1];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng(), results);

        Log.e("Place distance ", String.valueOf(Math.round(results[0])));
        placeDistance.setText(Math.round(results[0]) + "m");
    }

    private void setPlaceRatingBar(GooglePlacesResponse.Result place){
        float pourcentagePlaceRating = (float) ((place.getRating()*100)/5);
        float myPlaceRating = (3*pourcentagePlaceRating)/100;
        Log.e("Place rate ", String.valueOf(myPlaceRating));
        placeRatingBar.setRating(myPlaceRating);
    }

    private void setPlaceImage(GooglePlacesResponse.Result place, RequestManager glide){

        if (place.getPhotos()!=null && place.getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + place.getPhotos().get(0).getPhotoReference() + "&key=" + "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc";

            glide.load(photoUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(placeImage);
        }
        else {
            placeImage.setImageResource(R.drawable.no_image);
        }
    }

    private void configurePlaceDetailsRequest(GooglePlacesResponse.Result place) {
        optionsMap.put("place_id", place.getPlaceId());
        optionsMap.put("fields", "name,opening_hours");
        optionsMap.put("key", "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc");

    }

    /**
     * Define app comportment if PlaceDetails is successful
     *
     * @param place is the current GooglePlacesResponse instance
     */
    private void setPlaceTime( GooglePlacesResponse.Result place) {

        // If place times are registered
        if (place.getOpeningHours() != null) {
            // Start place detail request api
            configurePlaceDetailsRequest(place);
            GooglePlacesStream.streamFetchDetailsRequest(optionsMap)
                    .safeSubscribe(new DisposableObserver<GooglePlaceOpeningHoursResponse>() {
                        @Override
                        public void onNext(GooglePlaceOpeningHoursResponse value) {
                            // Control response value
                            Gson gson = new Gson();
                            String mValue = gson.toJson(value);
                            Log.e("SetPlacesTimes", "La réponse de la requete des details : " + mValue);

                            //configurePlaceTimesValues(value);
                            selectMessage(value, sOpeningHour, sOpeningMinute, sClosingHour, sClosingMinute);
                        }

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onComplete() {}
                    });

        // If place Times are not registered
        }else {placeTimes.setVisibility(View.INVISIBLE);}
    }

    private void selectMessage(GooglePlaceOpeningHoursResponse value, String sOpeningHour, String sOpeningMinute, String sClosingHour, String sClosingMinute ){
        placeTimes.setTypeface(null, Typeface.ITALIC);
        Calendar currentTime = Calendar.getInstance();
        int currentDay = currentTime.get(Calendar.DAY_OF_WEEK);
        // If place is Open
        if (value.getResult().getOpeningHours().getOpenNow()) {
            selectTimesMessageWhenPlaceIsOpen(value, currentDay, currentTime);
        }
        // If place is Close
        else {
            selectTimesMessageWhenPlaceIsClose(value, currentDay);
            }
    }

    private void selectTimesMessageWhenPlaceIsOpen(GooglePlaceOpeningHoursResponse value, int currentDay, Calendar currentTime){
        // Get closing hours into String values
        sClosingMinute = value.getResult().getOpeningHours().getPeriods().get(currentDay).getClose().getTime().substring(2, 4);
        sClosingHour = value.getResult().getOpeningHours().getPeriods().get(currentDay).getClose().getTime().substring(0, 2);
        Log.e("ListViewHolder", "String closing hour : " + sClosingHour+"h"+sClosingMinute);
        placeTimes.setText("Open until " + sClosingHour + "h" + sClosingMinute);

        // Si il ferme dans moins d'une heure
        // We get current date
        Log.e("ListViewHolder", "configurePlaceTimesValues : currentDate : " + currentTime.getTime());
        if(sClosingHour.equals("00")){
            sClosingHour = "24"; }
        int hourDiference = Integer.parseInt(sClosingHour) - currentTime.get(Calendar.HOUR_OF_DAY);
        Log.e("setPlaceTimes ", "La diff des heures = " + hourDiference);
        if (hourDiference < 1) {
            placeTimes.setTextColor(Color.RED);
            placeTimes.setText("Closing soon");
        }
    }

    private void selectTimesMessageWhenPlaceIsClose(GooglePlaceOpeningHoursResponse value, int currentDay){
        // Get opening hours into String values
        if(currentDay == value.getResult().getOpeningHours().getPeriods().size()){
            currentDay = 0; }
        sOpeningMinute = value.getResult().getOpeningHours().getPeriods().get(currentDay).getOpen().getTime().substring(2, 4);
        sOpeningHour = value.getResult().getOpeningHours().getPeriods().get(currentDay).getOpen().getTime().substring(0, 2);
        Log.e("ListViewHolder", "String opening hour : " + sOpeningHour+"h"+sOpeningMinute);
        placeTimes.setText("Close until " + sOpeningHour + "h" + sOpeningMinute);
    }

    private void setPlaceSubscribersNumberWithRestaurant(GooglePlacesResponse.Result place){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        // Convert current date into string value
        String currentDate = df.format(calendar.getTime());
        Log.e("RestaurantDetails", "currentDate = " + currentDate);

        // Execute firestore request
        Task<DocumentSnapshot> docRef = FirebaseFirestore.getInstance()
                .collection("restaurants")
                .document(place.getPlaceId())
                .collection("subscribers")
                .document(currentDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // On transform la réponse en objet subscribersCollection
                        SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                        // Si le document existe
                        if (subscribersCollection != null) {
                            if (subscribersCollection.getSubscribersList().size()>0) {
                                placeSubscriberImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
                                placeSubscriberNumbers.setText("("+subscribersCollection.getSubscribersList().size()+")");
                                Log.e("ListViewHolder", "Le restaurant a " + subscribersCollection.getSubscribersList().size()+ " subscribers");
                            }

                        }else{
                            Log.e("ListViewHolder", "Le restaurant n'a pas subscribers");
                        }

                    }
                });
    }
}



