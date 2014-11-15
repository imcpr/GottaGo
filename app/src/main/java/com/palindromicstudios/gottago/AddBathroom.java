package com.palindromicstudios.gottago;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseGeoPoint;

import java.text.ParseException;
import java.util.HashMap;


public class AddBathroom extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);
        Button submitButton = (Button) findViewById(R.id.add_submit_button);
        submitButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseGeoPoint location= new ParseGeoPoint(MainPage.aLong,MainPage.aLat);
                HashMap<String,Object> parameters = new HashMap<String,Object>();
                EditText buildingName = (EditText) findViewById(R.id.buildingText);
                EditText addComments = (EditText) findViewById(R.id.addComments);
                parameters.put("bathroomName",buildingName.getText().toString());
                parameters.put("description",addComments.getText().toString());
                parameters.put("geoPoint",location);
                ParseCloud.callFunctionInBackground("addBathroom", parameters, new FunctionCallback() {
                    public void done(Object object, com.parse.ParseException e) {

                        if (e == null) {
                            Toast.makeText(AddBathroom.this, "New bathroom succesfully added!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("addBathroom failed", e.toString());
                        }
                    }
                });
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_bathroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
