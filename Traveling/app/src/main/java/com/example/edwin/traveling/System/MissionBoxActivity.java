package com.example.edwin.traveling.System;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.edwin.traveling.R;
import com.example.edwin.traveling.System.System.TravelPlace;

import java.util.ArrayList;

import static com.example.edwin.traveling.System.MainActivity.btn;
import static com.example.edwin.traveling.System.MainActivity.cle_img;
import static com.example.edwin.traveling.System.MainActivity.mission_img;

public class MissionBoxActivity extends AppCompatActivity {
    static int btnimg[] = {R.drawable.m01,R.drawable.m02,R.drawable.m03,R.drawable.m04,R.drawable.m05};

    private ArrayList<TravelPlace> placeList;
    private ArrayList<TravelPlace> festivalList;

    Location locationA,locationB;

    private class PositionReceiver extends BroadcastReceiver {
        public float latitude;
        public float longitude;

        public PositionReceiver(){
            latitude = 0;
            longitude = 0;
        }

        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("TRIPLAY", "Position Receive");
            locationA = new Location("pointA");
            locationB = new Location("pointB");

            if(intent.getAction().equals("PosData")){
                latitude = intent.getFloatExtra("LA", 0f);
                longitude = intent.getFloatExtra("LO", 0f);
                Log.d("distance", "Position Receive:"+latitude+","+longitude);
                // 내 좌표.
                locationA.setLatitude(latitude);
                locationA.setLatitude(longitude);
            }

            float distance;
            TravelPlace cursor = placeList.get(0);

            locationB.setLatitude(cursor.getX());
            locationB.setLongitude(cursor.getY());

            Log.d("distance","location: " + cursor.getY() + "," +  cursor.getX());
            distance = locationA.distanceTo(locationB);
            Log.d("distance",""+(float)distance);

        }
    }

    PositionReceiver positionReceiver;

    int sel_num;
    ImageView mission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.missionboxactivity);

        positionReceiver = new PositionReceiver();
        IntentFilter filter = new IntentFilter("PosData");
        registerReceiver(positionReceiver, filter);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        mission = (ImageView)findViewById(R.id.missionimg);
        Mission mission[] = new Mission[5];

        Intent it = getIntent();
        placeList = (ArrayList<TravelPlace>) it.getSerializableExtra("placeList");
        festivalList = (ArrayList<TravelPlace>) it.getSerializableExtra("festivalList");


        ImageView misbtn01 = (ImageView)findViewById(R.id.imageView2);
        ImageView misbtn02 = (ImageView)findViewById(R.id.imageView3);
        ImageView misbtn03 = (ImageView)findViewById(R.id.imageView4);
        ImageView misbtn04 = (ImageView)findViewById(R.id.imageView5);
        ImageView misbtn05 = (ImageView)findViewById(R.id.imageView6);


        if (btn[0]) {
            misbtn01.setImageResource(btnimg[0]);
        }
        if (btn[1]) {
            misbtn02.setImageResource(btnimg[1]);
        }
        if (btn[2]) {
            misbtn03.setImageResource(btnimg[2]);
        }
        if (btn[3]) {
            misbtn04.setImageResource(btnimg[3]);
        }
        if (btn[4]) {
            misbtn05.setImageResource(btnimg[4]);
        }

    }

    public void misbtn01(View view) {
        sel_num = 0;
        if(btn[sel_num]){
            mission.setImageResource(mission_img[sel_num]);
        }
    }
    public void misbtn02(View view) {
        sel_num = 1;
        if(btn[sel_num]){
            mission.setImageResource(mission_img[sel_num]);
        }
    }
    public void misbtn03(View view) {
        sel_num = 2;
        if(btn[sel_num]){
            mission.setImageResource(mission_img[sel_num]);
        }
    }
    public void misbtn04(View view) {
        sel_num = 3;
        if(btn[sel_num]){
            mission.setImageResource(mission_img[sel_num]);
        }
    }
    public void misbtn05(View view) {
        sel_num = 4;
        if(btn[sel_num]){
            mission.setImageResource(mission_img[sel_num]);
        }
    }


    public class Mission {
        private int Num;
        private boolean Find;
        private boolean Clear;
        private String Name;
        private TravelPlace Place;

        public boolean isFind(){
            return Find;
        }

        public boolean isClear() {
            return Clear;
        }

        public String getName() {
            return Name;
        }
        public int getNum(){
            return Num;
        }
/*
        public TravelPlace getPlace(){



        }
*/
    }


    public void clear(View view) { // clear를 누르면 초록색 미션이미지로 바뀜.
        if (btn[sel_num]) {
            mission_img[sel_num] = cle_img[sel_num];
            mission.setImageResource(mission_img[sel_num]);
        }
    }

    public void Exit(View view) {
        //ar버튼
        //finish();
    }

}
