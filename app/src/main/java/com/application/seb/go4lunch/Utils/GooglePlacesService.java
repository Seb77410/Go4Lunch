package com.application.seb.go4lunch.Utils;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GooglePlacesService {

    //----------------------------------------------------------------------------------------------
    // Google places nearby request
    //----------------------------------------------------------------------------------------------
    @GET( Constants.JSON_RETURN_FORMAT )
    Observable<GooglePlacesResponse> getnearbyRestaurant(@QueryMap Map<String, String> optionsMap);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
