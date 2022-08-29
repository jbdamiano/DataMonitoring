package com.jbdev.datamonitoring.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.jbdev.datamonitoring.utils.ConnectivityChangeReceiver;
import com.jbdev.datamonitoring.utils.Logger;
import com.jbdev.datamonitoring.utils.PhoneCallBack;

public class BackgroundStateService extends IntentService {

    static final String MY_CONNECTIVITY_CHANGE = "CONNECTIVITY_CHANGE";
    //private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundStateService";
    static PhoneCallBack phoneCallBack = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BackgroundStateService() {
        super("stateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "In onStartCommand");
        Logger.dump("In onStartCommand");
        IntentFilter filter = new IntentFilter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createChangeConnectivityMonitor();
            filter.addAction(MY_CONNECTIVITY_CHANGE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground();
            else
                startForeground(1, new Notification());
        } else {
            ;
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }

        ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
        registerReceiver(
                connectivityChangeReceiver,
                filter
        );

        connectivityChangeReceiver.processUpdate();

        /*if (phoneCallBack == null) {
            TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
            phoneCallBack = new PhoneCallBack();
            tm.listen(phoneCallBack, PhoneStateListener.LISTEN_CALL_STATE
                    | PhoneStateListener.LISTEN_CELL_INFO // Requires API 17
                    | PhoneStateListener.LISTEN_CELL_LOCATION
                    | PhoneStateListener.LISTEN_DATA_ACTIVITY
                    | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                    | PhoneStateListener.LISTEN_SERVICE_STATE
                    | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                    | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                    | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);

        }*/
        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private void createChangeConnectivityMonitor() {
        final Intent intent = new Intent(MY_CONNECTIVITY_CHANGE);
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder().build(),
                    new ConnectivityManager.NetworkCallback() {

                        @Override
                        public void onUnavailable() {
                            sendBroadcast(intent);
                        }

                        /**
                         * @param network
                         */
                        @Override
                        public void onAvailable(Network network) {
                            sendBroadcast(intent);
                        }

                        /**
                         * @param network
                         */
                        @Override
                        public void onLost(Network network) {
                            sendBroadcast(intent);
                        }
                    });
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Logger.dump("In onTaskRemoved");
        Log.i(TAG, "In onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.dump("In onDestroy");
        Log.i(TAG, "In onDestroy");
    }

//  public class LocationServiceBinder extends Binder {
//    public BackgroundStateService getService() {
//      return BackgroundStateService.this;
//    }
//  }

}
