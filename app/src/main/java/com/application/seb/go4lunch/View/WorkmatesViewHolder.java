package com.application.seb.go4lunch.View;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;


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

    void updateWithWorkmatesList(User user, RequestManager glide){

        // If user selected a restaurant
        if(user.getRestaurant() != null) {
            textView.setText(user.getUsername() + "(" + user.getRestaurant() + ")");
        }
        else{
            textView.setText(user.getUsername() + " hasn't decided yet");
            textView.setTextColor(ContextCompat.getColor( itemView.getContext(),R.color.grey));
            textView.setTypeface(null, Typeface.ITALIC);
        }

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
