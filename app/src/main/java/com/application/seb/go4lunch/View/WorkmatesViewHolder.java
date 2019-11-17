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
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private View itemView;


    WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userTextView);
        imageView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userImage);
    }

    void updateWithWorkmatesList(ArrayList<Restaurant> restaurantList, User user, RequestManager glide){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        // Convert current date into string value
        String currentDate = df.format(calendar.getTime());
        Log.e("RestaurantDetails", "currentDate = " + currentDate);

        // pour chaque restaurant
        for (int x = 0; x <= restaurantList.size()-1; x++){
            // On prend la liste des subscribers via FireStore
            String restaurantId = restaurantList.get(x).getId();
            String restaurantName = restaurantList.get(x).getName();
            FireStoreRestaurantRequest
                    .getRestaurantSubscribersCollection(restaurantId)
                    .document(currentDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                SubscribersCollection subscribersCollection = Objects.requireNonNull(task.getResult()).toObject(SubscribersCollection.class);
                                if (subscribersCollection != null) {
                                    ArrayList<String> subscriberList = subscribersCollection.getSubscribersList();
                                    setTextView(user, subscriberList, restaurantName);
                                }
                            }else {
                                Log.e("WorkmatesViewHolder", "FireStore restaurant request ERROR : " + task.getException());
                            }
                        }
                    });
        }
        setImageView(user, glide);
    }

    private void setTextView(User user, ArrayList<String> subscriberList, String restaurantName){

        if (subscriberList.contains(user.getUid())){
            textView.setText(user.getUsername() +" is eating ("+ restaurantName + ")");
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextColor(Color.BLACK);
        } else{
            textView.setText(user.getUsername() + " hasn't decided yet");
            textView.setTextColor(ContextCompat.getColor( itemView.getContext(),R.color.grey));
            textView.setTypeface(null, Typeface.ITALIC);

        }
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
