package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        //Initialize toolbar
        Toolbar mToolbar = (Toolbar)findViewById(R.id.resultToolBar);
        mToolbar.setTitle("Search results");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getJSON
        Intent intent = getIntent();
        String nearbyString = intent.getStringExtra("PlaceNearby");
        try {
            JSONObject jsonObject = new JSONObject(nearbyString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            Log.d("results length", Integer.toString(jsonArray.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
