package com.application.seb.go4lunch.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Helper;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private View itemView;
    private ArrayList<Restaurant> restaurantList;
    private SubscribersCollection subscribersCollection;
    String subscribRestaurant;


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
        textView.setText(user.getUsername() + " hasn't decided yet");
        textView.setTextColor(ContextCompat.getColor( itemView.getContext(),R.color.grey));
        textView.setTypeface(null, Typeface.ITALIC);

        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                getSubscriberList(user, restaurant);
                            }
                        }
                    }
                });
    }


    private void getSubscriberList(User user, Restaurant restaurant) {
        String currentDate = Helper.setCurrentDate();
            // On prend la liste des subscribers via FireStore
            FireStoreRestaurantRequest
                    .getRestaurantSubscribersCollection(restaurant.getId())
                    .document(currentDate)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            subscribersCollection = Objects.requireNonNull(documentSnapshot).toObject(SubscribersCollection.class);
                            if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(user.getUid())) {
                                subscribRestaurant = restaurant.getName();
                            }
                            if (subscribRestaurant != null){
                                textView.setText(user.getUsername() +" is eating("+ subscribRestaurant + ")");
                                textView.setTypeface(null, Typeface.NORMAL);
                                textView.setTextColor(Color.BLACK);
                            }
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
