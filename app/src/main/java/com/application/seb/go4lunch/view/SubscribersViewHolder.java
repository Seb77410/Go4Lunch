package com.application.seb.go4lunch.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.api.FireStoreUserRequest;
import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

class SubscribersViewHolder  extends RecyclerView.ViewHolder {

    private ImageView subscribersPhoto;
    private TextView subscribersName;

    SubscribersViewHolder(@NonNull View itemView) {
        super(itemView);

        subscribersPhoto = itemView.findViewById(R.id.subscribers_imageView);
        subscribersName = itemView.findViewById(R.id.subscribers_textView);

    }

    /**
     * This method get and show user information on UI
     * @param subscriberId is user id
     * @param glide is RequestManager instance necessary to load user photo
     */
    void updateWithSubscribersList(String subscriberId, RequestManager glide){

        // Get user
        FireStoreUserRequest.getUser(subscriberId).addOnSuccessListener(documentSnapshot -> {
            // On le met dans un objet
            User user = documentSnapshot.toObject(User.class);

            if (user != null) {
                // Get user name and user photo
                String photoUrl = user.getUrlPicture();
                String name = user.getUsername();

                // Update user name
                subscribersName.setText(name + " is joining !");
                // Update user photo
                updateUserPhoto(glide, photoUrl);
            }
        });

    }

    /**
     * This method load user photo if user profile have photo
     * @param glide is a RequestManager instance necessary to load photo
     * @param photoUrl is user photo url that we want load
     */
    private void updateUserPhoto(RequestManager glide, String photoUrl){
        if(photoUrl != null) {
            glide.load(photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(subscribersPhoto);
        }
        else{
            glide.load(R.drawable.no_image)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(subscribersPhoto);
        }
    }

}
