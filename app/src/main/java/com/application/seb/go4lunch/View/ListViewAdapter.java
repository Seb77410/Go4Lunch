package com.application.seb.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewViewHolder> {

    // For data
   private LatLng userLocation;
   private RequestManager glide;
   private ArrayList<String> nearbyPlacesId;
   public void setNearbyPlacesId(ArrayList<String> nearbyPlacesId) {this.nearbyPlacesId = nearbyPlacesId;}

   // Constructor
    public ListViewAdapter(ArrayList<String> nearbyPlacesId, LatLng userLocation, RequestManager glide) {
        this.nearbyPlacesId = nearbyPlacesId;
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

        return new ListViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        String placeId = nearbyPlacesId.get(position);
        holder.updateWithPlacesList(placeId, userLocation, glide);
    }

    @Override
    public int getItemCount() {
        if (nearbyPlacesId == null) {
            Log.d("ListViewAdapter", "Places list = null");
            return 0;
        } else {
            Log.d("ListViewAdapter", "Places list size : " + nearbyPlacesId.size());
            return nearbyPlacesId.size();
        }
    }
}
