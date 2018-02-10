package com.michaelwilliamjones.capturetheflag;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;




public class MainActivity extends AppCompatActivity {
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();

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
