package com.application.seb.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Helper {

    //----------------------------------------------------------------------------------------------
    // For current date
    //----------------------------------------------------------------------------------------------

    /**
     * This method convert a Calendar instance into String value with format "dd-MM-yyyy"
     * @param calendar is a Calendar instance that we want to convert into String value
     * @return String value that contains current date
     */
    public static String setCurrentDate(Calendar calendar) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        return df.format(calendar.getTime());
    }

    //----------------------------------------------------------------------------------------------
    // For Rating bar
    //----------------------------------------------------------------------------------------------

    /**
     * This method a convert a rate with max 5 as a rate with max rate 3
     * @param placeRate is the rate that we want to convert
     * @return a float value that correspond to the convert rate
     */
    public static float ratingConverter(float placeRate) {
        float percentagePlaceRating = (placeRate * 100) / 5;
        //Log.e("rating converter", "place rate : " + placeRate +" result : " + (3*percentagePlaceRating)/100);
        return (3 * percentagePlaceRating) / 100;
    }

    //----------------------------------------------------------------------------------------------
    // For place distance
    //----------------------------------------------------------------------------------------------

    /**
     * This meter calculate meters numbers between two points
     * @param userlocation is a LatLng instance that correspond to the first point that we want to calculate distance
     * @param placeLat is a Double value that correspond to 2nde point latitude that we want to calculate distance
     * @param placeLong is a Double value that correspond to 2nde point longitude that we want to calculate distance
     * @return an Integer value that correspond meters numbers between that 2 points
     */
    public static int computeDistance(LatLng userlocation, Double placeLat, Double placeLong) {
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


    //----------------------------------------------------------------------------------------------
    // For Place time
    //----------------------------------------------------------------------------------------------

    /**
     * Modify a TextView text according if place is open or close
     * @param place is a GooglePlaceDetailsResponse instance that we want to know if it is open
     * @param textView is a TextView instance that we want to show if this place is open
     * @return a String value : - "Open" : if place is open
     *                          - "Close" : if place is close
     *                          - null : if can't get place times
     */
    public static String setPlaceTime(GooglePlaceDetailsResponse place, TextView textView) {
        if (place.getResult().getOpeningHours() != null) {
            if (place.getResult().getOpeningHours().getOpenNow()) {
                textView.setText(R.string.place_is_open);
            } else {
                textView.setText(R.string.place_is_close);
            }
            return textView.getText().toString();
        } else {
            return null;
        }
    }

    /**
     * This method check, according current time, if a place is closing soon.
     * A place is closing soon only if it close under one hour
     * @param place is the place we want to verify
     * @return true only if place is closing soon
     */
    public static Boolean checkIfPlaceIsClosingSoon(GooglePlaceDetailsResponse place) {
        // For data
        int currentDay = Helper.getCurrentDayToInt();
        ArrayList<Integer> index = Helper.getPlaceOpeningIndex(place, currentDay);
        Calendar calendar = Calendar.getInstance();
        Calendar firstOpenDate = Helper.getPlaceOpeningTimes(index.get(0), place, calendar);
        Calendar firstCloseDate = Helper.getPlaceClosingTimes(index.get(0), place);
        Calendar secondOpenDate;
        Calendar secondCloseDate;
        boolean isClosingSoon = false;

        // Verify if place is closing soon
        if (!calendar.before(firstOpenDate) && calendar.before(firstCloseDate)) {
            isClosingSoon = Helper.verifyIfPlaceIsClosingSoon(firstOpenDate, firstCloseDate);
        } else if (index.size() == 2) {
            secondOpenDate = Helper.getPlaceOpeningTimes(index.get(1), place, calendar);
            secondCloseDate = Helper.getPlaceClosingTimes(index.get(1), place);
            isClosingSoon = Helper.verifyIfPlaceIsClosingSoon(secondOpenDate, secondCloseDate);
        }
        return isClosingSoon;
    }

    /**
     *  Convert a day into Integer value
     * @param currentDay is the day that we want convert. Must be : Calendar.DAY_OF_WEEK
     * @return current day of week
     */
    public static int convertDayOfWeekAsInteger(int currentDay) {
        int dayOfWeek = -1;
        switch (currentDay) {
            case Calendar.MONDAY:
                dayOfWeek = 1;
                break;
            case Calendar.TUESDAY:
                dayOfWeek = 2;
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = 3;
                break;
            case Calendar.THURSDAY:
                dayOfWeek = 4;
                break;
            case Calendar.FRIDAY:
                dayOfWeek = 5;
                break;
            case Calendar.SATURDAY:
                dayOfWeek = 6;
                break;
            case Calendar.SUNDAY:
                dayOfWeek = 0;
        }
        //Log.e("convertDayWeekAsInteger", "Current day : " + dayOfWeek);
        return dayOfWeek;
    }

    /**
     *  Get current day of week into Integer value
     * @return day of week
     */
    private static int getCurrentDayToInt() {
        Calendar calendar = Calendar.getInstance();
        return Helper.convertDayOfWeekAsInteger(calendar.get(Calendar.DAY_OF_WEEK));
    }

    /**
     *  This method get place Period index that correspond to current day of week into ArrayLis
     * @param place is the place we want to know opening times
     * @param currentDay is the current day of week
     * @return an ArrayList that contains every index that contains place current day times
     */
    private static ArrayList<Integer> getPlaceOpeningIndex(GooglePlaceDetailsResponse place, int currentDay) {
        ArrayList<Integer> placeOpeningsIndex = new ArrayList<>();
        for (int y = 0; y < place.getResult().getOpeningHours().getPeriods().size(); y++) {
            if (place.getResult().getOpeningHours().getPeriods().get(y).getOpen().getDay() == currentDay) {
                Log.e("getPlaceServiceNumber ", "place opening hour " + place.getResult().getOpeningHours().getPeriods().get(y).getOpen().getTime());
                placeOpeningsIndex.add(y);
            }
        }
        return placeOpeningsIndex;
    }

    /**
     * This method set place opening time into Calendar instance according current day
     * @param index is the GooglePlaceDetailsResponse.getResult().getOpeningHours().getPeriods() list index.
     *              That correspond to place time according current day of week
     * @param place is a GooglePlaceDetailsResponse instance
     * @return place current opening time into Calendar instance
     */
    private static Calendar getPlaceOpeningTimes(int index, GooglePlaceDetailsResponse place, Calendar currentDate) {
        String placeOpeningTime = place.getResult().getOpeningHours().getPeriods().get(index).getOpen().getTime();
        currentDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(placeOpeningTime.substring(0, 2)));
        currentDate.set(Calendar.MINUTE, Integer.parseInt(placeOpeningTime.substring(2, 4)));
        return currentDate;
    }

    /**
     * This method set place closing time into Calendar instance according current day
     * @param index is the GooglePlaceDetailsResponse.getResult().getOpeningHours().getPeriods() list index.
     *              That correspond to place time according current day of week
     * @param place is a GooglePlaceDetailsResponse instance
     * @return place current closing time into Calendar instance
     */
    private static Calendar getPlaceClosingTimes(int index, GooglePlaceDetailsResponse place) {

        // Get place time into String value
        String placeOpeningTime = place.getResult().getOpeningHours().getPeriods().get(index).getClose().getTime();

        // Convert String value into Calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(placeOpeningTime.substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(placeOpeningTime.substring(2, 4)));
        if (place.getResult().getOpeningHours().getPeriods().get(index).getClose().getDay() == Helper.getCurrentDayToInt() + 1) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        return calendar;
    }

    /**
     * This method calculate difference between two Calendar instance
     * @param openDate correspond to a place opening date
     * @param closeDate correspond to a place closing date
     * @return true if difference between the 2 dates is under one hour
     */
    private static Boolean verifyIfPlaceIsClosingSoon(Calendar openDate, Calendar closeDate) {
        boolean isClosingSoon = false;
        Calendar calendar = Calendar.getInstance();

        if (!calendar.before(openDate) && calendar.before(closeDate)) {
            long seconds = (closeDate.getTimeInMillis() - calendar.getTimeInMillis()) / 1000;
            int hours = (int) (seconds / 3600);

            if (hours == 0) {
                isClosingSoon = true;
            }
        }
        return isClosingSoon;
    }


    //----------------------------------------------------------------------------------------------
    // For SharedPreferences
    //----------------------------------------------------------------------------------------------

    /**
     * For SignInActivity. Save value when user sign in
     * @param context is app context
     * @param isSignIn is a boolean value that return true when user is sign in
     */
    public static void setSignInValue(Context context, Boolean isSignIn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean("AlreadySignIn", isSignIn);
        prefEditor.apply();
        Log.d("Helper", "Life Save SignIn value to " + isSignIn);
    }

    /**
     * For SignInActivity. Get value to know if user sign in
     * @param context is app context
     * @return is a boolean value that return true when user is sign in
     */
    public static boolean getSignInValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignIn", MODE_PRIVATE);
        //Log.e("Helper", " Life saved SignIn value is " + sharedPreferences.getBoolean("AlreadySignIn", false));
        return sharedPreferences.getBoolean("AlreadySignIn", false);
    }

    /**
     * For WorkmatesFragment. Get user subscribe place name
     * @param context is app context
     * @return a String value that contains user subscribe place name
     */
    public static String getSubscribePlaceValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SUBSCRIBE_PLACE_PREF, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SUBSCRIBE_PLACE_PREF_VALUE, null);
    }

}











