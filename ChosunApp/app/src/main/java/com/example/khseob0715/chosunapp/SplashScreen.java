package com.example.khseob0715.chosunapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.splashs_screen);
        Handler handler = new Handler();
        handler.postDelayed(new SHandler(),2000);
    }
    private class SHandler implements Runnable{
        public void run() {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            SplashScreen.this.finish();
            // splash 화면을 Activity 스택에서 제거.
        }
    }

}
