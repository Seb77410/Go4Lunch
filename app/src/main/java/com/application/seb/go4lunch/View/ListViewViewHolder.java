package com.application.seb.go4lunch.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeTimes;
    private TextView placeDistance;
    private TextView placeSubscribersNembers;
    private ImageView placeSubsciberImage;
    private RatingBar placeRatingBar;


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        placeImage = itemView.findViewById(R.id.place_image);
        placeName = itemView.findViewById(R.id.place_name);
        placeAddress = itemView.findViewById(R.id.place_address);
        placeTimes = itemView.findViewById(R.id.place_times);
        placeDistance = itemView.findViewById(R.id.place_distance);
        placeSubscribersNembers = itemView.findViewById(R.id.place_subscribers_numbers);
        placeSubsciberImage = itemView.findViewById(R.id.place_subscribers_image);
        placeRatingBar = itemView.findViewById(R.id.place_ratingBar);

    }

    public void updateWithPlacesList(GooglePlacesResponse.Result place, LatLng userLocation, RequestManager glide) {
        placeName.setText(place.getName());

        setPlaceAddress(place);
        setPlaceDistance(place, userLocation);
        setPlaceRatingBar(place);
        setPlaceImage(place, glide);
        //setPlaceTimes(place);
        isOpenOrClose(place);

/*

*/

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

    private void isOpenOrClose(GooglePlacesResponse.Result place){
        if (place.getOpeningHours() != null) {
            if (place.getOpeningHours().getOpenNow() != null || place.getOpeningHours().getOpenNow()) {
                placeTimes.setText("Is open");
                placeTimes.setTextColor(Color.GREEN);

            } else {
                placeTimes.setText("Is closed");
                placeTimes.setTextColor(Color.RED);
            }
        } else {
            placeTimes.setText("Horaires inconnus");
            placeTimes.setTypeface(null, Typeface.ITALIC);
        }

    }

}



