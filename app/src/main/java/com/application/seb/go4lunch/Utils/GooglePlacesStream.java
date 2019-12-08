package com.application.seb.go4lunch.Utils;

import android.util.Log;

import com.application.seb.go4lunch.Model.AutocompleteResponse;
import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
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
        Log.e("SECTION IN STREAM : ", "Nearby places " + optionsMap.toString() );
        GooglePlacesService newYorkTimesService = GooglePlacesService.retrofit.create(GooglePlacesService.class);
        return newYorkTimesService.getNearbyRestaurant(optionsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //----------------------------------------------------------------------------------------------
    // Observable for place details api request
    //----------------------------------------------------------------------------------------------

    public static Observable<GooglePlaceDetailsResponse> streamFetchDetailsRequestTotal(HashMap<String, String> optionsMap) {
        Log.e("SECTION IN STREAM : ", "Details Request Total " + optionsMap.toString() );
        GooglePlacesService newYorkTimesService = GooglePlacesService.retrofit2.create(GooglePlacesService.class);
        return newYorkTimesService.getRestaurantDetailsTotal(optionsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //----------------------------------------------------------------------------------------------
    // Observable for place autocomplete api request
    //----------------------------------------------------------------------------------------------

    public static Observable<AutocompleteResponse> streamFetchAutocomplete(HashMap<String, String> optionsMap) {
        Log.e("SECTION IN STREAM : ", "Autocomplete " + optionsMap.toString() );
        GooglePlacesService newYorkTimesService = GooglePlacesService.retrofit4.create(GooglePlacesService.class);
        return newYorkTimesService.getQueryAutocompleteDetails(optionsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

}
