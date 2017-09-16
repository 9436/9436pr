package com.example.edwin.traveling.System;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.edwin.traveling.R;
import com.example.edwin.traveling.System.System.APIGetter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(MainActivity.this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, 0);

        final long period = 1000;
        long time = SystemClock.currentThreadTimeMillis();

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time+period, period, pendingIntent);
    }

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
                startActivity(Mission);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final float START_X=(float) 37.206833;
        final float START_Y=(float) 126.990899;
        LatLng start = new LatLng(START_X, START_Y);

        drawMarker(googleMap, start, "시작점", "현재위치입니다", R.drawable.icon_player);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //get festival information from APIGetter
        APIGetter placeGetter = new APIGetter(APIGetter.ADJ_PLACE);
        APIGetter festGetter = new APIGetter(APIGetter.ADJ_FESTIVAL);
        placeGetter.addParam(START_Y);
        placeGetter.addParam(START_X);
        festGetter.addParam(START_Y);
        festGetter.addParam(START_X);

        try {
            placeGetter.start();
            festGetter.start();
            placeGetter.join();
            festGetter.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        ArrayList<TravelPlace> placeList = (ArrayList<TravelPlace>)placeGetter.getResult();
        ArrayList<TravelPlace> festivalList = (ArrayList<TravelPlace>)festGetter.getResult();
        Log.d("TRIPLAY", "SIZE="+festivalList.size());
        for(int i=0;i<festivalList.size();i++){
            TravelPlace cursor = festivalList.get(i);
            LatLng point = new LatLng(cursor.getY(), cursor.getX());
            drawMarker(googleMap, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_festival);
        }

        for(int i=0;i<placeList.size();i++){
            TravelPlace cursor = placeList.get(i);
            LatLng point = new LatLng(cursor.getY(), cursor.getX());
            switch(cursor.getType()){
                case TravelPlace.CULTURE:
                    drawMarker(googleMap, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_culture);
                    break;
                case TravelPlace.FOOD:
                    drawMarker(googleMap, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_food);
                    break;
                case TravelPlace.REPORTS:
                    drawMarker(googleMap, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_lesure);
                    break;
                case TravelPlace.TOUR:
                    drawMarker(googleMap, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_tour);
                    break;
            }

        }

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
