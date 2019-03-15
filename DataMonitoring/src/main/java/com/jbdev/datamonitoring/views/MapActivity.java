package com.jbdev.datamonitoring.views;

import android.graphics.Color;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jbdev.datamonitoring.R;
import com.jbdev.datamonitoring.database.model.State;
import com.jbdev.datamonitoring.datas.StatesCollection;

import java.util.Collections;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean move = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void display(LatLng pos, String etat, String subscriber, String imsi) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(subscriber + ":" + imsi);
        markerOptions.visible(true);
        markerOptions.position(pos);
        if (etat.equals("CONNECTED")) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (etat.equals("DISCONNECTED")) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        }
        mMap.addMarker(markerOptions);
        if (!move) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            move = true;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean toDisplay = false;

        List<State> states = StatesCollection.getInstamce().getList();

        mMap.clear();

        LatLng oldpos = null;
        String oldEtat = null;
        String oldSubscriber= null;
        String oldImsi = null;

        for (State state : states) {

            if (state.getLatitude() == 0 && state.getLongitude() == 0) {
                continue;
            }
            LatLng pos = new LatLng(state.getLatitude(), state.getLongitude());

            String etat = state.getState();
            String subscriber = state.getOperator();
            String imsi = state.getImsi();
            int trace = state.getTrace();
            boolean traced = false;


            if (trace == 0) {
                display(pos, etat, subscriber, imsi);
            }

            if (trace == 1) {
                if (oldpos == null) {
                    oldpos = pos;
                    oldEtat = etat;
                    oldSubscriber = subscriber;
                    oldImsi = imsi;
                    display(pos, etat, subscriber, imsi);
                } else {
                    int color;
                    if (oldEtat.equals("CONNECTED")) {
                        color = Color.GREEN;
                    } else if (oldEtat.equals("DISCONNECTED")) {
                        color = Color.RED;
                    } else {
                        color = Color.YELLOW;

                    }
                    mMap.addPolyline(new PolylineOptions()
                            .add(oldpos, pos)
                            .width(15)
                            .color(color));
                    if (!oldEtat.equals(etat)) {
                        display(oldpos, oldEtat, oldSubscriber, oldImsi);
                        display(pos, etat, subscriber, imsi);

                    }
                    oldpos = pos;
                    oldEtat = etat;
                }

            } else {
                if (oldpos != null) {
                    display(oldpos, oldEtat, oldSubscriber, oldImsi);
                }
                oldpos = null;
            }


        }

        ;
    }
}
