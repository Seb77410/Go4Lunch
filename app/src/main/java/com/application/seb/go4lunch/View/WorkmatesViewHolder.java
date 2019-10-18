package com.application.seb.go4lunch.View;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;


 class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;

    WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userTextView);
        imageView = itemView.findViewById(R.id.workmatesFragment_recyclerView_userImage);
    }

    void updateWithWorkmatesList(User user, RequestManager glide){
        Log.e("Image url : ", user.getUrlPicture());
        textView.setText(user.getUsername());
        glide.load(user.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(imageView);
    }

}
