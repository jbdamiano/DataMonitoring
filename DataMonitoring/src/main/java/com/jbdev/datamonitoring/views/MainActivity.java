package com.jbdev.datamonitoring.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.jbdev.datamonitoring.R;
import com.jbdev.datamonitoring.database.DatabaseHelper;
import com.jbdev.datamonitoring.database.model.State;
import com.jbdev.datamonitoring.datas.StatesCollection;
import com.jbdev.datamonitoring.services.BackgroundLocationService;
import com.jbdev.datamonitoring.services.BackgroundStateService;
import com.jbdev.datamonitoring.services.ServerService;
import com.jbdev.datamonitoring.utils.CurrentStateAndLocation;
import com.jbdev.datamonitoring.utils.Logger;
import com.jbdev.datamonitoring.utils.MyDividerItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static private com.jbdev.datamonitoring.views.StatesAdapter mAdapter;

    protected RecyclerView recyclerView;
    public DatabaseHelper db;
    StatesCollection statesCollection;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity instance = null;
    CurrentStateAndLocation currentStateAndLocation = CurrentStateAndLocation.getInstance();
    public BackgroundLocationService gpsService;
    public boolean stateServiceStarted = false;
    static private boolean locationChangeRecording = false;
    static private boolean updated = false;

    public static MainActivity getInstance() {
        return instance;
    }

    public void startStateService() {
        if (!stateServiceStarted) {
            final Intent intentState = new Intent(this.getApplication(), BackgroundStateService.class);
            this.getApplication().startService(intentState);
            stateServiceStarted = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (instance == null) {
            instance = this;
        }

        checkAndAskForPermission();

        //CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        //TextView noNotesView = findViewById(R.id.empty_state_view);

        db = new DatabaseHelper(this);
        statesCollection = StatesCollection.getInstamce();
        statesCollection.clear();
        statesCollection.addAll(db.getAllStates());

        if (mAdapter == null) {
            mAdapter = new StatesAdapter(this, statesCollection.getList());
        } else {
            mAdapter.updateData(statesCollection.getList());
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 26));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        startRecording(updated);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.send) {
            // Handle the camera action
            exportCsv();
        } else if (id == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //Read Update
            builder.setTitle("Delete");
            builder.setMessage("All entries will be deleted. Are you sure ?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                    db.deleteAllState();
                    statesCollection.clear();
                    mAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            //Setting the title manually
            //alert.setTitle("AlertDialogExample");
            alert.show();

        } else if (id == R.id.main) {

        } else if (id == R.id.login) {
            Intent var6 = new Intent(this, LoginActivity.class);
            PendingIntent var9 = TaskStackBuilder.create(this).addNextIntentWithParentStack(var6).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            (new android.support.v4.app.NotificationCompat.Builder(this)).setContentIntent(var9);
            this.startActivity(var6);
        } else if (id == R.id.map) {
            // Use TaskStackBuilder to build the back stack and get the PendingIntent
            Intent detailsIntent = new Intent(this, MapActivity.class);
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(detailsIntent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
            // Intent launchNewIntent = new Intent(MainActivity.this,MapActivity.class);
            startActivity(detailsIntent);

        } else if (id == R.id.iccid) {
            SubscriptionManager sm = SubscriptionManager.from(this);
            String iccId = "Not allowed to get information";

            @SuppressLint("MissingPermission") List<SubscriptionInfo> sis = sm.getActiveSubscriptionInfoList();

            if (sis != null) {
                // getting first SubscriptionInfo
                SubscriptionInfo si = sis.get(0);

                // getting iccId
                iccId = si.getIccId();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //Read Update
            builder.setTitle("ICCID");
            builder.setMessage(iccId);
            builder.setCancelable(true);

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            //Setting the title manually
            //alert.setTitle("AlertDialogExample");
            alert.show();

        } else if (id == R.id.start) {
            locationChangeRecording = !locationChangeRecording;
            if (locationChangeRecording) {
                item.setTitle("stop recording location");
            } else {
                item.setTitle("start recording location");
            }

            invalidateOptionsMenu();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        Logger.dump("onResume");
        super.onResume();
        if (statesCollection.getList() != null) {
            mAdapter.updateData(statesCollection.getList());
        }
        updateMenuTitle();
    }

    @Override
    protected void onRestart() {
        Logger.dump("onRestart");
        super.onRestart();
        if (statesCollection.getList() != null) {
            mAdapter.updateData(statesCollection.getList());
        }
        updateMenuTitle();
    }

    public boolean isLocationChangeRecording() {
        return locationChangeRecording;
    }

    private void updateMenuTitle() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) {
            return;
        }
        Menu menu = navigationView.getMenu();
        if (menu != null && menu.size() > 0) {
            if (locationChangeRecording) {
                menu.findItem(R.id.start).setTitle("stop recording location");
            } else {
                menu.findItem(R.id.start).setTitle("start recording location");
            }
        }
        startRecording(updated);
    }

    private void startRecording(boolean value) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) {
            return;
        }
        Menu menu = navigationView.getMenu();
        if (menu != null && menu.size() > 0) {

            menu.findItem(R.id.start).setVisible(value);
        }
    }
    private void checkAndAskForPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        4);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        5);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            if (checkBatteryOptimized()) {
                //Dispaya warning message
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //Read Update
                builder.setTitle("Info");
                builder.setMessage("For collecting dsta in background, the application must not be optimized." +
                        "Disable it?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        try {
                            startActivity(intent);
                        }catch(Exception ignored) {

                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                //Setting the title manually
                //alert.setTitle("AlertDialogExample");
                alert.show();

            }

            SubscriptionManager sm = SubscriptionManager.from(this);

            List<SubscriptionInfo> sis = sm.getActiveSubscriptionInfoList();

            if (sis != null) {
                // getting first SubscriptionInfo
                SubscriptionInfo si = sis.get(0);

                // getting iccId
                String iccId = si.getIccId();
                Log.d("********************", iccId);
                Logger.dump("******************** ICCID: " + iccId);
            }
            final Intent intent = new Intent(this.getApplication(), BackgroundLocationService.class);
            this.getApplication().startService(intent);

            startStateService();

        }
    }

    private boolean checkBatteryOptimized() {

        PowerManager pwrm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        String name = getPackageName();
        return !pwrm.isIgnoringBatteryOptimizations(name);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        checkAndAskForPermission();
    }

    @SuppressLint("ShowToast")
    public void exportCsv() {
        @SuppressLint("SdCardPath") File myFile = new File("/sdcard/export.csv");
        try {
            final boolean newFile = myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("timestamp;state;reason;networktype;operator;imsi;latitude;longitude;recording;gps operator; speed\n");
            List<State> states = db.getAllStates();
            for (State state : states) {
                myOutWriter.append(state.getTimestamp()).append(";").append(state.getState()).
                        append(";").append(state.getReason()).append(";").
                        append(state.getSubtype()).append(";").append(state.getOperator()).
                        append(";").append(state.getImsi()).append(";").
                        append(String.valueOf(state.getLatitude())).append(";").
                        append(String.valueOf(state.getLongitude())).append(";").
                        append(String.valueOf(state.getTrace())).append(";").
                        append(state.getProvider()).append(";").append(String.valueOf(state.getSpeed())).append("\n");
            }
            myOutWriter.close();
            Uri u1;

            u1 = Uri.fromFile(myFile);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "state export");
            sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
            //sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jdamiano@sierrawireless.com"});
            sendIntent.setType("text/html");
            MainActivity.getInstance().startActivity(Intent.createChooser(sendIntent, "E-mail"));


        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error exporting", Toast.LENGTH_LONG);
        }


    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                Object gpsService = ((BackgroundLocationService.LocationServiceBinder) service).getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };


    public void createState(String state, String reason, String subtype, String operator, String imsi) {
        long id = db.insertState(state, reason, subtype, operator, imsi, currentStateAndLocation.getLatitude(), currentStateAndLocation.getLongitude(), locationChangeRecording ? 1 : 0, currentStateAndLocation.getProvider(), currentStateAndLocation.getSpeed());

        State n = db.getState(id);

        if (n != null) {

            statesCollection.add(0, n);
            if (statesCollection.getList() != null) {
                mAdapter.updateData(statesCollection.getList());
            }
        }
        updated = true;
        startRecording(updated);
    }


    public void startServerService() {

        Log.d("Main", "Start service ICI");
        Intent var2 = new Intent(this.getApplication(), ServerService.class);
        this.getApplication().startService(var2);

    }

}
