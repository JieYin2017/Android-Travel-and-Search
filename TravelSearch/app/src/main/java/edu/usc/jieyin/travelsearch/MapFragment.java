package edu.usc.jieyin.travelsearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment {

    private AutoCompleteTextView fromAutoComplete;
    private Spinner travelModeSpinner;
    private SupportMapFragment mapFragment;
    private double fromLat, fromLon, destLat, destLon;
    private String placeName;
    private GoogleMap mMap;



    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        travelModeSpinner = view.findViewById(R.id.travelModeSpinner);
        fromAutoComplete = view.findViewById(R.id.fromAutoComplete);
        setAutoComplete();

        // Inflate the layout for this fragment
        return view;
    }

    public void setDestLoc(JSONObject placeDetails) {
        try {
            placeName = placeDetails.getString("name");
            destLat = placeDetails.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            destLon = placeDetails.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMyLocationEnabled(true);
                    LatLng dest = new LatLng(destLat, destLon);
                    mMap.addMarker(new MarkerOptions().position(dest)
                            .title(placeName)).showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest,15.0f));
                }
            });
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
            e.printStackTrace();
        }

    }

    private void setAutoComplete(){
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getContext());
        AdapterView.OnItemClickListener onItemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(getContext(),
                                "selected place "
                                        + ((Place)(adapterView.
                                        getItemAtPosition(i))).getPlaceText()
                                , Toast.LENGTH_SHORT).show();
                        //do something with the selection

                    }
                };
        fromAutoComplete.setAdapter(adapter);
        fromAutoComplete.setOnItemClickListener(onItemClickListener);
    }

}
