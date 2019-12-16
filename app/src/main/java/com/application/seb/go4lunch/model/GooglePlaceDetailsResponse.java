package com.application.seb.go4lunch.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GooglePlaceDetailsResponse {

    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("status")
    @Expose
    private String status;

    public Result getResult() { return result; }

    public String getStatus() {
        return status;
    }

    public class Close {

        @SerializedName("day")
        @Expose
        private Integer day;
        @SerializedName("time")
        @Expose
        private String time;

        public Integer getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }

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

    public class Open {

        @SerializedName("day")
        @Expose
        private Integer day;
        @SerializedName("time")
        @Expose
        private String time;

        public Integer getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }

    }

    public class OpeningHours {

        @SerializedName("open_now")
        @Expose
        private Boolean openNow;
        @SerializedName("periods")
        @Expose
        private List<Period> periods = null;

        public Boolean getOpenNow() {
            return openNow;
        }

        public List<Period> getPeriods() {
            return periods;
        }
    }

    public class Period {

        @SerializedName("close")
        @Expose
        private Close close;
        @SerializedName("open")
        @Expose
        private Open open;

        public Close getClose() {return close; }

        public Open getOpen() {
            return open;
        }
    }

    public class Photo {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("photo_reference")
        @Expose
        private String photoReference;
        @SerializedName("width")
        @Expose
        private Integer width;

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getPhotoReference() {
            return photoReference;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }
    }

    public class Result {

        @SerializedName("formatted_phone_number")
        @Expose
        private String formattedPhoneNumber;
        @SerializedName("geometry")
        @Expose
        private Geometry geometry;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("opening_hours")
        @Expose
        private OpeningHours openingHours;
        @SerializedName("photos")
        @Expose
        private List<Photo> photos = null;
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @SerializedName("rating")
        @Expose
        private float rating;
        @SerializedName("vicinity")
        @Expose
        private String vicinity;
        @SerializedName("website")
        @Expose
        private String website;

        public String getFormattedPhoneNumber() {
            return formattedPhoneNumber;
        }

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

        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public String getPlaceId() {
            return placeId;
        }

        public float getRating() {
            return rating;
        }

        public String getVicinity() {
            return vicinity;
        }

        public String getWebsite() {
            return website;
        }
    }
}