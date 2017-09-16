package com.example.edwin.traveling.System;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Edwin on 2017-09-16.
 */

public class LocationService extends Service {
    LocationManager lm;

    private class CustomListener implements LocationListener {
        Location presentLocation = new Location("");

        public Location getLocation(){
            return presentLocation;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                presentLocation.setLatitude(location.getLatitude());
                presentLocation.setLongitude(location.getLongitude());

                Intent intent = new Intent("PosData");
                intent.putExtra("LA", (float) location.getLatitude());
                intent.putExtra("LO", (float) location.getLongitude());
                getApplicationContext().sendBroadcast(intent);

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    CustomListener locListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("TRIPLAY", "service onCreate");
        super.onCreate();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new CustomListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TRIPLAY", "service onStart");
        try {
            if( lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
                Log.d("TRIPLAY", "GPS");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            }
            else if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){
                Log.d("TRIPLAY", "NETWORK");
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            } else {
                Log.d("TRIPLAY", "UNABLE PROVIDER");
            }

        } catch(SecurityException ex){
            ex.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
