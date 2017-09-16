package com.example.edwin.traveling.System.System;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.edwin.traveling.R;
import com.example.edwin.traveling.System.MainActivity;

/**
 * Created by vskyl on 2017-09-16.
 */

public class SplashActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.splashscreen);
        Handler handler = new Handler();
        handler.postDelayed(new SHandler(),2000);
    }
    private class SHandler implements Runnable{
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
            // splash 화면을 Activity 스택에서 제거.
        }
    }
}
