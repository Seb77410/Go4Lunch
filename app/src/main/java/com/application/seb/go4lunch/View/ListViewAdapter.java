package com.application.seb.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {

   private List<GooglePlacesResponse.Result> placesList;
   private LatLng userLocation;
   private RequestManager glide;


    public ListViewAdapter(List<GooglePlacesResponse.Result> placesList, LatLng userLocation, RequestManager glide) {
        this.placesList = placesList;
        this.userLocation = userLocation;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create view holder and inflating its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_view_item, parent, false);

        Log.d("ListViewAdapter ", "Places list : " + placesList.toString());

        return new ListViewViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        GooglePlacesResponse.Result place = placesList.get(position);
        holder.updateWithPlacesList(place, userLocation, glide);
    }

    @Override
    public int getItemCount() {
        if (placesList == null) {
            Log.d("ListViewAdapter", "Places list = null");
            return 0;
        } else {
            Log.d("ListViewAdapter", "Places list size : " + placesList.size());
            return placesList.size();
        }
    }
}
