package com.example.khseob0715.chosunapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.khseob0715.chosunapp.R.id.stratBtn;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ANOTHER = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_a);


        Button startBtn = (Button)findViewById(stratBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewActivity02.class);
                startActivityForResult(intent,REQUEST_CODE_ANOTHER);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ANOTHER) {
            Toast toast = Toast.makeText(getBaseContext(), "onActivityResult called with code : " + resultCode, Toast.LENGTH_LONG);
            toast.show();

            if (resultCode == 1) {
                String name = data.getExtras().getString("name");
                toast = Toast.makeText(getBaseContext(),"result name : " + name,Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    public void onButton1Clicked(View v){
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.chosun.ac.kr"));
        startActivity(myIntent);
    }

    public void onButton2Clicked(View v) {
        Intent myIntent = new Intent(getApplicationContext(),NewActivity.class);
        startActivity(myIntent);
    }

}
