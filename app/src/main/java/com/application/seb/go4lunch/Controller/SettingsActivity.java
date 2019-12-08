package com.application.seb.go4lunch.Controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Switch;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Switch ableNotification;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ableNotification = findViewById(R.id.settings_activity_able_notifications);
        configureBackStack();
        setSwitchNotificationsButton();
    }

    private void setSwitchNotificationsButton(){

        FireStoreUserRequest.getUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addOnSuccessListener(documentSnapshot -> {
                    // Transform response into User instance
                    User user = documentSnapshot.toObject(User.class);
                    // If user as allowed notification
                    if(Objects.requireNonNull(user).isAbleNotifications()){
                        // Set notification button "ON"
                        ableNotification.setChecked(true);
                    }
                    updateNotificationsParameters();
                });
    }

    private void updateNotificationsParameters(){
        ableNotification.setOnCheckedChangeListener((buttonView, isChecked) ->
                FireStoreUserRequest.updateUserNotificationsBoolean(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), isChecked));
    }

    private void configureBackStack(){
        mToolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
        // Set back stack
        Drawable upArrow = ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_arrow_back_black_24dp, null);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
}
