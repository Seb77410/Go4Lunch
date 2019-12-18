package com.application.seb.go4lunch.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.utils.Helper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import static com.application.seb.go4lunch.utils.Constants.RC_SIGN_IN;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        startSignInActivity();
    }


    //----------------------------------------------------------------------------------------------
    // FireBase Login
    //----------------------------------------------------------------------------------------------

    /**
     * Launch and configure FireBase Sign-In Activity
     */
    private void startSignInActivity(){

        Helper.setSignInValue(getApplicationContext(), false);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        //.setLogo(R.drawable.ic_logo_go4lunch)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Result after sign in : add user data to FireStore database and notify user that sign-in is OK
     * or not OK with a Toast
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Log.d("Auth successful"
                        , "User id = " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                        + ", User name = " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                        + ", User photo url : " + FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()
                        + ", current date : " + Helper.setCurrentDate(Calendar.getInstance()));
                // Add user to FireStore database
                //createUser();
                // Start MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else { // ERRORS
                if (response == null) {
                    Log.e("SignIn activity", "Error : Auth cancel");
                    startSignInActivity();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() ==  ErrorCodes.NO_NETWORK) {
                    Log.e("SignIn activity", "Error : internet is OFF");
                    startSignInActivity();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("SignIn activity", "Unknown error");
                    startSignInActivity();
                }
            }
        }else {
            startSignInActivity();
        }
    }

    //----------------------------------------------------------------------------------------------
    // OnResume
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        if(Helper.getSignInValue(getApplicationContext())){
            startSignInActivity();
        }
        Log.d("SignIn Life cycle", "onResume");
    }


}
