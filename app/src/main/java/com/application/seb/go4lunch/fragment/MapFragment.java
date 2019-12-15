package com.application.seb.go4lunch.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.api.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.controller.RestaurantDetails;
import com.application.seb.go4lunch.model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.model.GooglePlacesResponse;
import com.application.seb.go4lunch.model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.utils.Constants;
import com.application.seb.go4lunch.utils.GooglePlacesStream;
import com.application.seb.go4lunch.utils.Helper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapFragment extends Fragment implements
        OnMapReadyCallback
        ,GoogleMap.OnMarkerClickListener
        {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private GoogleMap mMap;
    private Disposable disposable;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<String> autocompletePlacesId;

    //----------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //----------------------------------------------------------------------------------------------
    // Data listeners for MainActivity
    //----------------------------------------------------------------------------------------------
     public interface OnFragmentInteractionListener {
        void onFragmentSetUserLocation(LatLng userLocation);
        void onFragmentSetNearbyPlacesId (ArrayList<String> nearbyPlacesId);
    }


    //----------------------------------------------------------------------------------------------
    // On Create
    //----------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        // Configure view
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMap);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapFragment.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    //----------------------------------------------------------------------------------------------
    // Get Fragment arguments
    //----------------------------------------------------------------------------------------------

    /**
     * This method get Fragment arguments. This arguments correspond to places Id has response for
     * toolbar search
     */
    private void getArgs(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            autocompletePlacesId = bundle.getStringArrayList("autocompletePlacesId");
        }

    }

    //----------------------------------------------------------------------------------------------
    // For Location
    //----------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Device location not allowed
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for location permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE);
        }
        // Device location allowed
        else {
            // Get MapFragment arguments
            getArgs();
                // If arguments are null : start Nearby places request
            if (autocompletePlacesId == null){
                mMap.setMyLocationEnabled(true);
                updateUserLocation();
                mMap.setOnMarkerClickListener(this);
            }
                // If arguments are not null : add marker on arguments places
            else {
                for (int x = 0 ; x < autocompletePlacesId.size(); x++){
                    executePlaceDetailsRequest(autocompletePlacesId.get(x));
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMarkerClickListener(this);
                }
            }

        }
    }

    //----------------------------------------------------------------------------------------------
    // For Restaurants Location
    //----------------------------------------------------------------------------------------------

    /**
     * This method configure google nearby places service options
     * @param optionsMap is a HashMap that contains fields and values for service
     * @param lastLocation is user location (necessary to start service)
     */
    private void setNearbyPlaceRequestOptions(HashMap<String, String> optionsMap, LatLng lastLocation){
        optionsMap.put(Constants.LOCATION, lastLocation.latitude + "," + lastLocation.longitude);
        //optionsMap.put("type", "food,restaurant");
        optionsMap.put(Constants.KEYWORD, Constants.KEYWORD_VALUES);
        optionsMap.put(Constants.RANK_BY, Constants.RANK_BY_VALUES);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);
    }

    /**
     * This method execute google nearby places service,add marker on every places as response,
     * send every Ids to mainActivity, and save every places to FireStore database
     * @param lastLocation is user location (necessary to start service)
     */
    private void getNearbyPlaces(LatLng lastLocation){
        // Set options map service
        HashMap<String, String> optionsMap = new HashMap<>();
        setNearbyPlaceRequestOptions(optionsMap, lastLocation);
        // Get nearby places
        disposable = GooglePlacesStream.streamFetchQueryRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlacesResponse>(){
                    @Override
                    public void onNext(GooglePlacesResponse value) {
                        Log.d("Places response", String.valueOf(value.getResults().size()));
                        ArrayList<String> nearbyPlacesId = new ArrayList<>();
                        if (value.getResults().size() > 0){
                        for (int x = 0; x < value.getResults().size(); x++) {
                            // Add restaurant to DataBase
                            addRestaurantToDataBase(x, value);
                            // Add marker on every restaurant
                            addMarkerOnRestaurant(x, value);
                            nearbyPlacesId.add(value.getResults().get(x).getPlaceId());
                            }
                            // Send nearby places id to MainActivity
                            ((OnFragmentInteractionListener) Objects.requireNonNull(getActivity())).onFragmentSetNearbyPlacesId(nearbyPlacesId);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {e.printStackTrace();}
                    @Override
                    public void onComplete() {}
                });
    }

    /**
     * This method add restaurant to FireStore databas
     * @param x is response index for this place
     * @param value is GooglePlacesResponse instance
     */
    private void addRestaurantToDataBase(int x,GooglePlacesResponse value ){
        FireStoreRestaurantRequest.getRestaurant(value.getResults().get(x).getPlaceId())
                .addOnCompleteListener(task -> {
                    // If restaurant not yet created, we create restaurant
                    if (!Objects.requireNonNull(task.getResult()).exists()){
                        FireStoreRestaurantRequest.createRestaurant(value.getResults().get(x).getName(), value.getResults().get(x).getPlaceId(), value.getResults().get(x).getVicinity());
                    }
                });
    }

    /**
     * This method add marker on every places that contains google nearby places service response.
     * Marker color is green if any user already subscribe a place. Else marker is red
     * @param x is google nearby places response index
     * @param value is GooglePlacesResponse instance
     */
    private void addMarkerOnRestaurant(int x,GooglePlacesResponse value ){
        // Get Restaurant location into LatLng instance
        Double lat = value.getResults().get(x).getGeometry().getLocation().getLat();
        Double lng = value.getResults().get(x).getGeometry().getLocation().getLng();
        LatLng latLng = new LatLng(lat, lng);
        Log.d("Result location " , "Latitude = " + lat + " ! Longitutde = " + lng);
        // Set marker options
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(value.getResults().get(x).getName());
        // Get current date into sting value
        String currentDate = Helper.setCurrentDate(Calendar.getInstance());

        // Verify if restaurant have already at least one subscriber and add marker according response
        FireStoreRestaurantRequest.getSubscriberList(value.getResults().get(x).getPlaceId(), currentDate)
                .addOnSuccessListener(documentSnapshot -> {
                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                    if (subscribersCollection != null && subscribersCollection.getSubscribersList().size() > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_green));

                    }else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurants_red));
                    }
                    Marker mMarker = mMap.addMarker(markerOptions);
                    mMarker.setTag(value.getResults().get(x).getPlaceId());
                });
    }

    //----------------------------------------------------------------------------------------------
    // Get User location
    // ---------------------------------------------------------------------------------------------

    /**
     * This method get user Location, add a marker on his location and search restaurants nearby
     */
    private void updateUserLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.e("User Location ",  location.getLatitude() +" , " + location.getLongitude());
                        // Send user location to MainActivity
                        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng latLng = new LatLng(48.848071, 2.395671);
                        ((OnFragmentInteractionListener) Objects.requireNonNull(getActivity())).onFragmentSetUserLocation(latLng);
                        // Move camera to current position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        // Search nearby places
                        getNearbyPlaces(latLng);
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // When GPS is activate
    //----------------------------------------------------------------------------------------------

    /**
     * This method define app comportment when user allow app to get user location
     * @param requestCode is a request code
     * @param permissions is the current permission asked to user
     * @param grantResults is user response to this ask
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        updateUserLocation();
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // When user click on Marker
    //----------------------------------------------------------------------------------------------

    /**
     * This method will start RestaurantDetails activity
     * and pass place information as intent arguments
      * @param marker is the marker that user click
     * @return always false
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTag() != null) {
            // Set marker place Id into string value
            String markerPlaceId = (String) marker.getTag();
            //Start RestaurantDetails activity with restaurant details as arguments
            Intent intent = new Intent(getActivity(), RestaurantDetails.class);
            intent.putExtra(Constants.PLACE_DETAILS ,markerPlaceId);
            startActivity(intent);
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Update fragment for Autocomplete search
    //----------------------------------------------------------------------------------------------

    /**
     * This method execute google place details service for specific place and add marker
     * to this place location.
     * @param placeId is the Id from place that we want to execute google place details service
     */
    private void executePlaceDetailsRequest(String placeId) {
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, placeId);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

        GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceDetailsResponse>() {
                    @Override
                    public void onNext(GooglePlaceDetailsResponse value) {addMarkerOnRestaurant(value);}
                    @Override
                    public void onError(Throwable e) {e.printStackTrace();}
                    @Override
                    public void onComplete() {}
                });

    }

    /**
     *This method add marker on every places that contains google nearby places service response.
     *Marker color is green if any user already subscribe a place. Else marker is red
     * @param value is the google nearby places service response
     */
    private void addMarkerOnRestaurant(GooglePlaceDetailsResponse value ){

        // Get Restaurant location into LatLng instance
        Double lat = value.getResult().getGeometry().getLocation().getLat();
        Double lng = value.getResult().getGeometry().getLocation().getLng();
        LatLng latLng = new LatLng(lat, lng);
        Log.d("Result search location " , "Latitude = " + lat + " ! Longitutde = " + lng);
        // Set marker options
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(value.getResult().getName());
        // Get current date into sting value
        String currentDate = Helper.setCurrentDate(Calendar.getInstance());

        // Zoom camera on user location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        // Verify if restaurant have already at least one subscriber and add marker according response
        FireStoreRestaurantRequest.getSubscriberList(value.getResult().getPlaceId(), currentDate)
                .addOnSuccessListener(documentSnapshot -> {
                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);
                    if (subscribersCollection != null && subscribersCollection.getSubscribersList().size() > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_green));
                    }else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurants_red));
                    }
                    Marker mMarker = mMap.addMarker(markerOptions);
                    mMarker.setTag(value.getResult().getPlaceId());
                });
    }

    //----------------------------------------------------------------------------------------------
    // On Destroy
    //----------------------------------------------------------------------------------------------

    public void onDestroyView() {
        // Remove disposable
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
        super.onDestroyView();
    }
}
