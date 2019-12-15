package com.application.seb.go4lunch.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AutocompleteResponse {


    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public String getStatus() {
        return status;
    }


    public class Prediction {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("place_id")
        @Expose
        private String placeId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlaceId() {
            return placeId;
        }


    }

}
