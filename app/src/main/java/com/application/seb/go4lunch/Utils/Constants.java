package com.application.seb.go4lunch.Utils;


public class Constants {

    // For place Autocomplete
    public static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    //----------------------------------------------------------------------------------------------
    // Activities arguments values
    //----------------------------------------------------------------------------------------------

    // RestaurantDetails activity Intent arguments values
    public static final String PLACE_DETAILS ="PLACE_DETAILS";

    // WebView Activity intent arguments value
    public static final String URL = "url";

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

    // Places Photo API values
    public static final String HEAD_LINK = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public static final String KEY_PARAMETERS = "&key=";

    // Places Details API parameters value
    public static final String PLACE_ID = "place_id";
    public static final String FIELDS = "fields";
    public static final String FIELDS_VALUES = "name,website,opening_hours,formatted_phone_number";
    public static final String KEY = "key";

    // Nearby Places API parameters values
    public static final String LOCATION = "location";
    public static final String KEYWORD = "keyword";
    public static final String KEYWORD_VALUES = "restaurant,pizza";
    public static final String RANK_BY = "rankby";
    public static final String RANK_BY_VALUES = "distance";


    // Return response from google API
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

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
    static final int NOTIFICATIONS_CHANNEL_ID = 3;

    //----------------------------------------------------------------------------------------------
    // RestaurantDetails activity
    //----------------------------------------------------------------------------------------------

    // ACTION_CALL Intent value
    public static final String TEL = "tel:";
}
