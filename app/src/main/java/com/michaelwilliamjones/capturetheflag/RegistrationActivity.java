package com.michaelwilliamjones.capturetheflag;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final RegistrationActivity thisActivity = this;

        // TECH DEBT: this seems like a hack.
        ((Button)findViewById(R.id.registrationSubmitButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.onRegistrationClick(v);
            }
        });
    }

    private String getTextFieldContents(int textFieldID) {
        return ((EditText)findViewById(textFieldID)).getText().toString();
    }

    public void onRegistrationClick(View view) {
        new BackgroundRegistrationTask(getBaseContext(), this).execute(
                // add registration fields here.
                getTextFieldContents(R.id.registrationUsername),
                getTextFieldContents(R.id.registrationPassword),
                getTextFieldContents(R.id.registrationEmail));
    }
}
