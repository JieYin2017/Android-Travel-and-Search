package edu.usc.jieyin.travelsearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MapFragment extends Fragment {

    private AutoCompleteTextView fromAutoComplete;
    private String fromPlace;
    private Spinner travelModeSpinner;
    private SupportMapFragment mapFragment;
    private double destLat, destLon;
    private String placeName;
    private GoogleMap mMap;
    private ArrayList<Double> lats = new ArrayList<>();
    private ArrayList<Double> lons = new ArrayList<>();
    private ArrayList<LatLng> routePoints = new ArrayList<>();
    private int check;
    private static final int COLOR_BLACK_ARGB = 0xff4285f4;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;


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
        setTravelModeSpinner();

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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 15.0f));
                }
            });
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
            e.printStackTrace();
        }

    }

    private void setAutoComplete() {
        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(getContext());
        AdapterView.OnItemClickListener onItemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(getContext(),
                                "selected place "
                                        + ((Place) (adapterView.
                                        getItemAtPosition(i))).getPlaceText()
                                , Toast.LENGTH_SHORT).show();
                        updatePath();

                    }
                };
        fromAutoComplete.setAdapter(adapter);
        fromAutoComplete.setOnItemClickListener(onItemClickListener);
    }

    private void updatePath() {

        String routeURL = "";
        String travelMode = travelModeSpinner.getSelectedItem().toString();
        fromPlace = fromAutoComplete.getText().toString();

        if (!fromPlace.matches("\\s*\\S+.*")) {
            Toast.makeText(getContext(),
                    "Please enter or select a valid place first",
                    Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            try {
                routeURL = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/routes/?from=" + URLEncoder.encode(fromPlace, "UTF-8")
                        + "&dest=" + URLEncoder.encode(placeName, "UTF-8") + "&mode=" + travelMode.toLowerCase();
                Log.d("ROUTE", routeURL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            StringRequest routeRequest = new StringRequest(Request.Method.GET, routeURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject routeObject = new JSONObject(response);
                                initializeRouteArray(routeObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Activity activity = getActivity();
                            if (activity != null && isAdded()) {
                                Toast.makeText(getContext(),
                                        "ERROR: please check your network and retry",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            routeRequest.setShouldCache(false);
            queue.add(routeRequest);
        }
    }


    private void initializeRouteArray(JSONObject routeObject) {
        lats.clear();
        lons.clear();
        try {
            JSONArray lat = routeObject.getJSONArray("lats");
            JSONArray lon = routeObject.getJSONArray("lons");
            for (int i = 0; i < lat.length(); i++) {
                lats.add(lat.getDouble(i));
                lons.add(lon.getDouble(i));
            }
            Log.d("ROUTE", "" + lats.size() + travelModeSpinner.getSelectedItem().toString());
            if (lats.size() == 0) {
                Toast.makeText(getContext(),
                        "Sorry, we cannot provide any direction based on your provided location",
                        Toast.LENGTH_SHORT).show();
            } else {
                updateMap();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateMap() {
        mMap.clear();
        routePoints.clear();

        for (int i = 0; i < lats.size(); i++) {
            routePoints.add(new LatLng(lats.get(i), lons.get(i)));
        }
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(routePoints));

        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);

        Double east = Math.max(lats.get(0), lats.get(lats.size() - 1));
        Double west = Math.min(lats.get(0), lats.get(lats.size() - 1));

        Double north = Math.max(lons.get(0), lons.get(lons.size() - 1));
        Double south = Math.min(lons.get(0), lons.get(lons.size() - 1));

        Log.d("MAP_COOR", "southwest" + south + west + "  northeast" + north + east);
        LatLngBounds routeBound = new LatLngBounds(
                new LatLng(west, south), new LatLng(east, north));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(routeBound, (int) (30 * getResources().getDisplayMetrics().density)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lats.get(lats.size() - 1), lons.get(lons.size() - 1)))
                .title(placeName)).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lats.get(0), lons.get(0)))
                .title(fromPlace)).showInfoWindow();
    }

    private void setTravelModeSpinner() {
        travelModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (++check > 1) {
                    Log.d("TravelModeSpinner", travelModeSpinner.getSelectedItem().toString());
                    updatePath();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });
    }
};

