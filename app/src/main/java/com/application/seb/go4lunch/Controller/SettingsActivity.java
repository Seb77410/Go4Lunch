package com.application.seb.go4lunch.Controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Switch;

import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        FirebaseFirestore
                .getInstance()
                .collection(Constants.USER_COLLECTION_NAME)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);
                    if(Objects.requireNonNull(user).isAbleNotifications()){
                        ableNotification.setChecked(true);
                    }
                    updateNotificationsParameters();
                });
    }

    private void updateNotificationsParameters(){
        ableNotification.setOnCheckedChangeListener((buttonView, isChecked) -> FirebaseFirestore
                .getInstance()
                .collection(Constants.USER_COLLECTION_NAME)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .update(Constants.ABLE_NOTIFICATIONS, isChecked));
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
