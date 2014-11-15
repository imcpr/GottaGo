package com.palindromicstudios.gottago;

import android.app.Activity;
import android.graphics.Color;
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
import android.widget.TextView;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public class BathroomList extends ActionBarActivity {

    private double aLong;
    private double aLat;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.bathroom_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            mAdapter = new MyAdapter(MainPage.items);
            mRecyclerView.setAdapter(mAdapter);
        } catch (NullPointerException e) {
            Log.e("BULLSHIT", e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
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

    public class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        private List<String> items;
        private int lastPosition = -1;

        public MyAdapter(List<String> items) {
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            String str = items.get(position);
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
