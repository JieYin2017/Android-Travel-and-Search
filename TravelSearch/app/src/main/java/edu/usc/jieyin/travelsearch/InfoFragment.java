package edu.usc.jieyin.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class InfoFragment extends Fragment {
    private TextView address;
    private TextView phoneNumber;
    private TextView priceLevel;
    private RatingBar rating;
    private TextView googlePage;
    private TextView website;


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
        return view;
    }

    public void setInfo(JSONObject placeDetails){
        if(placeDetails.has("formatted_address")){
            try {
                address.setText(placeDetails.getString("formatted_address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
