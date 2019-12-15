package com.application.seb.go4lunch.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GooglePlacesResponse {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public class Geometry {

        @SerializedName("location")
        @Expose
        private Location location;

        public Location getLocation() {
            return location;
        }


    }

    public class Location {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public Double getLng() {
            return lng;
        }

    }

    public class Photo {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("width")
        @Expose
        private Integer width;

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

    }

    public class Result {

        @SerializedName("geometry")
        @Expose
        private Geometry geometry;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("photos")
        @Expose
        private List<Photo> photos = null;
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @SerializedName("vicinity")
        @Expose
        private String vicinity;

        public Geometry getGeometry() {
            return geometry;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getVicinity() {
            return vicinity;
        }

    }
}
