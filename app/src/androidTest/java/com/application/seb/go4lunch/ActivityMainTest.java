package com.application.seb.go4lunch;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.application.seb.go4lunch.Controller.MainActivity;
import com.application.seb.go4lunch.Controller.RestaurantDetails;
import com.application.seb.go4lunch.Controller.SettingsActivity;
import com.application.seb.go4lunch.Controller.SignInActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


public class ActivityMainTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void toolBarTest(){
        onView(withId(R.id.activity_main_toolbar)).check(matches(isDisplayed()));
        onView(withText(R.string.toolbar_title)).check(matches(isDisplayed()));
        onView(withId(R.id.search_menu)).check(matches(isDisplayed()));
    }

    @Test
    public void bottomViewTest(){
        onView(withId(R.id.activity_main_bottom_navigation)).check(matches(isDisplayed()));

        // "MAP VIEW" fragment
        onView(withId(R.id.action_map)).check(matches(isDisplayed()));
        onView(withId(R.id.mainMap)).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(3000));

        // "LIST VIEW" fragment
        onView(withId(R.id.action_list)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.main_list_view_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.listViewRecyclerView)).check(matches(isDisplayed())).perform(click());
            // Click on item recycler view
        onView(withId(R.id.listViewRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        intended(hasComponent(RestaurantDetails.class.getName()));
        restaurantDetailsTest();
        onView(isRoot()).perform(pressBack());

        // "WORKMATES" fragment
        onView(withId(R.id.action_workmates)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.main_list_view_workmates)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.workmatesFragment_recyclerView)).check(matches(isDisplayed())).perform(click());
    }


    @Test
    public void drawerLayoutTest(){
        // Open drawer menu
        onView(withId(R.id.activity_main_drawer_layout)).perform(open());
        onView(withId(R.id.drawer_bottom_image_view)).check(matches(isDisplayed()));

        // Navigation view header
        onView(withId(R.id.drawer_navigation_header)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_header_user_photo)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_header_title)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_header_user_infos_linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_header_user_name)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_header_user_email)).check(matches(isDisplayed()));

        // Navigation view menu item
            // "YOUR LUNCH" button
        onView(withText(R.string.your_lunch)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(RestaurantDetails.class.getName()));
        restaurantDetailsTest();
        onView(isRoot()).perform(pressBack());
        onView(withId(R.id.activity_main_drawer_layout)).perform(open());

            // "SETTING" Button
        onView(withText(R.string.settings)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
        onView(withId(R.id.activity_main_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_activity_able_notifications)).check(matches(isDisplayed()));
        onView(isRoot()).perform(pressBack());

            // "LOGOUT" button
        onView(withId(R.id.activity_main_drawer_layout)).perform(open());
        onView(withText(R.string.logout)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(SignInActivity.class.getName()));
    }

    /**
     * Perform action of waiting for a specific time.
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    private void restaurantDetailsTest(){
        onView(withId(R.id.restaurant_details_image)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_informations_contener)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_name)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_ratingBar)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_address)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_contact_contener)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_call_imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_call_textView)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_like_image)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_like_textView)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_website_image)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_website_textView)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_frameLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.restaurant_details_subscribe_button)).check(matches(isDisplayed()));
    }
}
