package com.example.edwin.traveling.System;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

/**
 * Created by Edwin on 2017-09-16.
 */

public class CommuneService extends Service {
    IBinder mBinder = new MedianBinder();

    private class MedianBinder extends Binder {
        Location location;

        public void setLocation(Location l){
            location.setLatitude(l.getLatitude());
            location.setLongitude(l.getLongitude());
        }

        public Location getLocation(Location l){
            return location;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
