package com.application.seb.go4lunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;


import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.utils.Helper;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
public class HelperTest {

    @Test
    public void calculateDistanceTest(){
        LatLng latLng = new LatLng(1.5, 1.5);
        Double placeLat = 1.5;
        Double placeLong = 1.5;

        Assert.assertNotEquals(1.5,Helper.computeDistance(latLng, placeLat, placeLong));
        Assert.assertEquals(0, Helper.computeDistance(latLng, placeLat, placeLong));
    }

    @Test
    public void setCurrentDateTest(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.YEAR, 2010);

        Assert.assertNotEquals("11-10-2010", Helper.setCurrentDate(calendar));
        Assert.assertEquals("10-11-2010", Helper.setCurrentDate(calendar));
    }

    @Test
    public void ratingConvert(){
        float rate = 5;
        float result = 3;

        Assert.assertNotEquals(5, Helper.ratingConverter(rate));
        Assert.assertEquals(0,result, Helper.ratingConverter(rate));
    }

    @Test
    public void convertDayOfWeekAsIntegerTest(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Assert.assertNotEquals(0, Helper.convertDayOfWeekAsInteger(calendar.get(Calendar.DAY_OF_WEEK)));
        Assert.assertEquals(1, Helper.convertDayOfWeekAsInteger(calendar.get(Calendar.DAY_OF_WEEK)));
    }

    @Test
    public void setPlaceTimeTest(){
        GooglePlaceDetailsResponse placeDetailsResponse = Mockito.mock(GooglePlaceDetailsResponse.class);
        GooglePlaceDetailsResponse.Result result = Mockito.mock(GooglePlaceDetailsResponse.Result.class);

        Mockito.when(placeDetailsResponse.getResult()).thenReturn(result);
        Mockito.when(result.getOpeningHours()).thenReturn(null);

        TextView textView = Mockito.mock(TextView.class);
        Mockito.when(textView.getText()).thenReturn(null);

        Assert.assertNull(Helper.setPlaceTime(placeDetailsResponse, textView));
        Assert.assertNotEquals("Open", Helper.setPlaceTime(placeDetailsResponse, textView));
    }

    @Test
    public void getPrefTest(){

        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Context context = Mockito.mock(Context.class);

        Mockito.when(context.getSharedPreferences("SignIn", Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getBoolean("AlreadySignIn", false)).thenReturn(true);

        Assert.assertTrue(Helper.getSignInValue(context));

    }

}