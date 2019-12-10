package com.application.seb.go4lunch;

import android.content.Context;

import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.utils.Constants;
import com.application.seb.go4lunch.utils.GooglePlacesStream;
import com.application.seb.go4lunch.utils.Helper;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class HelperTest {

    @Test
    public void calculateDistanceTest(){
        LatLng latLng = new LatLng(49.0363282, 2.9334548);
        Double placeLat = 48.96521329999999;
        Double placeLong = 2.8831791;
        Assert.assertEquals(8717, Helper.computeDistance(latLng, placeLat, placeLong));
    }

    @Test
    public void setCurrentDateTest(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.YEAR, 2010);

        Assert.assertEquals("10-11-2010", Helper.setCurrentDate(calendar));
    }

    @Test
    public void ratingConvert(){
        float rate = 5;
        float result = 3;
        Assert.assertEquals(0,result, Helper.ratingConverter(rate));
    }

    @Test
    public void requestTest(){
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, "dc417714ed26274fe18ea86a72a153519b450868"); // pizza maestro ID
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

        Observable<GooglePlaceDetailsResponse> detailsResponseObservable = GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap);
        TestObserver<GooglePlaceDetailsResponse> detailsResponseTestObserver = new TestObserver<>();

        detailsResponseObservable.subscribeWith(detailsResponseTestObserver)
                .assertNoErrors()
                .awaitTerminalEvent();

    }
}