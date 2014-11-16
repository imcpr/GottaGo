package com.palindromicstudios.gottago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;


public class AddBathroom extends ActionBarActivity {
    //    ParseFile ImgFile;
    private byte[] image;
    final int SELECT_PHOTO= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);



        Button addImage = (Button) findViewById(R.id.addImageButton);
        addImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                Intent i;
//                i.setType("image/*");
                startActivityForResult(i, SELECT_PHOTO);

            }
        });
        Button submitButton = (Button) findViewById(R.id.add_submit_button);
        submitButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText buildingName = (EditText) findViewById(R.id.buildingText);
                EditText addComments = (EditText) findViewById(R.id.addComments);
                if (buildingName.getText().toString().isEmpty()) {
                    Toast.makeText(AddBathroom.this, "Don't forget to enter a bathroom name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseGeoPoint location= new ParseGeoPoint(MainPage.aLat,MainPage.aLong);
                HashMap<String,Object> parameters = new HashMap<String,Object>();
                ParseFile ImgFile = null;
                if (image!=null) {
                    ImgFile = new ParseFile(buildingName.getText().toString(), image);
                }
                try {
                    if( ImgFile != null )
                        ImgFile.save();
                    parameters.put("bathroomName",buildingName.getText().toString());
                    parameters.put("description",addComments.getText().toString());
                    parameters.put("geoPoint",location);
                    parameters.put("ImgFile",ImgFile);
                    ParseObject bathroom = new ParseObject("Bathroom");
                    ParseRelation<ParseObject> relation = bathroom.getRelation("description");
                    bathroom.put("bathroomName",buildingName.getText().toString());
                    ParseObject review = new ParseObject("Review");
                    review.put("content", addComments.getText().toString());
                    review.save();
                    relation.add(review);
                    bathroom.put("geoPoint", location);
                    if(ImgFile!=null) {
                        bathroom.put("ImgFile", ImgFile);
                    }
                    bathroom.save();
//                    ParseCloud.callFunctionInBackground("addBathroom", parameters, new FunctionCallback() {
//                        public void done(Object object,com.parse.ParseException e) {
//
//                            if (e == null) {
//                                Toast.makeText(AddBathroom.this, "New bathroom successfully added!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.e("addBathroom failed", e.toString());
//                            }
//                        }
//                    });
                    Toast.makeText(AddBathroom.this, "Location submitted", Toast.LENGTH_SHORT).show();
                    Intent b = new Intent(AddBathroom.this,MainPage.class);
                    b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(b);
                    AddBathroom.this.finish();
                } catch (Exception e) {
                    Log.e("image save error", e.toString());
                }

            }

        });
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
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        image = stream.toByteArray();

                    } catch (Exception e) {
                        Log.e("AddBathroom class image error", e.toString());
                    }

                }
        }
    }
}