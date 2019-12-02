package com.application.seb.go4lunch.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.BuildConfig;
import com.application.seb.go4lunch.Controller.RestaurantDetails;
import com.application.seb.go4lunch.Model.GooglePlaceDetailsResponse;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.application.seb.go4lunch.Utils.Helper;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapFragment extends Fragment implements
        OnMapReadyCallback
        ,GoogleMap.OnMarkerClickListener
        {

    // For data
    private GoogleMap mMap;
    private Disposable disposable;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<GooglePlacesResponse.Result> placesResponseList;
    private ArrayList<String> autocompletePlacesId;

            // Constructor
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void getArgs(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            autocompletePlacesId = bundle.getStringArrayList("autocompletePlacesId");
        }

    }

    //----------------------------------------------------------------------------------------------
    // Places list callback for MainActivity
    //----------------------------------------------------------------------------------------------

     public interface OnFragmentInteractionListener {
        void onFragmentSetGooglePlacesResponse(GooglePlacesResponse googlePlacesResponse);
        void onFragmentSetUserLocation(LatLng userLocation);
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
                }
            }

        }

    }

    //----------------------------------------------------------------------------------------------
    // For Restaurants Location
    //----------------------------------------------------------------------------------------------

    private void setNearbyPlaceRequestOptions(HashMap<String, String> optionsMap, LatLng lastLocation){
        optionsMap.put(Constants.LOCATION, lastLocation.latitude + "," + lastLocation.longitude);
        //optionsMap.put("type", "food,restaurant");
        optionsMap.put(Constants.KEYWORD, Constants.KEYWORD_VALUES);
        optionsMap.put(Constants.RANK_BY, Constants.RANK_BY_VALUES);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);
    }

    private void getNearbyPlaces(LatLng lastLocation){

        HashMap<String, String> optionsMap = new HashMap<>();
        setNearbyPlaceRequestOptions(optionsMap, lastLocation);

        disposable = GooglePlacesStream.streamFetchQueryRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlacesResponse>(){

                    @Override
                    public void onNext(GooglePlacesResponse value) {
                        Log.d("Places response", String.valueOf(value.getResults().size()));

                        //For data
                        placesResponseList = value.getResults();

                        if (value.getResults().size() > 0){
                        for (int x = 0; x < value.getResults().size(); x++) {

                            // Add restaurant to DataBase
                            addRestaurantToDataBase(x, value);
                            // Add marker on every restaurant
                            addMarkerOnRestaurant(x, value);
                            // Send GooglePlacesResponse to MainActivity
                            ((OnFragmentInteractionListener) Objects.requireNonNull(getActivity())).onFragmentSetGooglePlacesResponse(value);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {e.printStackTrace();}
                    @Override
                    public void onComplete() {}
                });
    }



    private void addRestaurantToDataBase(int x,GooglePlacesResponse value ){
        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .document(value.getResults().get(x).getPlaceId()).get()
                .addOnCompleteListener(task -> {
                    if (!Objects.requireNonNull(task.getResult()).exists()){
                        FireStoreRestaurantRequest.createRestaurant(value.getResults().get(x).getName(), value.getResults().get(x).getPlaceId(), value.getResults().get(x).getVicinity());
                    }
                });
    }

    private void addMarkerOnRestaurant(int x,GooglePlacesResponse value ){

         // Get Restaurant location
        Double lat = value.getResults().get(x).getGeometry().getLocation().getLat();
        Double lng = value.getResults().get(x).getGeometry().getLocation().getLng();
        Log.d("Result location " , "Latitude = " + lat + " ! Longitutde = " + lng);
        // Set marker options
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(value.getResults().get(x).getName());
        // Get current date into sting value
        String currentDate = Helper.setCurrentDate();

        // Verify if restaurant have already at least one subscriber and add marker according response
        FireStoreRestaurantRequest
                .getRestaurantSubscribersCollection(value.getResults().get(x).getPlaceId())
                .document(currentDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                    if (subscribersCollection != null && subscribersCollection.getSubscribersList().size() > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_green));

                    }else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurants_red));
                    }
                    Marker mMarker = mMap.addMarker(markerOptions);
                    mMarker.setTag(x);
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
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
            // Get marker data
            int markerTag = (int) marker.getTag();
            Log.d("Marker arguments", "marker list position is : " + markerTag);

            // Set marker place details to string value
            GooglePlacesResponse.Result placeInfos = placesResponseList.get(markerTag);
            Gson gson = new Gson();
            String stringPlaceInfos = gson.toJson(placeInfos);

            //Start RestaurantDetails activity with restaurant details as arguments
            Intent intent = new Intent(getActivity(), RestaurantDetails.class);
            intent.putExtra(Constants.PLACE_DETAILS ,stringPlaceInfos);
            startActivity(intent);
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------
    // Update fragment for Autocomplete search
    //----------------------------------------------------------------------------------------------

    private void executePlaceDetailsRequest(String placeId) {
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Constants.PLACE_ID, placeId);
        optionsMap.put(Constants.KEY, BuildConfig.PLACE_API_KEY);

        GooglePlacesStream.streamFetchDetailsRequestTotal(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlaceDetailsResponse>() {
                    @Override
                    public void onNext(GooglePlaceDetailsResponse value) {
                        Log.e("Map Fragment", "Places Details Total response ");
                        addMarkerOnRestaurant(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void addMarkerOnRestaurant(GooglePlaceDetailsResponse value ){

        // Get Restaurant location
        Double lat = value.getResult().getGeometry().getLocation().getLat();
        Double lng = value.getResult().getGeometry().getLocation().getLng();
        Log.e("Result search location " , "Latitude = " + lat + " ! Longitutde = " + lng);
        // Set marker options
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(value.getResult().getName());
        // Get current date into sting value
        String currentDate = Helper.setCurrentDate();

        // Verify if restaurant have already at least one subscriber and add marker according response
        FireStoreRestaurantRequest
                .getRestaurantSubscribersCollection(value.getResult().getPlaceId())
                .document(currentDate)
                .get()
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

        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();

        try {
            Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.mainMap));
            if (fragment != null) {
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
    }
}
