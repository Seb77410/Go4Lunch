package com.application.seb.go4lunch.utils;

public class Constants {

    //----------------------------------------------------------------------------------------------
    // Activities arguments values
    //----------------------------------------------------------------------------------------------

    // RestaurantDetails activity Intent arguments values
    public static final String PLACE_DETAILS ="PLACE_DETAILS";

    // WebView Activity intent arguments value
    public static final String URL = "url";

    //----------------------------------------------------------------------------------------------
    // Fragments TAG
    //----------------------------------------------------------------------------------------------

    public static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    public static final String LIST_VIEW_FRAGMENT_TAG = "LIST_VIEW_FRAGMENT";
    public static final String WORKMATES_FRAGMENT_TAG = "WORKMATES_FRAGMENT";

    //----------------------------------------------------------------------------------------------
    // FireStore
    //----------------------------------------------------------------------------------------------

    // FireStore document value
    public static final String USER_LIKE_LIST = "userLikeList";
    public static final String SUBSCRIBERS_LIST = "subscribersList";
    public static final String ALREADY_SUBSCRIBE_RESTAURANT = "alreadySubscribeRestaurant";
    public static final String CURRENT_DATE = "currentDate";
    public static final String ABLE_NOTIFICATIONS = "ableNotifications";

    //FireStore BDD collection name
    public static final String USER_COLLECTION_NAME ="users";
    public static final String RESTAURANT_COLLECTION_NAME ="restaurants";
    public static final String SUBSCRIBERS_COLLECTION_NAME ="subscribers";

    //----------------------------------------------------------------------------------------------
    // FireBase
    //----------------------------------------------------------------------------------------------

    // FireBase sign in code
    public static final int RC_SIGN_IN = 13;

    //----------------------------------------------------------------------------------------------
    // Google API
    //----------------------------------------------------------------------------------------------

    // Location request code
    public static final int REQUEST_CODE = 12;

    // Places API service values
    static final String JSON_RETURN_FORMAT = "json";
    public static final String KEY = "key";
    public static final String LOCATION = "location";

    // Places Details API parameters value
    public static final String PLACE_ID = "place_id";

    // Places photo API values
    public static final String BASE_PHOTO_API_REQUEST = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public static final String PHOTO_API_KEY_PARAMETERS = "&key=";

    // Nearby Places API parameters values
    public static final String KEYWORD = "keyword";
    public static final String KEYWORD_VALUES = "restaurant,bar";
    public static final String RANK_BY = "rankby";
    public static final String RANK_BY_VALUES = "distance";

    // Autocomplete Places API parameters values
    public static final String INPUT = "input";
    public static final String TYPES = "types";
    public static final String TYPES_VALUE = "establishment";
    public static final String RADIUS = "radius";
    public static final String RADIUS_VALUE = "10000";
    public static final String STRICTBOUNDS = "strictbounds";

    //----------------------------------------------------------------------------------------------
    // SharedPreferences
    //----------------------------------------------------------------------------------------------

    // For user subscribed restaurant
    public static final String SUBSCRIBE_PLACE_PREF = "subscribePlace";
    public static final String SUBSCRIBE_PLACE_PREF_VALUE = "place";

    //----------------------------------------------------------------------------------------------
    // Notifications values
    //----------------------------------------------------------------------------------------------

    static final String NOTIFICATIONS_TAG = "go4lunch";
    static final int NOTIFICATIONS_ID = 3;

    //----------------------------------------------------------------------------------------------
    // RestaurantDetails activity
    //----------------------------------------------------------------------------------------------

    // ACTION_CALL Intent value
    public static final String TEL = "tel:";
}
