package com.application.seb.go4lunch.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    // Values
    private List<User> userList;
    private RequestManager glide;

    // Constructor
    public WorkmatesAdapter(List<User> userList, RequestManager glide) {
        this.userList = userList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create view holder and inflating its xml layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_workmates_item, parent, false);

        Log.d("WorkmatesAdapter ", "UserList : " + userList.toString());

        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User user = userList.get(position);
        holder.updateWithWorkmatesList(user,glide);
    }

    @Override
    public int getItemCount() {

        if (userList == null) {
            Log.d("WorkmatesAdapter", "UserList = null");
            return 0;
        } else {
            Log.d("WorkmatesAdapter", "List size : " + userList.size());
            return userList.size();
        }
    }
}
