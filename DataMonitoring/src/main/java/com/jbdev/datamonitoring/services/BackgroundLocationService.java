package com.jbdev.datamonitoring.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.jbdev.datamonitoring.utils.CurrentStateAndLocation;
import com.jbdev.datamonitoring.views.MainActivity;

public class BackgroundLocationService extends Service {

  private final LocationServiceBinder binder = new LocationServiceBinder();
  private final String TAG = "BackgroundLocationService";
  private LocationListener mLocationListener;
  private LocationListener mLocationListenerNetwork;
  private LocationManager mLocationManager;

  CurrentStateAndLocation currentStateAndLocation = CurrentStateAndLocation.getInstance();


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  private class LocationListener implements android.location.LocationListener {
    private final String TAG = "LocationListener";

    LocationListener(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
      Log.i(TAG, "LocationChanged: " + location);
      // Discard excessive speed 42m/s => 151.2 km/h
      //if (location.getSpeed() > 42) {
      //  return;
      //}
      Log.i(TAG, location.getProvider());
      Log.i(TAG, "" + location.getAccuracy());

      currentStateAndLocation.setLongitude(location.getLongitude());
      currentStateAndLocation.setSpeed(location.getSpeed());
      currentStateAndLocation.setLatitude(location.getLatitude());
      currentStateAndLocation.setProvider(location.getProvider());
      if (MainActivity.getInstance().isLocationChangeRecording()){
        MainActivity.getInstance().createState(currentStateAndLocation.getState(),
            currentStateAndLocation.getReason(), currentStateAndLocation.getSubtype(),
            currentStateAndLocation.getOperator(),currentStateAndLocation.getImsi());
      }

      //MainActivity.getInstance().startStateService();
    }


    @Override
    public void onProviderDisabled(String provider) {
      Log.e(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
      Log.e(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.e(TAG, "onStatusChanged: " + status);
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_NOT_STICKY;
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    // startForeground(12345678, getNotification());
    startTracking();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mLocationManager != null) {
      try {
        mLocationManager.removeUpdates(mLocationListener);
      } catch (Exception ex) {
        Log.i(TAG, "fail to remove location listners, ignore", ex);
      }
    }
  }

  private void initializeLocationManager() {
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
  }

  public void startTracking() {
    initializeLocationManager();
    mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);
    mLocationListenerNetwork = new LocationListener(LocationManager.NETWORK_PROVIDER);

    try {
      int LOCATION_INTERVAL = 1000; /* Every seconds */
      int LOCATION_DISTANCE = 50; /* Every 50 meters */
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);

      mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
    } catch (java.lang.SecurityException ex) {
      // Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
      // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
    }

  }

  public void stopTracking() {
    this.onDestroy();
  }


  @RequiresApi(api = Build.VERSION_CODES.O)
  private Notification getNotification() {

    NotificationChannel channel = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
    }

    NotificationManager notificationManager = getSystemService(NotificationManager.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannel(channel);

    }

    Notification.Builder builder = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
      return builder.build();
    }

    return null;
  }


  public class LocationServiceBinder extends Binder {
    public BackgroundLocationService getService() {
      return BackgroundLocationService.this;
    }
  }

}
