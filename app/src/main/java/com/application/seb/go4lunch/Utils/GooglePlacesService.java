package com.application.seb.go4lunch.Utils;

import com.application.seb.go4lunch.Model.AutocompleteResponse;
import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GooglePlacesService {

    //----------------------------------------------------------------------------------------------
    // Google places nearby request
    //----------------------------------------------------------------------------------------------

    @GET( Constants.JSON_RETURN_FORMAT )
    Observable<GooglePlacesResponse> getNearbyRestaurant(@QueryMap Map<String, String> optionsMap);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    //----------------------------------------------------------------------------------------------
    // Google places details request
    //----------------------------------------------------------------------------------------------

    @GET( Constants.JSON_RETURN_FORMAT )
    Observable<GooglePlaceDetailsResponse> getRestaurantDetailsTotal(@QueryMap Map<String, String> optionsMap);

    Retrofit retrofit2 = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/details/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    //----------------------------------------------------------------------------------------------
    // Google places query autocomplete
    //----------------------------------------------------------------------------------------------

    @GET(Constants.JSON_RETURN_FORMAT)
    Observable<AutocompleteResponse> getQueryAutocompleteDetails(@QueryMap Map<String, String> optionsMap);

    Retrofit retrofit4 = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/autocomplete/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}
