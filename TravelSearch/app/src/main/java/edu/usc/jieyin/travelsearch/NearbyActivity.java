package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private TextView emptyView;
    private JSONArray results;
    private int page = 0;
    private Button nextPage;
    private Button previous;
    private JSONArray pages [] = new JSONArray[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        nextPage = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
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

            if(jsonObject.has("next_page_token")){
                nextPage.setEnabled(true);
            }

            results = jsonObject.getJSONArray("results");
            viewSelector(results);

            pages[0] = results;

            mRecyclerView = (RecyclerView) findViewById(R.id.resultRecycler);
            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ResultAdapter(pages[0], new ResultAdapterListener() {

                @Override
                public void iconTextViewOnClick(View v, int position) {
                    Log.d("TEXT-CLICK","TEXT-CLICK FROM NEARY BY ACTIVITY");


                }

                @Override
                public void iconImageViewOnClick(View v, int position) {
                    ImageView heart = (ImageView) v.findViewById(R.id.heart);

                    if(Integer.valueOf(R.drawable.icon_heart_outline_black).equals(heart.getTag())){
                        heart.setImageResource(R.drawable.icon_heart_fill_red);
                        heart.setTag(Integer.valueOf(R.drawable.icon_heart_fill_red));
                        try {
                            Toast.makeText(getApplicationContext(),
                                    pages[page].getJSONObject(position).getString("name") + " was added to favorites",
                                    Toast.LENGTH_SHORT).show();
                            FavoriteFragment.favoriteItems.put(pages[page].getJSONObject(position));
                            FavoriteFragment.fAdapter.notifyDataSetChanged();
                            FavoriteFragment.viewSelector();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        heart.setImageResource(R.drawable.icon_heart_outline_black);
                        heart.setTag(Integer.valueOf(R.drawable.icon_heart_outline_black));
                        try {
                            Toast.makeText(getApplicationContext(),
                                    pages[page].getJSONObject(position).getString("name") + " was removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                            for(int i = 0; i < FavoriteFragment.favoriteItems.length(); i++){
                                if(FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id")
                                        .equals(pages[page].getJSONObject(position).getString("place_id"))){
                                    FavoriteFragment.favoriteItems.remove(i);
                                    FavoriteFragment.fAdapter.notifyDataSetChanged();
                                    FavoriteFragment.viewSelector();
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


            Log.d("results length", Integer.toString(pages[page].length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void viewSelector(JSONArray results){
        mRecyclerView = findViewById(R.id.resultRecycler);
        emptyView = findViewById(R.id.noRecord);
        RelativeLayout pagination = findViewById(R.id.pagination);
        if(results.length() == 0){
            mRecyclerView.setVisibility(View.GONE);
            pagination.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            pagination.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    View.OnClickListener previousListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(page != 0){
                previous.setEnabled(true);
            }
        }
    };
}
