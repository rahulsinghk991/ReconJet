package com.finalproject.crane.reconjet.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.finalproject.crane.reconjet.R;
import com.finalproject.crane.reconjet.dao.DbOperations;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Random;

public class RunActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    boolean flag = true;
    double pace = 0;
    double heartbeat = 60;
    double mile = 0;
    double speed=0;
    double avg_heartbeat;
    double avg_speed;
    double speedTotal;
    double heartbeatTotal;
    int counter = 0;
    TextView speed_tv;
    TextView avghb_tv;
    TextView mile_tv;
    String speedtxt = "speed:";
    String miletxt = "miles:";
    String heartbeattxt = "avg heartbeats:";
    String username;
    double lat;
    double lng;
    LatLng currentLoc;
    LatLng newloc;
    GoogleApiClient mGoogleApiClient;
    GoogleMap map;
    Location mLastLocation;
    PolylineOptions polylines;
    LocationRequest mLocationRequest;
    Boolean locationUpdateRequest;

    /** App Life Circle **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        speed_tv = (TextView) findViewById(R.id.run_speed);
        avghb_tv = (TextView) findViewById(R.id.run_hbs);
        mile_tv = (TextView) findViewById(R.id.run_miles);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // Show google map api
        locationUpdateRequest = true;
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();

    }//end of onCreate

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**Button onClickHandler**/
    public void startRunHandler(View view) {  //start button on click handler
        new Thread(new GetDataThread()).start();
    }

    public void stopRunHandler(View view) {  //stop button on click handler
        flag = false;   //stop getting data

        //Update Db in a thread
        new UpdateData().execute();
    }
    /** Threads **/
    Handler handler = new Handler() {   //thread handler
        public void handleMessage(Message message) {
            //TO DO
            if (message.what == 1) {
                speed_tv.setText(speedtxt + speed);
                avghb_tv.setText(heartbeattxt+heartbeat);
                String str_mile = String.format("%.2f",mile);
                mile_tv.setText(miletxt + str_mile);
            }
            super.handleMessage(message);
        }
    };



    public class GetDataThread implements Runnable { //get Data
        @Override
        public void run() {
            Random r = new Random();

            while (flag) {
                try {
                    Thread.sleep(5000);// get data every 5secs
                    counter++;
                    pace = r.nextInt(15 - 8) + pace;
                    speed = r.nextInt(20);
                    heartbeat = r.nextInt(60)+50;
                    mile += 0.1;
                    ArrayList<Double> doubleArray = new ArrayList<>();
                    doubleArray.add(speed);
                    doubleArray.add(mile);
                    doubleArray.add(pace);
                    doubleArray.add(heartbeat);
                    speedTotal += speed;
                    heartbeatTotal += heartbeat;
                    Message message = new Message();
                    message.what = 1;
                    //update data realtime
                    DbOperations dbOperations = new DbOperations();
                    dbOperations.updateRealTimeRun(doubleArray,username);
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }//end of thread

    public class UpdateData extends AsyncTask<String, Void, String> {//update after training data
        @Override
        protected String doInBackground(String... strings) {
            avg_heartbeat = heartbeatTotal/counter;
            avg_speed = speedTotal/counter;
            ArrayList<Double> doubleArray = new ArrayList<>();
            doubleArray.add(avg_speed);
            doubleArray.add(mile);
            doubleArray.add(pace);
            doubleArray.add(avg_heartbeat);
            DbOperations dbOperations = new DbOperations();
            dbOperations.updateRun(doubleArray, username);
            locationUpdateRequest = false;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //map.addMarker(new MarkerOptions().position(currentLoc).title("End Point"));
        }
    }




    /** implemented methods**/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        lat = mLastLocation.getLatitude();
        lng = mLastLocation.getLongitude();
        currentLoc = new LatLng(lat,lng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,14));
        polylines = new PolylineOptions().add(currentLoc);
        polylines.color(R.color.wallet_holo_blue_light);
        map.addMarker(new MarkerOptions().position(currentLoc).title("Start Point"));
        map.addPolyline(polylines);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        if (locationUpdateRequest) {
            startLocationUpdates();
            System.out.println("start update");
        }

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        //System.out.println(lat+","+lng);
        newloc = new LatLng(lat,lng);
        updateUI();
    }

    public void updateUI(){
        if(!newloc.equals(currentLoc)){
            map.addPolyline(polylines.add(newloc));
            currentLoc = newloc;
        }
    }
}