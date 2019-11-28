package com.application.seb.go4lunch.Utils;

public class Constants {

    // FireBase sign in code
    public static final int RC_SIGN_IN = 13;

    // For NavigationDrawer
    public static String  NAV_OPEN = "nav_open";
    public static String NAV_CLOSE = "nav_close";

    // For place Autocomplete
    public static int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Return response from google API
    public static String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

    // ACTION_CALL Intent value
    public static String TEL = "tel:";

    // RestaurantDetails activity Intent arguments values
    public static String PLACE_DETAILS ="PLACE_DETAILS";

    // WebView Activity intent arguments value
    public static String URL = "url";

    //----------------------------------------------------------------------------------------------
    // FireStore
    //----------------------------------------------------------------------------------------------

    // FireStore document value
    public static String USER_LIKE_LIST = "userLikeList";
    public static String SUBSCRIBERS_LIST = "subscribersList";
    public static String ALREADY_SUBSCRIBE_RESTAURANT = "alreadySubscribeRestaurant";
    public static String CURRENT_DATE = "currentDate";

    //FireStore BDD collection name
    public static final String USER_COLLECTION_NAME ="users";
    public static final String RESTAURANT_COLLECTION_NAME ="restaurants";
    public static final String SUBSCRIBERS_COLLECTION_NAME ="subscribers";


    //----------------------------------------------------------------------------------------------
    // Google API
    //----------------------------------------------------------------------------------------------

    // Location request code
    public static final int REQUEST_CODE = 12;

    // Places API service values
    static final String JSON_RETURN_FORMAT = "json";

    // Places Photo API values
    public static String HEAD_LINK = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public static String KEY_PARAMETERS = "&key=";

    // Places Details API parameters value
    public static String PLACE_ID = "place_id";
    public static String FIELDS = "fields";
    public static String FIELDS_VALUES = "name,website,opening_hours,formatted_phone_number";
    public static String KEY = "key";

    //----------------------------------------------------------------------------------------------
    // SharedPreferences
    //----------------------------------------------------------------------------------------------

    // For user subscribed restaurant
    public static String SUBSCRIBE_PLACE_PREF = "subscribePlace";
    public static String SUBSCRIBE_PLACE_PREF_VALUE = "place";


    //----------------------------------------------------------------------------------------------
    // /!\ A METTRE DANS LE FICHIER RESSOURCE STRING /!\
    //----------------------------------------------------------------------------------------------

    // Toast values
    public static String PLACE_JUST_LIKE = "You just like this restaurant";
    public static String NO_PHONE_NUMBER = "This restaurant have no phone number";
    public static String NO_WEBSITE = "This restaurant have not website";
}
