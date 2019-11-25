package com.application.seb.go4lunch.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Helper;
import com.application.seb.go4lunch.View.WorkmatesAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkmatesAdapter adapter;
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private ArrayList<User> UsersList = new ArrayList<>();
    private ArrayList<SubscribersCollection> subscribersCollections = new ArrayList<>();

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentResult = inflater.inflate(R.layout.fragment_workmates, container, false);
        recyclerView = fragmentResult.findViewById(R.id.workmatesFragment_recyclerView);

        getFormatUsersList();
        return fragmentResult;
    }


    // ---------------------------------------------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------------------------------------------

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(List<User> userList) {

        //Log.e("User list : ", userList.toString());
        //Log.e("User list name 1: ", userList.get(0).getUsername());
        //Log.e("User list size : ", String.valueOf(userList.size()));
        //Log.e("User list name 2: ", userList.get(1).getUsername());

        // Create adapter passing the list of users
        this.adapter = new WorkmatesAdapter(userList,Glide.with(this));
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // ---------------------------------------------------------------------------------------------
    // FireStore
    // ---------------------------------------------------------------------------------------------

    private void getFormatUsersList() {

        FireStoreUserRequest
                .getUsersCollection()
                .orderBy("alreadySubscribeRestaurant", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            User user = document.toObject(User.class);
                            UsersList.add(user);
                        }
                        Log.e("La liste d'users", "est égale a " + UsersList.size());

                        configureRecyclerView(UsersList);
                        adapter.notifyDataSetChanged();
                    }
                });

    }

}