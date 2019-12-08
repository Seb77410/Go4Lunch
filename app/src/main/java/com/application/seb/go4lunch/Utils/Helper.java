package com.application.seb.go4lunch.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.rpc.Help;
import com.google.zxing.common.StringUtils;

import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.opencensus.internal.StringUtil;

public class Helper {

    public static String setCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        // Convert current date into string value
        return df.format(calendar.getTime());
    }

    public static void setSignInValue(Context context , Boolean isSignIn){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean("AlreadySignIn", isSignIn);
        prefEditor.apply();
        Log.e("Helper", "Life Save SignIn value to " + isSignIn);
    }

    public static boolean getSignInValue(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", Context.MODE_PRIVATE);
        Log.e("Helper", " Life saved SignIn value is " + sharedPreferences.getBoolean("AlreadySignIn", false));
        return sharedPreferences.getBoolean("AlreadySignIn", false);
    }

    public static Float ratingConverter(Double placeRate){
        float percentagePlaceRating = (float) ((placeRate*100)/5);
        float myPlaceRating = (3*percentagePlaceRating)/100;
        return myPlaceRating;
    }

}
