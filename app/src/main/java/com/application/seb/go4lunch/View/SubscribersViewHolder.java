package com.application.seb.go4lunch.View;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

class SubscribersViewHolder  extends RecyclerView.ViewHolder {

    private ImageView subscribersPhoto;
    private TextView subscribersName;

    SubscribersViewHolder(@NonNull View itemView) {
        super(itemView);

        subscribersPhoto = itemView.findViewById(R.id.subscribers_imageView);
        subscribersName = itemView.findViewById(R.id.subscribers_textView);

    }

    void updateWithSubscribersList(String subscriberId, RequestManager glide){

        // On recup l'utilisateur qui a l'id passé en paramétres
        FireStoreUserRequest.getUser(subscriberId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // On le met dans un objet
                User user = documentSnapshot.toObject(User.class);

                // Si cet objet n'est pas null
                if (user != null) {
                    // On recup sa photo et son nom d'utilisateur
                    String photoUrl = user.getUrlPicture();
                    String name = user.getUsername();

                    // On modifie l'affichage du nom
                    subscribersName.setText(name + " is joining!");
                    // On affiche sa photo
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

        });

    }



}
