package com.application.seb.go4lunch.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersViewHolder> {

    private RequestManager glide;
    private ArrayList<String> subscribersList;

    public SubscribersAdapter(RequestManager glide, ArrayList<String> subscribersList) {
        this.glide = glide;
        this.subscribersList = subscribersList;
    }

    @NonNull
    @Override
    public SubscribersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create view holder and inflating its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_subscribers_item, parent, false);

        return new SubscribersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribersViewHolder holder, int position) {
        String subscribersId =subscribersList.get(position);
        holder.updateWithSubscribersList(subscribersId, glide);
    }

    @Override
    public int getItemCount() {
        return subscribersList.size();
    }
}
