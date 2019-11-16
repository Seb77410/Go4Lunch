package com.application.seb.go4lunch.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.Controller.RestaurantDetails;
import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.ItemClickSupport;
import com.application.seb.go4lunch.View.ListViewAdapter;
import com.application.seb.go4lunch.View.WorkmatesAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    GooglePlacesResponse googlePlacesResponse;
    private ListViewAdapter adapter;
    private RecyclerView recyclerView;
    private LatLng userLocation;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance(GooglePlacesResponse googlePlacesResponse, LatLng userLocation){
        ListViewFragment frag = new ListViewFragment();
        frag.googlePlacesResponse = googlePlacesResponse;
        frag.userLocation = userLocation;
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        recyclerView = rootView.findViewById(R.id.listViewRecyclerView);

        if (googlePlacesResponse != null) {
            configureRecyclerView(googlePlacesResponse.getResults(), userLocation);
            configureOnClickRecyclerView(recyclerView);
        }else{
            //TODO : dire ce qu'il serra à faire si l'utilisateur n'est pas géolocaliser
        }
        return rootView;
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(List<GooglePlacesResponse.Result> placesList, LatLng userLocation) {

        // Create adapter passing the list of users
        this.adapter = new ListViewAdapter(placesList, userLocation ,Glide.with(this));
        // For recyclerView views not disappear
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     *Configure item click on RecyclerView
     * @param recyclerView is item recycler view
     */
    private void configureOnClickRecyclerView(RecyclerView recyclerView) {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_list_view_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int mPosition, View v) {

                        // Set marker place details to string value
                        GooglePlacesResponse.Result placeInfos = googlePlacesResponse.getResults().get(mPosition);
                        Gson gson = new Gson();
                        String stringPlaceInfos = gson.toJson(placeInfos);

                        //Start RestaurantDetails activity with restaurant details as arguments
                        Intent intent = new Intent(getActivity(), RestaurantDetails.class);
                        intent.putExtra("PLACE_DETAILS" ,stringPlaceInfos);
                        startActivity(intent);
                    }
                });
    }


}
