package com.application.seb.go4lunch.View;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.application.seb.go4lunch.Utils.Helper;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;

@SuppressLint("SetTextI18n")
class ListViewViewHolder extends RecyclerView.ViewHolder {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeTimes;
    private TextView placeDistance;
    private TextView placeSubscriberNumbers;
    private ImageView placeSubscriberImage;
    private RatingBar placeRatingBar;

    ListViewViewHolder(@NonNull View itemView) {
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


    void updateWithPlacesList(String placeId, LatLng userLocation, RequestManager glide) {
        updateByExecutePlaceDetailsRequest(placeId, userLocation, glide);
        Log.d("ListViewViewHolder", "Restaurant Id : " + placeId);
    }

    private void updateByExecutePlaceDetailsRequest(String placeId,LatLng userLocation, RequestManager glide) {
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, placeId);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

        GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceDetailsResponse>() {
                    @Override
                    public void onNext(GooglePlaceDetailsResponse value) {
                        placeName.setText(value.getResult().getName());
                        setPlaceAddress(value);
                        setPlaceDistance(value, userLocation);
                        setPlaceRatingBar(value);
                        setPlaceImage(value, glide);
                        setPlaceTime(value);
                        setPlaceSubscribersNumberWithRestaurant(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {}
                });

    }

    private void setPlaceAddress(GooglePlaceDetailsResponse place){
        String mPlaceAddress = place.getResult().getVicinity();
        mPlaceAddress =  mPlaceAddress.replace(", ", ",\n");
        placeAddress.setText(mPlaceAddress);
    }

    private void setPlaceDistance(GooglePlaceDetailsResponse place, LatLng userLocation){
        float[] results = new float[1];
        Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                place.getResult().getGeometry().getLocation().getLat(),
                place.getResult().getGeometry().getLocation().getLng(),
                results);
        Log.d("Place distance ", String.valueOf(Math.round(results[0])));
        placeDistance.setText(Math.round(results[0]) + "m");
    }

    private void setPlaceRatingBar(GooglePlaceDetailsResponse place){
        float myPlaceRating = Helper.ratingConverter(place.getResult().getRating());
        Log.d("Place rate ", String.valueOf(myPlaceRating));
        placeRatingBar.setRating(myPlaceRating);
    }

    private void setPlaceImage(GooglePlaceDetailsResponse place, RequestManager glide){

        if (place.getResult().getPhotos()!=null && place.getResult().getPhotos().get(0).getPhotoReference() != null) {
            String photoUrl = Constants.BASE_PHOTO_API_REQUEST
                    + place.getResult().getPhotos().get(0).getPhotoReference() + Constants.PHOTO_API_KEY_PARAMETERS + BuildConfig.PLACE_API_KEY;

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

    private void setPlaceSubscribersNumberWithRestaurant(GooglePlaceDetailsResponse place){

        // Convert current date into string value
        String currentDate = Helper.setCurrentDate();
        Log.d("ListViewHolder", "currentDate = " + currentDate);

        // Execute FireStore request
        FireStoreRestaurantRequest.getSubscriberList(place.getResult().getPlaceId(), currentDate)
                .addOnSuccessListener(documentSnapshot -> {
                    // Transform response into subscribersCollection instance
                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                    // If document exist
                    if (subscribersCollection != null) {
                        if (subscribersCollection.getSubscribersList().size()>0) {
                            placeSubscriberImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
                            placeSubscriberNumbers.setText("("+subscribersCollection.getSubscribersList().size()+")");
                            Log.d("ListViewHolder", "Restaurant have " + subscribersCollection.getSubscribersList().size()+ " subscribers");
                        }

                    }else{
                        Log.d("ListViewHolder", "Le restaurant n'a pas subscribers");
                    }

                });
    }


    private void setPlaceTime(GooglePlaceDetailsResponse place){
        placeTimes.setTypeface(null, Typeface.ITALIC);
        if (place.getResult().getOpeningHours() != null) {

            if (place.getResult().getOpeningHours().getOpenNow()) {
                placeTimes.setText(R.string.place_is_open);
            } else {
                placeTimes.setText(R.string.place_is_close);
            }
        }
    }


}



