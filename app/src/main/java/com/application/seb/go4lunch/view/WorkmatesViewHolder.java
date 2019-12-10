package com.application.seb.go4lunch.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.api.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.model.Restaurant;
import com.application.seb.go4lunch.model.SubscribersCollection;
import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.utils.Helper;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Objects;

class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private View itemView;
    private SubscribersCollection subscribersCollection;
    private String subscribeRestaurant;


    WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userTextView);
        imageView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userImage);
    }

    void updateWithWorkmatesList(User user,RequestManager glide){
        getRestaurantCollectionList(user);
        setImageView(user, glide);
    }


    // Get restaurant collection list from FireStore
    private void getRestaurantCollectionList(User user) {
        textView.setText(user.getUsername() + R.string.no_restaurant_select);
        textView.setTextColor(ContextCompat.getColor( itemView.getContext(),R.color.grey));
        textView.setTypeface(null, Typeface.ITALIC);

        FireStoreRestaurantRequest
                .restaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            getSubscriberList(user, restaurant);
                        }
                    }
                });
    }


    private void getSubscriberList(User user, Restaurant restaurant) {
        String currentDate = Helper.setCurrentDate(Calendar.getInstance());
            // On prend la liste des subscribers via FireStore
            FireStoreRestaurantRequest
                    .restaurantSubscribersCollection(restaurant.getId())
                    .document(currentDate)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        subscribersCollection = Objects.requireNonNull(documentSnapshot).toObject(SubscribersCollection.class);
                        if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(user.getUid())) {
                            subscribeRestaurant = restaurant.getName();
                        }
                        if (subscribeRestaurant != null){
                            textView.setText(user.getUsername() + R.string.is_eating +"(" +subscribeRestaurant + ")");
                            textView.setTypeface(null, Typeface.NORMAL);
                            textView.setTextColor(Color.BLACK);
                        }
                    });
    }

    private void setImageView(User user, RequestManager glide){
        // If user profile have photo
        if(user.getUrlPicture() != null) {
            Log.e("Image url : ", user.getUrlPicture());
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(imageView);
        }else {
            glide.load(R.drawable.no_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
            Log.e("WorkmatesViewHolder", "Workmates do not have image");
        }
    }

}
