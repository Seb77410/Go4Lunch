package com.application.seb.go4lunch.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.view.SubscribersAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribersFragment extends Fragment {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private ArrayList<String> subscribersList;
    private RecyclerView recyclerView;

    //----------------------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------------------
    public SubscribersFragment() {}

    public SubscribersFragment(ArrayList<String> subscribersList) {
        this.subscribersList = subscribersList;
    }

    //----------------------------------------------------------------------------------------------
    // OnCreate
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribers, container, false);
        recyclerView = view.findViewById(R.id.fragment_subscribers_recyclerView);

        configureRecyclerView();
        return view;
    }

    //----------------------------------------------------------------------------------------------
    // Configure
    //----------------------------------------------------------------------------------------------
    /**
     * Configure RecyclerView, Adapter, LayoutManager & glue it together
     */
    private void configureRecyclerView() {

        // Create adapter passing the list of users
        SubscribersAdapter adapter = new SubscribersAdapter(Glide.with(this), subscribersList);
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
