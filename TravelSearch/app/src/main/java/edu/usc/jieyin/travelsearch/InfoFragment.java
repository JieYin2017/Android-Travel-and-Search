package edu.usc.jieyin.travelsearch;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class InfoFragment extends Fragment {
    private TextView address;
    private TextView phoneNumber;
    private TextView priceLevel;
    private RatingBar rating;
    private TextView googlePage;
    private TextView website;
    private TableRow addressRow;
    private TableRow phoneRow;
    private TableRow priceRow;
    private TableRow googleRow;
    private TableRow ratingRow;
    private TableRow websiteRow;
    private String phoneNumberString;
    private String googleString;
    private String websiteString;



    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        address = view.findViewById(R.id.address);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        priceLevel = view.findViewById(R.id.priceLevel);
        rating = view.findViewById(R.id.rating);
        googlePage = view.findViewById(R.id.googlePage);
        website = view.findViewById(R.id.website);
        addressRow = view.findViewById(R.id.addressRow);
        phoneRow= view.findViewById(R.id.phoneRow);
        priceRow= view.findViewById(R.id.priceRow);
        googleRow= view.findViewById(R.id.googleRow);
        ratingRow= view.findViewById(R.id.ratingRow);
        websiteRow= view.findViewById(R.id.websiteRow);

        phoneNumber.setOnClickListener(callPhone);
        googlePage.setOnClickListener(openGoogle);
        website.setOnClickListener(openWebsite);

        phoneNumber.setPaintFlags(phoneNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        googlePage.setPaintFlags(googlePage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        website.setPaintFlags(website.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    public void setInfo(JSONObject placeDetails){
        if(placeDetails.has("formatted_address")){
            addressRow.setVisibility(View.VISIBLE);
            try {
                address.setText(placeDetails.getString("formatted_address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            addressRow.setVisibility(View.GONE);
        }
        if(placeDetails.has("formatted_phone_number")){
            phoneRow.setVisibility(View.VISIBLE);
            try {
                phoneNumberString = placeDetails.getString("formatted_phone_number");
                phoneNumber.setText(phoneNumberString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            phoneRow.setVisibility(View.GONE);
        }
        if(placeDetails.has("price_level")){
            try {
                priceRow.setVisibility(View.VISIBLE);
                String priceLevelSymbol = "$";
                for (int i = 1; i < placeDetails.getInt("price_level"); i++) {
                    priceLevelSymbol += "$";
                }
                priceLevel.setText(priceLevelSymbol);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            priceRow.setVisibility(View.GONE);
        }
        if(placeDetails.has("rating")){
            ratingRow.setVisibility(View.VISIBLE);
            try {
                rating.setRating((float) placeDetails.getDouble("rating"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            ratingRow.setVisibility(View.GONE);
        }
        if(placeDetails.has("url")){
            googleRow.setVisibility(View.VISIBLE);
            try {
                googleString = placeDetails.getString("url");
                googlePage.setText(googleString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            googleRow.setVisibility(View.GONE);
        }
        if(placeDetails.has("website")){
            websiteRow.setVisibility(View.VISIBLE);
            try {
                websiteString = placeDetails.getString("website");
                website.setText(websiteString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            websiteRow.setVisibility(View.GONE);
        }

    }

    View.OnClickListener callPhone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumberString, null));
            startActivity(intent);
        }
    };

    View.OnClickListener openGoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(googleString); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    View.OnClickListener openWebsite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse(websiteString); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

}
