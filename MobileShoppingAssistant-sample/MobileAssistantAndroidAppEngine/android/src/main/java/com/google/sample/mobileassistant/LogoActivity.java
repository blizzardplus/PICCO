package com.google.sample.mobileassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(LogoActivity.this, SignInActivity.class);
                startActivity(i);
            }
        }, 3000);
    }

//    public void goToLogin(View view){
//        Intent intent = new Intent(this, SignInActivity.class);
//        startActivity(intent);
//    }

}
