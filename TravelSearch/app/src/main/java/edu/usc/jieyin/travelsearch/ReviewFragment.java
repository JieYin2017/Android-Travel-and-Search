package edu.usc.jieyin.travelsearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewFragment extends Fragment {
    private TextView noReview;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray googleReview = new JSONArray();
    private JSONArray yelpReview = new JSONArray();
    private JSONArray reviewToUpdate = new JSONArray();
    private boolean isYelp = false;

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

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReviewAdapter(reviewToUpdate, new ReviewAdapter.ReviewAdapterListener(){
            @Override
            public void iconTextViewOnClick(View v, int position) {
                RelativeLayout reviewBar = v.findViewById(R.id.reviewDetail);
            }
        });
        mRecyclerView.setAdapter(mAdapter);


        // Inflate the layout for this fragment
        return view;
    }

    public void getReviews(JSONArray googleReview, JSONArray yelpReview){
        this.googleReview = googleReview;
        this.yelpReview = yelpReview;
        setReviews();
    }

    private void setReviews(){
        if(!isYelp){
            if (googleReview.length() == 0){
                noReview.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }else{
                noReview.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                for(int i = 0; i < googleReview.length(); i++){
                    try {
                        reviewToUpdate.put(googleReview.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }

    }

}
