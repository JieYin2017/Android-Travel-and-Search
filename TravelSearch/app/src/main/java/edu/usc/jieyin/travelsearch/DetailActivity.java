package edu.usc.jieyin.travelsearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.Uri;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private JSONObject placeDetails;
    private JSONArray yelpReview = new JSONArray();
    private JSONArray googleReview = new JSONArray();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private JSONObject placeJSON;

    private String placeID;
    private boolean isFavorite = false;
    private ImageView shareButton;
    private ImageView favoriteButton;
    private DetailActivity.ViewPagerAdapter adapter;
    private ProgressDialog progress;
    private RequestQueue queue;

    private String placeName;
    private String formattedAddress = "";
    private String website = "unavailable";


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
        viewPager.setOffscreenPageLimit(3);
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
            for (int i = 0; i < FavoriteFragment.favoriteItems.length(); i++) {
                if (FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id").equals(placeID)) {
                    isFavorite = true;
                    break;
                }
            }
            setToolBar();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress = new ProgressDialog(DetailActivity.this);
        progress.setMessage("Fetching details");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        fetchDetails();
    }

    private void setToolBar() {
        TextView placeNameView = findViewById(R.id.placeName);
        shareButton = findViewById(R.id.share);
        favoriteButton = findViewById(R.id.favoInfo);
        placeNameView.setText(placeName);
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.icon_heart_fill_white);
            favoriteButton.setTag(Integer.valueOf(R.drawable.icon_heart_fill_white));
            ;
        } else {
            favoriteButton.setTag(Integer.valueOf(R.drawable.icon_heart_outline_white));
            ;
        }
    }

    private void setShareFavorite() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    formattedAddress = placeDetails.getString("formatted_address");
                    website = placeDetails.getString("website");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    String twitterContent = "Check out " + placeName
                            + " locate at " + formattedAddress
                            + ". Website: " + website + "\n";

                    String twitterURL = null;
                    try {
                        twitterURL = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode(twitterContent, "UTF-8") + "&hashtags=TravelAndEntertainmentSearch";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Intent twitterIntent = new Intent(Intent.ACTION_VIEW);
                    twitterIntent.setData(Uri.parse(twitterURL));
                    startActivity(twitterIntent);
                }

            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(R.drawable.icon_heart_outline_white).equals(favoriteButton.getTag())) {
                    favoriteButton.setImageResource(R.drawable.icon_heart_fill_white);
                    favoriteButton.setTag(Integer.valueOf(R.drawable.icon_heart_fill_white));

                    Toast.makeText(getApplicationContext(),
                            placeName + " was added to favorites",
                            Toast.LENGTH_SHORT).show();
                    FavoriteFragment.favoriteItems.put(placeDetails);
                    FavoriteFragment.fAdapter.notifyDataSetChanged();
                    FavoriteFragment.viewSelector();

                } else {
                    favoriteButton.setImageResource(R.drawable.icon_heart_outline_white);
                    favoriteButton.setTag(R.drawable.icon_heart_outline_white);
                    try {
                        Toast.makeText(getApplicationContext(),
                                placeName + " was removed from favorites",
                                Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < FavoriteFragment.favoriteItems.length(); i++) {
                            if (FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id")
                                    .equals(placeDetails.getString("place_id"))) {
                                FavoriteFragment.favoriteItems.remove(i);
                                FavoriteFragment.fAdapter.notifyDataSetChanged();
                                FavoriteFragment.viewSelector();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    private void fetchDetails() {
        queue = Volley.newRequestQueue(getApplicationContext());
        String detailURL = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/details/?placeID=" + placeID;
        Log.d("placeDetails", detailURL);
        StringRequest nextPageRequest = new StringRequest(Request.Method.GET, detailURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject placeObj = new JSONObject(response);
                            placeDetails = placeObj.getJSONObject("result");
                            setShareFavorite();
                            googleReview = placeDetails.getJSONArray("reviews");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            fetchYelp(placeDetails);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("placeDetails", error.getMessage());
                        if (progress != null) {
                            progress.dismiss();
                        }
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

    private void fetchYelp(JSONObject currPlace) {
        JSONArray addressComponents;
        JSONObject addressItem;
        String type, formattedAdd, placeName, yelpURL;
        String city_name = "";
        String state_name = "";
        String country_name = "";
        String postal_code = "";
        try {

            formattedAdd = URLEncoder.encode(currPlace.getString("formatted_address"), "UTF-8");
            placeName = URLEncoder.encode(currPlace.getString("name"), "UTF-8");

            addressComponents = currPlace.getJSONArray("address_components");
            for (int i = 0; i < addressComponents.length(); i++) {
                addressItem = addressComponents.getJSONObject(i);
                type = addressItem.getJSONArray("types").getString(0);
                if (type.equals("locality")) {
                    city_name = URLEncoder.encode(addressItem.getString("long_name"), "UTF-8");
                }
                if (type.equals("administrative_area_level_1")) {
                    state_name = URLEncoder.encode(addressItem.getString("short_name"), "UTF-8");
                }
                if (type.equals("country")) {
                    country_name = URLEncoder.encode(addressItem.getString("short_name"), "UTF-8");
                }
                if (type.equals("postal_code")) {
                    postal_code = addressItem.getString("long_name");
                }
            }
            yelpURL = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/yelp?name=" + placeName + "&city=" + city_name +
                    "&state=" + state_name + "&country=" + country_name + "&address1=" + formattedAdd + "&postal_code=" + postal_code;
            Log.d("yelpURL", yelpURL);


            StringRequest yelpRequest = new StringRequest(Request.Method.GET, yelpURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("yelpResult", "succssfully");
                            if (!response.equals("failed")) {
                                try {
                                    yelpReview = new JSONObject(response).getJSONArray("reviews");
                                    Log.d("yelpResult", "response length: " + yelpReview.length());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            MapFragment mapFrag = (MapFragment) adapter.getItem(2);
                            mapFrag.setDestLoc(placeDetails);
                            ReviewFragment reviewFrag = (ReviewFragment) adapter.getItem(3);
                            reviewFrag.getReviews(googleReview, yelpReview);
                            Log.d("ReviewLength", "GoogleReview: " + googleReview.length() + " yelpReview: " + yelpReview.length());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("placeDetails", error.getMessage());
                            if (progress != null) {
                                progress.dismiss();
                            }
                        }
                    });
            queue.add(yelpRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            InfoFragment infoFrag = (InfoFragment) adapter.getItem(0);
            infoFrag.setInfo(placeDetails);
            PhotoFragment photoFrag = (PhotoFragment) adapter.getItem(1);
            photoFrag.setPhoto(placeID);
            if (progress != null) {
                progress.dismiss();
            }
        }

    }
}

