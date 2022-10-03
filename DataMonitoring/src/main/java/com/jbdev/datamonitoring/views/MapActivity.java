package com.jbdev.datamonitoring.views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;



import com.jbdev.datamonitoring.R;
import com.jbdev.datamonitoring.database.model.State;
import com.jbdev.datamonitoring.datas.StatesCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MapActivity extends AppCompatActivity implements com.mapbox.mapboxsdk.maps.OnMapReadyCallback {

    private MapView mMap;
    boolean move = false;
    private MapboxMap mMap2;


    private String getMapTilerKey(){
        Log.i("MapActivity", "getMapTilerKey");
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            return pm.getApplicationInfo("com.jbdev.datamonitoring",
                    PackageManager.GET_META_DATA
            ).metaData.getString("com.maptiler.simplemap.mapTilerKey");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MapActivity", "onCreate");
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, null);
        setContentView(R.layout.activity_map);
        mMap = findViewById(R.id.mapView);
        mMap.onCreate(savedInstanceState);

        mMap.getMapAsync(this);
    }


    private void display(LatLng pos, String etat, String subscriber) {
        Log.d("MApActivity", "display " + pos + " " + etat);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(subscriber + ":" + etat);

        markerOptions.position(pos);

        mMap2.addMarker(markerOptions);
        if (!move) {

            mMap2.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(pos)
                            .zoom(15)
                            .build()
            ),2000);

        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        Log.i("MapActivity", "onMapReady");
        mMap2 = mapboxMap;

        String  mapTilerKey = getMapTilerKey();

        String  styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=xYfr8jK8KtfyCjRGLMLi";

        mMap2.setStyle(styleUrl, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Log.d("MApActivity", "ici2");
                List<State> states = StatesCollection.getInstamce().getList();

                mMap2.clear();

                LatLng oldpos = null;
                String oldEtat = null;
                String oldSubscriber= null;

                for (State state : states) {
                    Log.d("MApActivity", "ici");
                    if (state.getLatitude() == 0 && state.getLongitude() == 0) {
                        continue;
                    }
                    Log.d("MApActivity", "ici 3");
                    LatLng pos = new LatLng(state.getLatitude(), state.getLongitude());

                    String etat = state.getState();
                    String subscriber = state.getOperator();
                    int trace = state.getTrace();


                    if (trace == 0) {
                        Log.d("MApActivity", "call display");
                        display(pos, etat, subscriber);
                    }

                    if (trace == 1) {
                        Log.d("MApActivity", "trace = 1");
                        if (oldpos == null) {
                            oldpos = pos;
                            oldEtat = etat;
                            oldSubscriber = subscriber;
                            Log.d("MApActivity", "display oldpos");
                            display(pos, etat, subscriber);
                        } else {
                            // Discard excessive speed 42m/s => 151.2 km/h

                            if (state.getSpeed() > 42) {
                                continue;
                            }
                            int color;
                            switch (oldEtat) {
                                case "CONNECTED":
                                    color = Color.GREEN;
                                    break;
                                case "DISCONNECTED":
                                    color = Color.RED;
                                    break;
                                default:
                                    color = Color.YELLOW;

                                    break;
                            }
                            mMap2.addPolyline(new PolylineOptions()
                                    .add(oldpos, pos)
                                    .width(15)
                                    .color(color));
                            if (!oldEtat.equals(etat)) {
                                display(oldpos, oldEtat, oldSubscriber);
                                display(pos, etat, subscriber);

                            }
                            oldpos = pos;
                            oldEtat = etat;
                            oldSubscriber = subscriber;
                        }

                    } else {
                        if (oldpos != null) {
                            Log.d("MApActivity", "display End loop");
                            display(oldpos, oldEtat, oldSubscriber);
                        }
                        oldpos = null;
                    }
                }
                if (oldpos != null) {
                    Log.d("MApActivity", "oldpos");
                    Log.d("MApActivity", "display End loop");
                    display(oldpos, oldEtat, oldSubscriber);
                }
                oldpos = null;
            }
        });


        List<State> states = StatesCollection.getInstamce().getList();

        mMap2.clear();

        LatLng oldpos = null;
        String oldEtat = null;
        String oldSubscriber= null;


        for (State state : states) {

            if (state.getLatitude() == 0 && state.getLongitude() == 0) {
                continue;
            }
            LatLng pos = new LatLng(state.getLatitude(), state.getLongitude());

            String etat = state.getState();
            String subscriber = state.getOperator();
            int trace = state.getTrace();


            if (trace == 0) {
                display(pos, etat, subscriber);
            }

            if (trace == 1) {
                if (state.getProvider().equals("network") || state.getProvider().equals("NDF")) {
                    continue;
                }
                if (oldpos == null) {
                    oldpos = pos;
                    oldEtat = etat;
                    oldSubscriber = subscriber;
                    Log.d("MApActivity", "display oldpos");
                    display(pos, etat, subscriber);
                } else {
                    // Discard excessive speed 42m/s => 151.2 km/h

                    if (state.getSpeed() > 42) {
                        continue;
                    }
                    int color;
                    switch (oldEtat) {
                        case "CONNECTED":
                            color = Color.GREEN;
                            break;
                        case "DISCONNECTED":
                            color = Color.RED;
                            break;
                        default:
                            color = Color.YELLOW;

                            break;
                    }
                    mMap2.addPolyline(new PolylineOptions()
                            .add(oldpos, pos)
                            .width(15)
                            .color(color));
                    if (!oldEtat.equals(etat)) {
                        display(oldpos, oldEtat, oldSubscriber);
                        display(pos, etat, subscriber);

                    }
                    oldpos = pos;
                    oldEtat = etat;
                    oldSubscriber = subscriber;
                }

            } else {
                if (oldpos != null) {
                    Log.d("MApActivity", "display End loop");
                    display(oldpos, oldEtat, oldSubscriber);
                }
                oldpos = null;
            }
        }
        if (oldpos != null) {
            Log.d("MApActivity", "display End loop");
            display(oldpos, oldEtat, oldSubscriber);
        }
        oldpos = null;
    }




}
