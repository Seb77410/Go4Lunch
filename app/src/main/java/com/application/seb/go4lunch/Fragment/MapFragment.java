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
import android.widget.Toast;

import com.application.seb.go4lunch.Controller.RestaurantDetails;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
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


import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapFragment extends Fragment implements
        OnMapReadyCallback
        ,GoogleMap.OnMarkerClickListener
        {

    public MapFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;
    private Marker currentUserLocationMarker;
    private Disposable disposable;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<GooglePlacesResponse.Result> placesResponseList;


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
            intent.putExtra("PLACE_DETAILS" ,stringPlaceInfos);
            startActivity(intent);
        }
        return false;
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
        //mFusedLocationProviderClient.requestLocationUpdates(mFusedLocationProviderClient.getLastLocation(),  , null);

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

        // Ask for location permission
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            updateUserLocation();
            mMap.setOnMarkerClickListener(this);
        }
    }

    private void getNearbyPlaces(LatLng lastLocation){
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put("location", lastLocation.latitude + "," + lastLocation.longitude);
        //optionsMap.put("type", "food,restaurant");
        optionsMap.put("keyword", "restaurant,pizza"); //TODO : Bar
        optionsMap.put("rankby", "distance");
        optionsMap.put("key", "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc");

        disposable = GooglePlacesStream.streamFetchQueryRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlacesResponse>(){

                    @Override
                    public void onNext(GooglePlacesResponse value) {
                        Log.e("Places response", value.toString());
                        Log.e("Places response", String.valueOf(value.getResults().size()));
                        placesResponseList = value.getResults();


                        if (value.getResults().size() > 0){
                        for (int x = 0; x < value.getResults().size(); x++) {
                            Double lat = value.getResults().get(x).getGeometry().getLocation().getLat();
                            Double lng = value.getResults().get(x).getGeometry().getLocation().getLng();
                            Log.e("Result location " , "Latitude = " + lat + " ! Longitutde = " + lng);


                            LatLng latLng = new LatLng(lat, lng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(value.getResults().get(x).getName());


                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_green));
                            Marker mMarker = mMap.addMarker(markerOptions);
                            mMarker.setTag(x);

                            // Send GooglePlacesResponse to MainActivity
                            ((OnFragmentInteractionListener) Objects.requireNonNull(getActivity())).onFragmentSetGooglePlacesResponse(value);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // Get User location
    // ---------------------------------------------------------------------------------------------

    private void updateUserLocation() {

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.e("User Location ",  location.getLatitude() +" , " + location.getLongitude());

                        // Send user location to MainActivity
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        ((OnFragmentInteractionListener) Objects.requireNonNull(getActivity())).onFragmentSetUserLocation(latLng);

                        // Remove old marker if necessary
                        if (currentUserLocationMarker != null) {
                            currentUserLocationMarker.remove();
                        }

                        // Add marker to User current position
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("It's me");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        currentUserLocationMarker = mMap.addMarker(markerOptions);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toast.makeText(getContext(), "Le resultat de la requete", Toast.LENGTH_LONG).show();

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
