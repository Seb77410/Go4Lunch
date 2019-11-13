package com.application.seb.go4lunch.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.View.ListViewAdapter;
import com.application.seb.go4lunch.View.WorkmatesAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

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
        }else{
            //TODO : dire ce qu'il serra à faire si l'utilisateur n'est pas géolocaliser
        }
        return rootView;
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(List<GooglePlacesResponse.Result> placesList, LatLng userLocation) {

        // Create adapter passing the list of users
        this.adapter = new ListViewAdapter(placesList, userLocation ,Glide.with(this));
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
