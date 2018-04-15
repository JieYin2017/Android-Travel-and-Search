package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.icu.text.IDNA;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    public static JSONObject placeDetails;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private JSONObject placeJSON;
    private String placeName;
    private String placeID;
    private boolean isFavorite = false;
    private ImageView shareButton;
    private ImageView favoriteButton;
    private DetailActivity.ViewPagerAdapter adapter;
    private int[] tabIcons = {
            R.drawable.icon_info_outline,
            R.drawable.icon_photo,
            R.drawable.icon_maps,
            R.drawable.icon_review
    };
    private String[] tabTitles = {
            "INFO",
            "PHOTOS",
            "MAPS",
            "REVIEWS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        addTabs(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        Intent intent = getIntent();
        String placeString = intent.getStringExtra("PlaceDetail");
        try {
            placeJSON = new JSONObject(placeString);
            placeName = placeJSON.getString("name");
            placeID = placeJSON.getString("place_id");
            for(int i = 0; i < FavoriteFragment.favoriteItems.length(); i++){
                if(FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id").equals(placeID)){
                    isFavorite = true;
                    break;
                }
            }
            setToolBar();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fetchDetails();
    }

    private void setToolBar(){
        TextView placeNameView = findViewById(R.id.placeName);
        shareButton = findViewById(R.id.share);
        favoriteButton = findViewById(R.id.favoInfo);
        placeNameView.setText(placeName);
        if(isFavorite){
            favoriteButton.setImageResource(R.drawable.icon_heart_fill_white);
        }
    }

    private void fetchDetails(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String detailURL = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/details/?placeID=" + placeID;
        Log.d("placeDetails",detailURL);
        StringRequest nextPageRequest = new StringRequest(Request.Method.GET, detailURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject placeObj = new JSONObject(response);
                            placeDetails = placeObj.getJSONObject("result");
                            InfoFragment infoFrag = (InfoFragment) adapter.getItem(0);
                            infoFrag.setInfo(placeDetails);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("placeDetails",error.getMessage());
                    }
                });
        queue.add(nextPageRequest);
    }

    private void setupTabIcons() {
        //tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        //tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        for (int i = 0; i < 4; i++) {
            LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
            tabContent.setText(" " + tabTitles[i]);
            tabContent.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i], 0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(tabContent);
        }


    }

    private void addTabs(ViewPager viewPager) {
        adapter = new DetailActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new InfoFragment(), tabTitles[0]);
        adapter.addFrag(new PhotoFragment(), tabTitles[1]);
        adapter.addFrag(new MapFragment(), tabTitles[2]);
        adapter.addFrag(new ReviewFragment(), tabTitles[3]);
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

