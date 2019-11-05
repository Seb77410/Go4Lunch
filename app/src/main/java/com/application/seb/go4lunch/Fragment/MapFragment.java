package com.application.seb.go4lunch.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.GooglePlacesStream;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;


import java.util.HashMap;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapFragment extends Fragment implements
        OnMapReadyCallback
        ,LocationListener
        ,GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener
        {

    public MapFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;


    public interface OnFragmentInteractionListener {
        void onFragmentSetGooglePlacesResponse(GooglePlacesResponse googlePlacesResponse);
    }


            //----------------------------------------------------------------------------------------------
    // On Create
    //----------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

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
            buildGoogleApiClient(); // ==> /!\/!\ Using deprecated method /!\/!\
            mMap.setMyLocationEnabled(true);
        }
    }



    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e("User Location ",  location.getLatitude() +" , " + location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("It's me");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

        getNearbyPlaces();
    }


    private void getNearbyPlaces(){
        HashMap<String, String> optionsMap = new HashMap<>();
        optionsMap.put("location", lastLocation.getLatitude() + "," + lastLocation.getLongitude());
        //optionsMap.put("radius", "150000");
        //optionsMap.put("type", "food,restaurant");
        optionsMap.put("keyword", "restaurant,pizza");
        optionsMap.put("rankby", "distance");
        optionsMap.put("key", "AIzaSyAp47kmngPnTKz7MY38uHXeJ7JwGoAcvQc");

        Disposable disposable = GooglePlacesStream.streamFetchQueryRequest(optionsMap)
                .subscribeWith(new DisposableObserver<GooglePlacesResponse>(){

                    @Override
                    public void onNext(GooglePlacesResponse value) {
                        Log.e("Places response", value.toString());
                        Log.e("Places response", String.valueOf(value.getResults().size()));


                        if (value.getResults().size() > 0){
                        for (int x = 0; x < value.getResults().size(); x++) {
                            Double lat = value.getResults().get(x).getGeometry().getLocation().getLat();
                            Double lng = value.getResults().get(x).getGeometry().getLocation().getLng();
                            Log.e("Result location " , "Latitude = " + lat + " ! Longitutde = " + lng);


                            LatLng latLng = new LatLng(lat, lng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(value.getResults().get(x).getName());

                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_green));

                            //markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.restaurant_green));
                            //markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.restaurants_red));

                            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap())

                            mMap.addMarker(markerOptions);

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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vertorRessourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vertorRessourceId);
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        }
        Bitmap bitmap = null;
        if (vectorDrawable != null) {
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //----------------------------------------------------------------------------------------------
    // Get User location
    // ---------------------------------------------------------------------------------------------


    // Start location request
    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getContext()))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                        buildGoogleApiClient(); // ==> /!\/!\ Using deprecated method /!\/!\
                        mMap.setMyLocationEnabled(true);
                        getNearbyPlaces();
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // On Destroy
    //----------------------------------------------------------------------------------------------

    public void onDestroyView()
    {
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
