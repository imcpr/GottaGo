package com.palindromicstudios.gottago;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;


public class BathroomDescription extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_description);

        final Button rateUp, rateDown, addDescription;
        rateUp = (Button) findViewById(R.id.rate_up);
        rateDown = (Button) findViewById(R.id.rate_down);
        addDescription = (Button) findViewById(R.id.buttonAddDescription);

        addDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(BathroomDescription.this);

                alert.setTitle("Add a description");

                // Set an EditText view to get user input
                final EditText input = new EditText(BathroomDescription.this);
                alert.setView(input);

                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("description", value);
                        params.put("bathroom", ITEMS.GET(POSITION));
                        ParseCloud.callFunctionInBackground("addDescription", params, new FunctionCallback<Void>() {
                            @Override
                            public void done(Void v, ParseException e) {
                                addDescription.setClickable(false);
                                addDescription.setVisibility(View.GONE);
                                Toast.makeText(BathroomDescription.this, "Thanks for contributing.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bathroom_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
