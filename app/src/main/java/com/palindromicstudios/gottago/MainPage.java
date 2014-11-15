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
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class MainPage extends ActionBarActivity implements LocationListener {

    public final static String USER_LONG = "com.palindromicstudios.LONG";
    public final static String USER_LAT = "com.palindromicstudios.LAT";
    private boolean hasLocation = false;
    LocationManager mLocationManager;
    private double aLong = 0.0;
    private double aLat = 0.0;
    public static List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        items = new ArrayList<String>();
        items.add("Starbucks (Parc/Sherbrooke)");
        items.add("Second Cup (Parc/Milton");
        items.add("Schulich Music Building (Sherbrooke/Aylmer)");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        ImageButton peeButton = (ImageButton) findViewById(R.id.peeButton);
        peeButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (hasLocation || 0 == 0) {
                    mLocationManager.removeUpdates(MainPage.this);
                    Intent intent = new Intent(MainPage.this, BathroomList.class);
                    //Intent intent = new Intent(MainPage.this, ConfirmActivity.class);
                    intent.putExtra(USER_LONG, aLong);
                    intent.putExtra(USER_LAT, aLat);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
                else {
                    Toast.makeText(MainPage.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
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
        if (id == R.id.action_add)
        {
            ParseGeoPoint location= new ParseGeoPoint(aLong,aLat);
            HashMap<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("location",location);

            ParseCloud.callFunctionInBackground("checkBathroom", parameters, new FunctionCallback<List<Object>>() {
                public void done(List<Object> objects, ParseException e) {

                    if (e == null) {
                        if (objects.size() > 0) {
                            for (int i = 0; i < objects.size(); i++) {
                                items.add(String.valueOf(objects.get(i)));
                            }
                            Intent intent = new Intent(MainPage.this, ConfirmActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainPage.this, AddBathroom.class);
                            startActivity(intent);
                        }

                    } else {
                        Log.e("checkBathroom failed", "checkBathroom failed");
                    }
                }
            });

            Intent intent = new Intent(this, AddBathroom.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i("Location Changed", location.getLatitude() + " and " + location.getLongitude());
//            mLocationManager.removeUpdates(this);
            aLong = location.getLongitude();
            aLat = location.getLatitude();
            hasLocation = true;
        }
    }

    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {}
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("Location changed", "true");
            hasLocation = true;
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


}
