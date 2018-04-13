package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class NearbyActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray results;

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
            results = jsonObject.getJSONArray("results");
            mRecyclerView = (RecyclerView) findViewById(R.id.resultRecycler);
            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ResultAdapter(results, new ResultAdapterListener() {

                @Override
                public void iconTextViewOnClick(View v, int position) {


                }

                @Override
                public void iconImageViewOnClick(View v, int position) {
                    ImageView heart = (ImageView) v.findViewById(R.id.heart);

                    if(Integer.valueOf(R.drawable.icon_heart_outline_black).equals(heart.getTag())){
                        heart.setImageResource(R.drawable.icon_heart_fill_red);
                        heart.setTag(Integer.valueOf(R.drawable.icon_heart_fill_red));
                        try {
                            Toast.makeText(getApplicationContext(),
                                    results.getJSONObject(position).getString("name") + " was added to favorites",
                                    Toast.LENGTH_SHORT).show();
                            FavoriteFragment.favoriteItems.put(results.getJSONObject(position));
                            FavoriteFragment.mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        heart.setImageResource(R.drawable.icon_heart_outline_black);
                        heart.setTag(Integer.valueOf(R.drawable.icon_heart_outline_black));
                        try {
                            Toast.makeText(getApplicationContext(),
                                    results.getJSONObject(position).getString("name") + " was removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                            for(int i = 0; i < FavoriteFragment.favoriteItems.length(); i++){
                                if(FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id")
                                        .equals(results.getJSONObject(position).getString("place_id"))){
                                    FavoriteFragment.favoriteItems.remove(i);
                                    FavoriteFragment.mAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("LENGTH OF FAVORITE --", "" + FavoriteFragment.favoriteItems.length());
                }
            });
            mRecyclerView.setAdapter(mAdapter);


            Log.d("results length", Integer.toString(results.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
