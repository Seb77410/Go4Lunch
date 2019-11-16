package com.application.seb.go4lunch.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.View.ListViewAdapter;
import com.application.seb.go4lunch.View.SubscribersAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribersFragment extends Fragment {

    private ArrayList<String> subscribersList;
    private SubscribersAdapter adapter;
    private RecyclerView recyclerView;

    public SubscribersFragment() {
        // Required empty public constructor
    }

    public SubscribersFragment(ArrayList<String> subscribersList) {
        this.subscribersList = subscribersList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribers, container, false);
        recyclerView = view.findViewById(R.id.fragment_subscribers_recyclerView);
        Log.e("SubscribersFragment", "On est bien dans le fragment");

        configureRecyclerView();
        return view;
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {

        // Create adapter passing the list of users
        this.adapter = new SubscribersAdapter(Glide.with(this),subscribersList);
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
