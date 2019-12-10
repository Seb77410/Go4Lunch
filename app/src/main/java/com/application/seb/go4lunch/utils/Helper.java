package com.application.seb.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Helper {

    // --- For current Date ---
    public static String setCurrentDate(Calendar calendar){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        return df.format(calendar.getTime());
    }

    // --- For SharedPreference ---
    public static void setSignInValue(Context context , Boolean isSignIn){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean("AlreadySignIn", isSignIn);
        prefEditor.apply();
        Log.e("Helper", "Life Save SignIn value to " + isSignIn);
    }

    public static boolean getSignInValue(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", MODE_PRIVATE);
        Log.e("Helper", " Life saved SignIn value is " + sharedPreferences.getBoolean("AlreadySignIn", false));
        return sharedPreferences.getBoolean("AlreadySignIn", false);
    }

    public static String getSubscribePlaceValue(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SUBSCRIBE_PLACE_PREF, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SUBSCRIBE_PLACE_PREF_VALUE, null);
    }


    // --- Fore Place rating ---
    public static float ratingConverter(float placeRate){
        float percentagePlaceRating = (placeRate*100)/5;
        //Log.e("rating converter", "place rate : " + placeRate +" result : " + (3*percentagePlaceRating)/100);
        return (3*percentagePlaceRating)/100;
    }

    // --- For place distance ---
    public static int computeDistance(LatLng userlocation, Double placeLat, Double placeLong){
        int radius = 6371;
        double latDistance = Math.toRadians(placeLat - userlocation.latitude);
        double lonDistance = Math.toRadians(placeLong - userlocation.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + (Math.cos(Math.toRadians(userlocation.latitude)) * Math.cos(
                Math.toRadians(placeLat)
        )
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = radius * c * 1000.0;// convert to meters

        double height = 0.0 - 0.0;
        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0);

//      Log.d("Place distance ", (int) Math.round(Math.sqrt(distance)) + " userlocation = " + userlocation + "Place lat : " + placeLat + " place long : " + placeLong);
        return (int) Math.round(Math.sqrt(distance));
    }





    public static String setPlaceTime(GooglePlaceDetailsResponse place, TextView textView) {
        if (place.getResult().getOpeningHours() != null) {
            if (place.getResult().getOpeningHours().getOpenNow()) {
                textView.setText(R.string.place_is_open);
            } else {
                textView.setText(R.string.place_is_close);
            }
            return textView.getText().toString();
        }
        else {
            return null;
        }
    }



}
