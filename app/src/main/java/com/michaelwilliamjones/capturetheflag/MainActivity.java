package com.michaelwilliamjones.capturetheflag;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;

public class MainActivity extends AppCompatActivity {
    CognitoUserPool userPool;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // cognito setup
        String userPoolId = "us-east-1_0XIfrCyzH";
        String clientId = "5vecsro21lfr9qdrs772vekkn8";
        String clientSecret = ""; // no secret
        Context context = getApplicationContext();
        userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret);

        //register button click handler
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                // load the register activity
                setContentView(R.layout.activity_registration);
            }
        });
    }
}
