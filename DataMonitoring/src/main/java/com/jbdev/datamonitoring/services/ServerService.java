//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jbdev.datamonitoring.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.jbdev.datamonitoring.utils.Logger;
import java.util.Timer;

public class ServerService extends IntentService {
    public static final String MY_CONNECTIVITY_CHANGE = "ServerService";
    public static final long serialVersionUID = -4208703141973047501L;
    private final String TAG = "Server Service";
    public Timer timer;

    public ServerService() {
        super("ServerService");

    }


    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        Log.i("ServerService", "onCreate");

    }

    public void onDestroy() {
        super.onDestroy();
        this.timer.cancel();
        Logger.dump("In onDestroy");
        Log.i("ServerService", "In onDestroy");

    }

    public void onHandleIntent(Intent intent) {
    }

    public int onStartCommand(Intent intent, int id, int currentId) {

        Log.i("ServerService", "In onStartCommand");
        Logger.dump("In onStartCommand");
        Task.settings = this.getSharedPreferences("ServerService", 0);
        Task.id = Task.settings.getInt("id", 0);
        Task.currentId = Task.settings.getLong("currentId", 0L);
        Log.i("ServerService", "start with id " + Task.currentId);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new Task(), 10000L, 300000L);
        return START_STICKY;
    }

    @TargetApi(14)
    public void onTaskRemoved(Intent intent) {

        super.onTaskRemoved(intent);
        this.timer.cancel();
        Logger.dump("In onTaskRemoved");
        Log.i("ServerService", "In onTaskRemoved");
    }

}
