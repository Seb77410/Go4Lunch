package com.application.seb.go4lunch.Utils;

import android.util.Log;

import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.Model.GooglePlaceOpeningHoursResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GooglePlacesStream {

    //----------------------------------------------------------------------------------------------
    // Observable for google map places search api request
    //----------------------------------------------------------------------------------------------

    public static Observable<GooglePlacesResponse> streamFetchQueryRequest(HashMap<String, String> optionsMap) {
        Log.e("SECTION IN STREAM : ", optionsMap.toString() );
        GooglePlacesService newYorkTimesService = GooglePlacesService.retrofit.create(GooglePlacesService.class);
        return newYorkTimesService.getNearbyRestaurant(optionsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //----------------------------------------------------------------------------------------------
    // Observable for place details api request
    //----------------------------------------------------------------------------------------------

    public static Observable<GooglePlaceOpeningHoursResponse> streamFetchDetailsRequest(HashMap<String, String> optionsMap) {
        Log.e("SECTION IN STREAM : ", optionsMap.toString() );
        GooglePlacesService newYorkTimesService = GooglePlacesService.retrofit2.create(GooglePlacesService.class);
        return newYorkTimesService.getRestaurantDetails(optionsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
