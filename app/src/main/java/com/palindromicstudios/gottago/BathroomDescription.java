package com.palindromicstudios.gottago;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;


public class BathroomDescription extends ActionBarActivity {
    private int position;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private byte[] image;
    final int SELECT_PHOTO= 1;
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
                        if (value.isEmpty()) {
                            Toast.makeText(BathroomDescription.this, "Whoops! Don't forget to enter your comment.", Toast.LENGTH_SHORT).show();
                            return;
                        }
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

        ParseFile image = MainPage.items.get(position).getParseFile("ImgFile");
        ImageButton addImageView = (ImageButton) findViewById(R.id.addImageButton);
        ParseImageView parseImageView = (ParseImageView) findViewById(R.id.parseImageView);
        addImageView.setVisibility(View.GONE);
        if (image != null) {
            parseImageView.setVisibility(View.VISIBLE);
            parseImageView.setParseFile(image);
            parseImageView.loadInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    Log.v("load image success", "loaded shit");
                    if (e != null) Log.e("Image error", e.toString());
                }

            });
        }
        else {
            parseImageView.setVisibility(View.GONE);
            addImageView.setVisibility(View.VISIBLE);
            addImageView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_PHOTO);
                }

            });



            parseImageView.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bathroom_description, menu);
        return true;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        Log.d("INTENT DATA", imageReturnedIntent.getData().toString());
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap selectedImg = BitmapFactory.decodeStream(imageStream);
                        imageStream.close();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImg.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                        image = stream.toByteArray();
                        stream.close();
                        ParseFile ImgFile = null;
                        if(image!=null){
                            ImgFile = new ParseFile(image);
                            try {
                                MainPage.items.get(position).put("ImgFile", ImgFile);
                                MainPage.items.get(position).save();
                            }
                            catch(Exception e){
                                //Toast.makeText(BathroomDescription.this,e.toString(),Toast.LENGTH_LONG).show();

                            }
                            Intent intent = new Intent(BathroomDescription.this, BathroomDescription.class);

                            intent.putExtra("position", position);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(BathroomDescription.this, "Image submission failed.", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        Log.e("AddBathroom class image error", e.toString());
                    }

                }
        }
    }
}
