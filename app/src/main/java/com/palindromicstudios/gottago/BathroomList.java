package com.palindromicstudios.gottago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class BathroomList extends Activity {

    private double aLong;
    private double aLat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_list);
        Intent intent = getIntent();
        aLong = intent.getDoubleExtra(MainPage.USER_LONG, 0.0);
        aLat = intent.getDoubleExtra(MainPage.USER_LAT, 0.0);
        TextView myText = (TextView) findViewById(R.id.list_text);
        myText.setText("aLong" + String.valueOf(aLong) + "aLat " + String.valueOf(aLat));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bathroom_list, menu);
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
