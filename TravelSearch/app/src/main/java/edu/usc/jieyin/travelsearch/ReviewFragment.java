package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ReviewFragment extends Fragment {
    private TextView noReview;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray googleReview = new JSONArray();
    private JSONArray yelpReview = new JSONArray();

    private JSONArray grToSort = new JSONArray();
    private JSONArray yrToSort = new JSONArray();;
    private ArrayList<JSONObject> sortContainer = new ArrayList<>();

    private JSONArray reviewToUpdate = new JSONArray();
    private boolean isYelp = false;
    private boolean isDefault = true;
    private Spinner sourceSpinner;
    private Spinner orderSpinner;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        noReview = view.findViewById(R.id.noReview);
        mRecyclerView = view.findViewById(R.id.reviewRecycle);

        sourceSpinner = view.findViewById(R.id.sourceSpinner);
        orderSpinner = view.findViewById(R.id.orderSpinner);

        setSourceSpinner();
        setOrderSpinner();

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter tempAdapter = new ReviewAdapter(reviewToUpdate, new ReviewAdapter.ReviewAdapterListener() {
            @Override
            public void iconTextViewOnClick(View v, int position) {
                Log.d("tempAdapter", "Initialization");

            }
        });
        mRecyclerView.setAdapter(mAdapter);


        // Inflate the layout for this fragment
        return view;
    }

    public void getReviews(final JSONArray googleReview, final JSONArray yelpReview) {

        this.googleReview = googleReview;
        this.yelpReview = yelpReview;

        setReviews();
    }

    private void setReviews() {
        clearReview();
        if (isDefault) {
            if (!isYelp) {
                if (googleReview.length() == 0) {
                    noReview.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    noReview.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < googleReview.length(); i++) {
                        try {
                            reviewToUpdate.put(googleReview.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //mAdapter.notifyDataSetChanged();
                    updateAdapter();
                }
            } else {
                if (yelpReview.length() == 0) {
                    noReview.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    noReview.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < yelpReview.length(); i++) {
                        try {
                            reviewToUpdate.put(yelpReview.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //mAdapter.notifyDataSetChanged();
                    updateAdapter();
                }
            }
        } else {
            if (!isYelp) {
                if (grToSort.length() == 0) {
                    noReview.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    noReview.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < grToSort.length(); i++) {
                        try {
                            reviewToUpdate.put(grToSort.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    updateAdapter();
                    //mAdapter.notifyDataSetChanged();
                }
            } else {
                if (yrToSort.length() == 0) {
                    noReview.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    noReview.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < yrToSort.length(); i++) {
                        try {
                            reviewToUpdate.put(yrToSort.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //mAdapter.notifyDataSetChanged();
                    updateAdapter();
                }
            }
        }

    }

    private void clearReview() {
        while (reviewToUpdate.length() != 0) {
            reviewToUpdate.remove(0);
        }
    }

    private void setSourceSpinner() {
        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sourceString = sourceSpinner.getSelectedItem().toString();
                Log.d("SpinnerEvent", sourceString);
                if (sourceString.equals("Google reviews")) {
                    isYelp = false;
                    setReviews();
                } else {
                    isYelp = true;
                    setReviews();
                }
                orderSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });
    }

    private void setOrderSpinner() {
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String orderString = orderSpinner.getSelectedItem().toString();
                Log.d("SpinnerEvent", orderString);
                clearSort();
                switch (orderString) {
                    case "Default order":
                        isDefault = true;
                        setReviews();
                        break;
                    case "Highest rating":
                        isDefault = false;
                        if (!isYelp) {
                            sortByRatingH(googleReview);
                        } else {
                            sortByRatingH(yelpReview);
                        }
                        setReviews();
                        break;
                    case "Lowest rating":
                        isDefault = false;
                        if (!isYelp) {
                            sortByRatingL(googleReview);
                        } else {
                            sortByRatingL(yelpReview);
                        }
                        setReviews();
                        break;
                    case "Most recent":
                        isDefault = false;
                        if (!isYelp) {
                            sortByTimeH(googleReview);
                        } else {
                            sortByTimeH(yelpReview);
                        }
                        setReviews();
                        break;
                    case "Least recent":
                        isDefault = false;
                        if (!isYelp) {
                            sortByTimeL(googleReview);
                        } else {
                            sortByTimeL(yelpReview);
                        }
                        setReviews();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });

    }

    private void sortByRatingH(JSONArray toSort) {
        for (int i = 0; i < toSort.length(); i++) {
            try {
                sortContainer.add(toSort.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(sortContainer, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int compareResult = 0;
                try {
                    compareResult = o2.getInt("rating") - o1.getInt("rating");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    return compareResult;
                }
            }
        });

        if(isYelp){
            for (int j = 0; j < sortContainer.size(); j++) {
                yrToSort.put(sortContainer.get(j));
            }
        }else{
            for (int j = 0; j < sortContainer.size(); j++) {
                grToSort.put(sortContainer.get(j));
            }
        }

    }

    private void sortByRatingL(JSONArray toSort) {
        for (int i = 0; i < toSort.length(); i++) {
            try {
                sortContainer.add(toSort.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(sortContainer, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int compareResult = 0;
                try {
                    compareResult = o1.getInt("rating") - o2.getInt("rating");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    return compareResult;
                }
            }
        });

        if(isYelp){
            for (int j = 0; j < sortContainer.size(); j++) {
                yrToSort.put(sortContainer.get(j));
            }
        }else{
            for (int j = 0; j < sortContainer.size(); j++) {
                grToSort.put(sortContainer.get(j));
            }
        }
    }

    private void sortByTimeH(JSONArray toSort) {
        for (int i = 0; i < toSort.length(); i++) {
            try {
                sortContainer.add(toSort.getJSONObject(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(sortContainer, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int d1ms = 0;
                int d2ms = 0;
                if (o1.has("time_created")) {
                    try {
                        String timeString1 = o1.getString("time_created");
                        String timeString2 = o2.getString("time_created");
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d1 = f.parse(timeString1);
                        Date d2 = f.parse(timeString2);
                        d1ms = (int) (d1.getTime() / 1000);
                        d2ms = (int) (d2.getTime() / 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        return d2ms - d1ms;
                    }

                } else {
                    try {
                        d1ms = o1.getInt("time");
                        d2ms = o2.getInt("time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        return d2ms - d1ms;
                    }

                }
            }
        });

        if(isYelp){
            for (int j = 0; j < sortContainer.size(); j++) {
                yrToSort.put(sortContainer.get(j));
            }
        }else{
            for (int j = 0; j < sortContainer.size(); j++) {
                grToSort.put(sortContainer.get(j));
            }
        }
    }

    private void sortByTimeL(JSONArray toSort) {

        for (int i = 0; i < toSort.length(); i++) {
            try {
                sortContainer.add(toSort.getJSONObject(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(sortContainer, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int d1ms = 0;
                int d2ms = 0;
                if (o1.has("time_created")) {
                    try {
                        String timeString1 = o1.getString("time_created");
                        String timeString2 = o2.getString("time_created");
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d1 = f.parse(timeString1);
                        Date d2 = f.parse(timeString2);
                        d1ms = (int) (d1.getTime() / 1000);
                        d2ms = (int) (d2.getTime() / 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        return d1ms - d2ms;
                    }

                } else {
                    try {
                        d1ms = o1.getInt("time");
                        d2ms = o2.getInt("time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        return d1ms - d2ms;
                    }

                }
            }
        });

        if(isYelp){
            for (int j = 0; j < sortContainer.size(); j++) {
                yrToSort.put(sortContainer.get(j));
            }
        }else{
            for (int j = 0; j < sortContainer.size(); j++) {
                grToSort.put(sortContainer.get(j));
            }
        }
    }

    private void clearSort() {
        sortContainer.clear();
        while (yrToSort.length() != 0) {
            yrToSort.remove(0);
        }
        while (grToSort.length() != 0) {
            grToSort.remove(0);
        }
    }

    private void updateAdapter(){
        mAdapter = new ReviewAdapter(reviewToUpdate, new ReviewAdapter.ReviewAdapterListener() {
            @Override
            public void iconTextViewOnClick(View v, int position) {
                String reviewURL = "";
                try {
                    if (isYelp) {
                        reviewURL = reviewToUpdate.getJSONObject(position).getString("url");
                    }
                    else{
                        reviewURL = reviewToUpdate.getJSONObject(position).getString("author_url");
                    }}catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent openReivew = new Intent(Intent.ACTION_VIEW);
                openReivew.setData(Uri.parse(reviewURL));
                startActivity(openReivew);
            }
        });
        mRecyclerView.swapAdapter(mAdapter,false);
    }

}
