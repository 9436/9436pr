package com.example.edwin.traveling.System;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.edwin.traveling.R;
import com.example.edwin.traveling.System.System.APIGetter;
import com.example.edwin.traveling.System.System.IconizedMenu;
import com.example.edwin.traveling.System.System.TravelPlace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    static int mission_img[] = {R.drawable.mis01,R.drawable.mis02,R.drawable.mis03,R.drawable.mis04,R.drawable.mis05};
    static int cle_img[] = {R.drawable.cle01,R.drawable.cle02,R.drawable.cle03,R.drawable.cle04,R.drawable.cle05};
    static boolean btn[] = {false, false, false, false, false};

    private ImageButton menuButton;

    private MarkerOptions userMarker;

    private ArrayList<TravelPlace> placeList;
    private ArrayList<TravelPlace> festivalList;

    private class PositionReceiver extends BroadcastReceiver {
        public float latitude;
        public float longitude;

        public PositionReceiver(){
            latitude = 0;
            longitude = 0;
        }

        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction().equals("PosData")){
                latitude = intent.getFloatExtra("LA", 0f);
                longitude = intent.getFloatExtra("LO", 0f);

                map.clear();
                drawUser(latitude, longitude);
                drawFestivalList(latitude, longitude);
                drawPlaceList(latitude, longitude);

            }
        }
    }

    PositionReceiver positionReceiver;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionReceiver = new PositionReceiver();
        IntentFilter filter = new IntentFilter("PosData");
        registerReceiver(positionReceiver, filter);

        menuButton = (ImageButton) findViewById(R.id.menubutton);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(MainActivity.this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, 0);

        final long period = 1000;
        long time = SystemClock.currentThreadTimeMillis();

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time+period, period, pendingIntent);
    }

    public void missionOnClick(View v){
        Intent Mission = new Intent(getApplicationContext(), MissionBoxActivity.class);
        Mission.putExtra("placeList",placeList);
        Mission.putExtra("festivalList",festivalList);
        startActivity(Mission);
    }

    public void menuOnClick(View v){
        IconizedMenu popup = new IconizedMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();

        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.menu_minus));

        inflater.inflate(R.menu.action_menu, menu);

        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.fest:
                        Toast.makeText(getApplicationContext(), "filter", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                    return false;
            }
        });

        popup.setOnDismissListener(new IconizedMenu.OnDismissListener() {
            @Override
            public void onDismiss(IconizedMenu menu) {
                menuButton.setImageDrawable(getResources().getDrawable(R.drawable.menu_plus));
            }
        });
        popup.show();

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.fest:
                Toast.makeText(this, "filter", Toast.LENGTH_SHORT).show();
                break;
            case R.id.questButton:
                Intent Mission = new Intent(getApplicationContext(), MissionBoxActivity.class);
                Mission.putExtra("placeList",placeList);
                Mission.putExtra("festivalList",festivalList);
                startActivity(Mission);
                break;
            default:
                break;
        }
        return true;
    }
*/
    private void drawPlaceList(float x, float y){
        //get festival information from APIGetter
        APIGetter placeGetter = new APIGetter(APIGetter.ADJ_PLACE);
        placeGetter.addParam(y);
        placeGetter.addParam(x);

        try {
            placeGetter.start();
            placeGetter.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        placeList = (ArrayList<TravelPlace>)placeGetter.getResult();

        for(int i=0;i<placeList.size();i++){
            TravelPlace cursor = placeList.get(i);
            LatLng point = new LatLng(cursor.getY(), cursor.getX());
            switch(cursor.getType()){
                case TravelPlace.CULTURE:
                    drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_culture);
                    break;
                case TravelPlace.FOOD:
                    drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_food);
                    break;
                case TravelPlace.REPORTS:
                    drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_lesure);
                    break;
                case TravelPlace.TOUR:
                    drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_tour);
                    break;
            }

        }
    }

    private void drawFestivalList(float x, float y){
        APIGetter festGetter = new APIGetter(APIGetter.ADJ_FESTIVAL);

        festGetter.addParam(y);
        festGetter.addParam(x);

        try {
            festGetter.start();
            festGetter.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        festivalList = (ArrayList<TravelPlace>)festGetter.getResult();
        Log.d("TRIPLAY", "SIZE="+festivalList.size());
        for(int i=0;i<festivalList.size();i++){
            TravelPlace cursor = festivalList.get(i);
            LatLng point = new LatLng(cursor.getY(), cursor.getX());
            drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_festival);
        }
    }

    private void drawUser(float x, float y){
        LatLng start = new LatLng(x, y);

        drawMarker(map, start, "나", "현재위치입니다", R.drawable.icon_player);
        map.moveCamera(CameraUpdateFactory.newLatLng(start));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final float START_X=(float) 37.206833;
        final float START_Y=(float) 126.990899;

        map = googleMap;
        drawUser(START_X, START_Y);
        drawFestivalList(START_X, START_Y);
        drawPlaceList(START_X, START_Y);

    }

    private void drawMarker(GoogleMap map, LatLng point, String title, String snippet, int icon){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);

        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), icon));
        markerOptions.icon(markerIcon);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        map.addMarker(markerOptions);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable){
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
