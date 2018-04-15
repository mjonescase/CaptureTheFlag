package com.michaelwilliamjones.capturetheflag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private String getTextFieldContents(int textFieldID) {
        return ((EditText)findViewById(textFieldID)).getText().toString();
    }

    public void onLoginClick(View view) {
        new BackgroundLoginTask(getBaseContext(), this).execute(
                // add registration fields here.
                getTextFieldContents(R.id.loginUsername),
                getTextFieldContents(R.id.loginPassword));
    }

    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
