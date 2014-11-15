package com.palindromicstudios.gottago;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainPage extends ActionBarActivity  {

    public final static String USER_LONG = "com.palindromicstudios.LONG";
    public final static String USER_LAT = "com.palindromicstudios.LAT";
    private boolean hasLocation = false;
    LocationManager mLocationManager;
    public static double aLong = 0.0;
    public static double aLat = 0.0;
    public static List<ParseObject> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        items = new ArrayList<ParseObject>();
//        items.add("Starbucks (Parc/Sherbrooke)");
//        items.add("Second Cup (Parc/Milton");
//        items.add("Schulich Music Building (Sherbrooke/Aylmer)");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("Tap to find the nearest one...");
        setSupportActionBar(toolbar);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location changed", "true");
                hasLocation = true;
                aLat = location.getLatitude();
                aLong = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 0, locationListener);
        }
        else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*60, 0, locationListener);
        }

        AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(500); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);

        ImageButton peeButton = (ImageButton) findViewById(R.id.peeButton);
        peeButton.startAnimation(blinkanimation);
        peeButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (hasLocation) {
                    items.clear();

                    ParseGeoPoint location= new ParseGeoPoint(aLong,aLat);
                    HashMap<String,Object> parameters = new HashMap<String,Object>();
                    parameters.put("location", location);
                    parameters.put("limit", 10);

                    ParseCloud.callFunctionInBackground("checkBathroom", parameters, new FunctionCallback<List<ParseObject>>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null) {
                                if (objects.size() > 0) {
                                    for (int i = 0; i < objects.size(); i++) {
                                        items.add(objects.get(i));
                                        Log.d("Name: ", objects.get(i).getString("bathroomName"));
                                    }
                                    Intent intent = new Intent(MainPage.this, BathroomList.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                                } else {
                                    Log.d("Size is 0", "");
                                }

                            } else {
                                Log.e("checkBathroom failed", e.toString());
                            }
                        }
                    });

                    //Intent intent = new Intent(MainPage.this, BathroomList.class);
                    //Intent intent = new Intent(MainPage.this, ConfirmActivity.class);
                    //intent.putExtra(USER_LONG, aLong);
                    //intent.putExtra(USER_LAT, aLat);
                    //startActivity(intent);
                }
                else {
                    Toast.makeText(MainPage.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                items.clear();
                ParseGeoPoint location= new ParseGeoPoint(aLong,aLat);
                HashMap<String,Object> parameters = new HashMap<String,Object>();
                parameters.put("location",location);
                parameters.put("limit", 5);
                mLocationManager.removeUpdates(locationListener);

                ParseCloud.callFunctionInBackground("checkBathroom", parameters, new FunctionCallback<List<ParseObject>>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {
                            if (objects.size() > 0) {
                                for (int i = 0; i < objects.size(); i++) {
                                    items.add(objects.get(i));
                                }
                                Intent intent = new Intent(MainPage.this, ConfirmActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainPage.this, AddBathroom.class);
                                startActivity(intent);
                            }
                            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                        } else {
                            Log.e("checkBathroom failed", e.toString());
                        }
                    }
                });
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_add)
        {
//            items.clear();
//            ParseGeoPoint location= new ParseGeoPoint(aLong,aLat);
//            HashMap<String,Object> parameters = new HashMap<String,Object>();
//            parameters.put("location",location);
//            parameters.put("limit", 5);
//
//            ParseCloud.callFunctionInBackground("checkBathroom", parameters, new FunctionCallback<List<ParseObject>>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//
//                    if (e == null) {
//                        if (objects.size() > 0) {
//                            for (int i = 0; i < objects.size(); i++) {
//                                items.add(objects.get(i));
//                            }
//                            Intent intent = new Intent(MainPage.this, ConfirmActivity.class);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(MainPage.this, AddBathroom.class);
//                            startActivity(intent);
//                        }
//
//                    } else {
//                        Log.e("checkBathroom failed", e.toString());
//                    }
//                }
//            });


        }
        return super.onOptionsItemSelected(item);
    }





}
