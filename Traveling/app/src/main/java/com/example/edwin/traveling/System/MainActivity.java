package com.example.edwin.traveling.System;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
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

    private int filter;

    private ImageButton menuButton;

    private MarkerOptions userMarker;
    private float userX;
    private float userY;
    private int zoom;

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
            if(intent.getAction().equals("userData")){
                Log.d("TRIPLAY", "Receive Position");
                latitude = intent.getFloatExtra("LA", 0f);
                longitude = intent.getFloatExtra("LO", 0f);
                Location newPos = new Location("NP");

                newPos.setLatitude( (double) latitude);
                newPos.setLongitude( (double) longitude);

                Location prePos = new Location("PP");

                prePos.setLatitude( (double) userX);
                prePos.setLongitude( (double) userY);

                if(newPos.distanceTo(prePos) > 150f) {
                    Log.d("TRIPLAY", "CHANGE ENVIRONMENT");
                    setFestivalInfo(latitude, longitude);
                    setPlaceInfo(latitude, longitude);
                }

                if(festivalCheck(newPos) > 0){
                    PendingIntent pintent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(getApplicationContext(), MainActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
                            .setSmallIcon(R.mipmap.icon_launcher)
                            .setContentTitle("근처에 축제가 있습니다.")
                            .setContentText("한 번 들려보시는 건 어떨까요?")
                            .setContentIntent(pintent);

                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(0, mBuilder.build());
                }

                map.clear();
                drawUser(latitude, longitude);
                switch(filter){
                    case 0:
                        drawFestivalList();
                        drawPlaceList();
                        break;
                    case TravelPlace.FESTIVAL:
                        drawFestivalList();
                        break;
                    case TravelPlace.TOUR:
                    case TravelPlace.CULTURE:
                    case TravelPlace.REPORTS:
                    case TravelPlace.FOOD:
                        drawPlaceList(filter);
                        break;
                }
            }

        }
    }

    PositionReceiver positionReceiver;
    GoogleMap map;

    private int festivalCheck(Location pos){
        Location loc = new Location("target");

        for(int i=0;i<festivalList.size();i++){
            loc.setLatitude( festivalList.get(i).getY() );
            loc.setLongitude( festivalList.get(i).getX() );

            if( pos.distanceTo(loc) < 50f){
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zoom = 15;
        filter = 0;
        positionReceiver = new PositionReceiver();
        IntentFilter filter = new IntentFilter("userData");
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

    public void zoomIn(View v){
        if(zoom <= 20){
            zoom += 1;
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    public void zoomOut(View v){
        if(zoom > 10){
            zoom -= 1;
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
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
                map.clear();
                drawUser(userX, userY);
                switch(item.getItemId()) {
                    case R.id.cancel:
                        filter = 0;
                        drawFestivalList();
                        drawPlaceList();
                        break;
                    case R.id.fest:
                        filter = TravelPlace.FESTIVAL;
                        drawFestivalList();
                        break;
                    case R.id.food:
                        filter = TravelPlace.FOOD;
                        drawPlaceList(filter);
                        break;
                    case R.id.tour:
                        filter = TravelPlace.TOUR;
                        drawPlaceList(filter);
                        break;
                    case R.id.repo:
                        filter = TravelPlace.REPORTS;
                        drawPlaceList(filter);
                        break;
                    case R.id.cult:
                        filter =TravelPlace.CULTURE;
                        drawPlaceList(filter);
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

    private void setPlaceInfo(float x, float y){
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
    }

    private void setFestivalInfo(float x, float y){
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
    }

    private void drawPlaceList(int selected){
        //get festival information from APIGetter
        for(int i=0;i<placeList.size();i++){
            TravelPlace cursor = placeList.get(i);
            if(cursor.getType() != selected){
                continue;
            }

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

    private void drawPlaceList(){
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

    private void drawFestivalList(){
        for(int i=0;i<festivalList.size();i++){
            TravelPlace cursor = festivalList.get(i);
            LatLng point = new LatLng(cursor.getY(), cursor.getX());
            drawMarker(map, point, cursor.getName(), cursor.getTypeName(), R.drawable.icon_festival);
        }
    }

    private void drawUser(float x, float y){
        LatLng start = new LatLng(x, y);

        userX = x;
        userY = y;

        userMarker = new MarkerOptions();
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_player));
        userMarker.icon(markerIcon);
        userMarker.title("나");
        userMarker.snippet("현재위치입니다.");
        userMarker.position(start);

        map.addMarker(userMarker);
        map.moveCamera(CameraUpdateFactory.newLatLng(start));
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom));

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final float START_X=(float) 37.206833;
        final float START_Y=(float) 126.990899;

        map = googleMap;
        drawUser(START_X, START_Y);
        setFestivalInfo(START_X, START_Y);
        setPlaceInfo(START_X, START_Y);

        drawFestivalList();
        drawPlaceList();

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
