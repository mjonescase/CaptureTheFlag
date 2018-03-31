package com.michaelwilliamjones.capturetheflag;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void onRegistrationClick(View view) {
        try {
            //Allow network on main thread
            StrictMode.ThreadPolicy defaultPolicy = StrictMode.getThreadPolicy();
            StrictMode.ThreadPolicy permitNetworkPolicy = new StrictMode.ThreadPolicy.Builder(defaultPolicy).permitNetwork().build();
            StrictMode.setThreadPolicy(permitNetworkPolicy);

            //Block until background thread completes
            new BackgroundRegistrationTask(getBaseContext()).execute("TODO").get();

            //Restore default policy
            StrictMode.setThreadPolicy(defaultPolicy);

            //Launch the MapsActivity.
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } catch (InterruptedException | ExecutionException e) {
            Log.d(TAG, "Exception with BackgroundLoginTask - " + e.getMessage());
        }
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
