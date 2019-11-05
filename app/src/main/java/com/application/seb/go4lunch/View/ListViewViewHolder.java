package com.application.seb.go4lunch.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.seb.go4lunch.Model.GooglePlacesResponse;
import com.application.seb.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

public class ListViewViewHolder extends RecyclerView.ViewHolder {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeHoraires;
    private TextView placeDistance;
    private TextView placeSubscribersNembers;
    private ImageView placeSubsciberImage;
    private LinearLayout placeStars;

    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        placeImage = itemView.findViewById(R.id.place_image);
        placeName = itemView.findViewById(R.id.place_name);
        placeAddress = itemView.findViewById(R.id.place_address);
        placeHoraires = itemView.findViewById(R.id.place_horaires);
        placeDistance = itemView.findViewById(R.id.place_distance);
        placeSubscribersNembers = itemView.findViewById(R.id.place_subscribers_numbers);
        placeSubsciberImage = itemView.findViewById(R.id.place_subscribers_image);
        placeStars = itemView.findViewById(R.id.place_star);

    }

    public void updateWithPlacesList(GooglePlacesResponse.Result place, RequestManager glide) {
        placeName.setText(place.getName());
        placeAddress.setText(place.getVicinity());

        // Place Horaires
        if (place.getOpeningHours() != null ) {
            if (place.getOpeningHours().getOpenNow()) {
                placeHoraires.setText("Is open");
                placeHoraires.setTextColor(Color.GREEN);

            } else {
                placeHoraires.setText("Is closed");
                placeHoraires.setTextColor(Color.RED);
            }
        }else {
            placeHoraires.setText("Horaires inconnus");
            placeHoraires.setTypeface(null, Typeface.ITALIC);
        }

        // Place Image
        if(place.get != null) {
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
    }
}
