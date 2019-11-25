package com.application.seb.go4lunch.Controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.R;
import com.application.seb.go4lunch.Utils.Helper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Objects;

import static com.application.seb.go4lunch.Utils.Constants.RC_SIGN_IN;

public class SignInActivity extends AppCompatActivity {


    private boolean alreadySignIn = false;

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
                        .setLogo(R.drawable.ic_logo_go4lunch)
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
        //Toast.makeText(getApplicationContext(), "result code = " + resultCode, Toast.LENGTH_LONG).show();
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Log.e("Utilisateur identifié ","User id = " + Objects.requireNonNull(this.getCurrentUser()).getUid() + ", User name = " + this.getCurrentUser().getDisplayName() + ", User photo url : " + String.valueOf(this.getCurrentUser().getPhotoUrl()));
                createUser();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                // setDrawerUserInfos();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Erreur : authentification annulée", Toast.LENGTH_LONG).show();
                    startSignInActivity();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() ==  ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), "Erreur : pas d'internet ", Toast.LENGTH_LONG).show();
                    startSignInActivity();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), "Erreur inconnue", Toast.LENGTH_LONG).show();
                    startSignInActivity();
                }
            }
        }else {
            startSignInActivity();
        }
    }

    private void createUser(){
        FireStoreUserRequest
                .getUsersCollection()
                .document(Objects.requireNonNull(getCurrentUser()).getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.exists()){
                        // We save User infos into FireStore database
                        if (getCurrentUser().getPhotoUrl() != null) {
                            FireStoreUserRequest.createUser(Objects.requireNonNull(getCurrentUser()).getUid(), getCurrentUser().getDisplayName(), Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString(), Helper.setCurrentDate())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }else{
                            FireStoreUserRequest.createUser(Objects.requireNonNull(getCurrentUser()).getUid(), getCurrentUser().getDisplayName(), Helper.setCurrentDate())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(Helper.getSignInValue(getApplicationContext())){
            startSignInActivity();
        }
        Log.e("SignIn Life cycle", "onResume");
    }



    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

}
