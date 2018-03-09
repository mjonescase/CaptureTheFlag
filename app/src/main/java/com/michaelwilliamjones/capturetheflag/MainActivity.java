package com.michaelwilliamjones.capturetheflag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.michaelwilliamjones.capturetheflag.dataAccessObjects.FacebookProfileDAO;


public class MainActivity extends AppCompatActivity {
    Button registerButton;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        //register button click handler
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                // load the register activity
                setContentView(R.layout.activity_registration);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if ( accessToken != null ) {
            GraphRequest myProfileRequest = new FacebookProfileDAO(accessToken).fetchMyProfile();
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            // TODO handle failed fb login
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
