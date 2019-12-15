package com.application.seb.go4lunch.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.api.FireStoreUserRequest;
import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.view.WorkmatesAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private RecyclerView recyclerView;
    private WorkmatesAdapter adapter;
    private ArrayList<User> UsersList = new ArrayList<>();

    //----------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------
    public WorkmatesFragment() {
        // Required empty public constructor
    }

    //----------------------------------------------------------------------------------------------
    // OnCreate
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentResult = inflater.inflate(R.layout.fragment_workmates, container, false);
        recyclerView = fragmentResult.findViewById(R.id.workmatesFragment_recyclerView);

        getFormatUsersList();
        return fragmentResult;
    }

    //----------------------------------------------------------------------------------------------
    // Configure
    //----------------------------------------------------------------------------------------------

    /**
     * This method Configure RecyclerView, Adapter, LayoutManager & glue it together
     * @param userList is a that contains every user Ids
     */
    private void configureRecyclerView(List<User> userList) {
        // Create adapter passing the list of users
        this.adapter = new WorkmatesAdapter(userList,Glide.with(this));
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //----------------------------------------------------------------------------------------------
    // FireStore
    //----------------------------------------------------------------------------------------------

    /**
     * This method get every user Ids ordered by boolean into a list.
     * The boolean verify if user already subscribe a restaurant.
     * Firsts of this list have this boolean tu true
     */
    private void getFormatUsersList() {
        // Get user list in order of boolean subscribe restaurant value
        FireStoreUserRequest.getFormatUsersList()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        User user = document.toObject(User.class);
                        UsersList.add(user);
                    }
                    Log.d("Users list", "size = " + UsersList.size());
                    // Update view
                    configureRecyclerView(UsersList);
                    adapter.notifyDataSetChanged();
                });

    }

}