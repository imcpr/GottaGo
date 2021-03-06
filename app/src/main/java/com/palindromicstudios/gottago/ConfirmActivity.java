package com.palindromicstudios.gottago;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfirmActivity extends Activity {

    private RecyclerView confirmList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        confirmList = (RecyclerView) findViewById(R.id.confirmListView);
        confirmList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        confirmList.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(MainPage.items);
        confirmList.setAdapter(mAdapter);

        Button noButton = (Button) findViewById(R.id.buttonDeclineAdd);
        noButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                Log.v("backpressed", "gobackkk");
            }
        });

        Button yesButton = (Button) findViewById(R.id.buttonConfirmAdd);
        yesButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ConfirmActivity.this, AddBathroom.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
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
            String str = object.getString("bathroomName");
            ParseGeoPoint geoPoint = object.getParseGeoPoint("geoPoint");
            ParseGeoPoint location = new ParseGeoPoint(MainPage.aLat, MainPage.aLong);
            double distance = geoPoint.distanceInKilometersTo(location)*1000;
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(0);
            holder.text.setText(str);
            holder.distance.setText(format.format(distance) + "m");
            setAnimation(holder.container, position);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item_layout_with_distance, viewGroup, false);
            itemView.setOnClickListener(mOnClickListener);
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
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = confirmList.getChildPosition(v);
            //String item = MainPage.items.get(position).getString("bathroomName");
            Intent intent = new Intent(ConfirmActivity.this, BathroomDescription.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    };


    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView text, distance;
        private CardView container;
        public MyHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text1);
            distance = (TextView) v.findViewById(R.id.text2);
            container = (CardView) v.findViewById(R.id.card_view);
        }
    }

}
