package com.google.sample.mobileassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
    }

    public void goToLogin(View view){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

}
