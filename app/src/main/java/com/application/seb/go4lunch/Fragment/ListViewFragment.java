package com.application.seb.go4lunch.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.seb.go4lunch.Controller.RestaurantDetails;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.application.seb.go4lunch.Utils.ItemClickSupport;
import com.application.seb.go4lunch.View.ListViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> nearbyPlacesId;
    private ListViewAdapter listViewAdapter;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance(ArrayList<String> nearbyPlacesId, ListViewAdapter listViewAdapter){
        ListViewFragment frag = new ListViewFragment();
        frag.nearbyPlacesId = nearbyPlacesId;
        frag.listViewAdapter = listViewAdapter;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        recyclerView = rootView.findViewById(R.id.listViewRecyclerView);

        if (nearbyPlacesId != null) {
            configureRecyclerView();
            configureOnClickRecyclerView(recyclerView);
        }else{
            Toast.makeText(getContext(), getString(R.string.no_location), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {

        // For recyclerView views not error
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(listViewAdapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     *Configure item click on RecyclerView
     * @param recyclerView is item recycler view
     */
    private void configureOnClickRecyclerView(RecyclerView recyclerView) {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_list_view_item)
                .setOnItemClickListener((recyclerView1, mPosition, v) -> {

                    //Start RestaurantDetails activity with restaurant ID as arguments
                    Intent intent = new Intent(getActivity(), RestaurantDetails.class);
                    intent.putExtra(Constants.PLACE_DETAILS, nearbyPlacesId.get(mPosition));
                    startActivity(intent);

                });
    }


}
