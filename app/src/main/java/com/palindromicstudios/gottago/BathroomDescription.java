package com.palindromicstudios.gottago;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;


public class BathroomDescription extends ActionBarActivity {
    private int position;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_description);


        position = getIntent().getIntExtra("position", -1);
        if (position == -1) {
            finish();
            Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle(MainPage.items.get(position).getString("bathroomName"));
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.descriptionListView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ImageButton mapBtn = (ImageButton) findViewById(R.id.launch_map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                ParseGeoPoint geoPoint = MainPage.items.get(position).getParseGeoPoint("geoPoint");
                intent.setData(Uri.parse("geo:" + geoPoint.getLatitude() + "," + geoPoint.getLongitude() + "?q=" +
                geoPoint.getLatitude() + "," + geoPoint.getLongitude() + "(" + MainPage.items.get(position).getString("bathroomName") + ")"));
                startActivity(intent);
            }
        });

        final ImageButton rateUp, rateDown;
        final Button addDescription;
        rateUp = (ImageButton) findViewById(R.id.rate_up);
        rateDown = (ImageButton) findViewById(R.id.rate_down);
        rating = (TextView) findViewById(R.id.rating);
        rating.setText(String.valueOf(MainPage.items.get(position).getInt("rating")));
        addDescription = (Button) findViewById(R.id.buttonAddDescription);

        ParseRelation relation = MainPage.items.get(position).getRelation("description");
        ParseQuery<ParseObject> query = relation.getQuery();
        query.orderByDescending("createdAt"); //most recent reviews first
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> reviews, ParseException e) {
                if (e == null) {
                    mAdapter = new MyAdapter(reviews);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    //error
                }
            }

        });

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
                        ParseRelation relation = MainPage.items.get(position).getRelation("description");
                        ParseObject newReview = new ParseObject("Review");
                        newReview.put("content", value); //input is the text that the user entered
                        try {
                            newReview.save();
                            relation.add(newReview);
                            MainPage.items.get(position).saveInBackground();
                            Toast.makeText(BathroomDescription.this, "Thanks for contributing.", Toast.LENGTH_SHORT).show();
                            ParseQuery<ParseObject> query = relation.getQuery();
                            query.orderByDescending("createdAt"); //most recent reviews first
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> reviews, ParseException e) {
                                    if (e == null) {
                                        mAdapter = new MyAdapter(reviews);
                                        mRecyclerView.setAdapter(mAdapter);
                                    } else {
                                        //error
                                    }
                                }

                            });
                        } catch (Exception e) {
                            Log.e("Error", e.toString());
                        }
                        //HashMap<String, Object> params = new HashMap<String, Object>();
                        //params.put("description", value);
                        //params.put("bathroom", MainPage.items.get(position));
//                        ParseCloud.callFunctionInBackground("addDescription", params, new FunctionCallback<Void>() {
//                            @Override
//                            public void done(Void v, ParseException e) {
//                                addDescription.setClickable(false);
//                                addDescription.setVisibility(View.GONE);
//                                Toast.makeText(BathroomDescription.this, "Thanks for contributing.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
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

        rateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPage.items.get(position).increment("rating");
                MainPage.items.get(position).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        rating.setText(String.valueOf(MainPage.items.get(position).getInt("rating")));
                    }
                });
                rateUp.setClickable(false);
                rateDown.setClickable(false);
                rateUp.setVisibility(View.INVISIBLE);
                rateDown.setVisibility(View.INVISIBLE);

            }
        });

        rateDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPage.items.get(position).increment("rating", -1);
                MainPage.items.get(position).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        rating.setText(String.valueOf(MainPage.items.get(position).getInt("rating")));
                    }
                });
                rateUp.setClickable(false);
                rateDown.setClickable(false);
                rateUp.setVisibility(View.INVISIBLE);
                rateDown.setVisibility(View.INVISIBLE);
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

    public class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        private List<ParseObject> items;
        private int lastPosition = -1;

        public MyAdapter(List<ParseObject> items) {
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            ParseObject object = items.get(position);
            String str = object.getString("content");
            holder.text.setText(str);
            setAnimation(holder.container, position);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_layout, viewGroup, false);
            return new MyHolder(itemView);
        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private CardView container;
        public MyHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text1);
            container = (CardView) v.findViewById(R.id.card_view);
        }
    }
}
