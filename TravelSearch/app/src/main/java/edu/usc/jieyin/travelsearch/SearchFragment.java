package edu.usc.jieyin.travelsearch;
/**
 * AutoComplete part is borrowed from http://www.zoftino.com/google-places-auto-complete-android
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class SearchFragment extends Fragment {
    public Intent intent;
    private double currentLat;
    private double currentLon;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog progress;

    private EditText editKeyword, editDistance;
    private AutoCompleteTextView editLocation;
    private TextView keywordError, locationError;
    private RadioButton otherLocRadio, currentLocRadio;
    private Button searchButton, clearButton;
    private Spinner categorySpinner;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getContext(), NearbyActivity.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLocation();

    }
    public void onPause() {
        super.onPause();
        if( progress != null){
            progress.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.fromButtonGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EditText otherLocation = (EditText) view.findViewById(R.id.location);
                if (checkedId == R.id.fromCurrent) {
                    otherLocation.setFocusable(false);
                    view.findViewById(R.id.locationError).setVisibility(View.GONE);
                } else {
                    otherLocation.setFocusableInTouchMode(true);
                }
            }
        });


        editKeyword = (EditText) view.findViewById(R.id.keyword);
        otherLocRadio = (RadioButton) view.findViewById(R.id.fromOther);
        keywordError = (TextView) view.findViewById(R.id.keywordError);
        locationError = (TextView) view.findViewById(R.id.locationError);
        categorySpinner = (Spinner) view.findViewById(R.id.category);
        editDistance = (EditText) view.findViewById(R.id.distance);
        currentLocRadio = (RadioButton) view.findViewById(R.id.fromCurrent);
        editLocation = (AutoCompleteTextView) view.findViewById(R.id.location);

        searchButton = view.findViewById(R.id.searchButton);
        clearButton = view.findViewById(R.id.clearButton);

        setSearchButton();
        setClearButton();
        setAutoComplete();
        return view;
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "APP requires user location, or search results may fail to be shown",
                        Toast.LENGTH_SHORT).show();
            // Permission is not granted
            // Should we show an explanation?
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            //
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currentLat = location.getLatitude();
                                currentLon = location.getLongitude();
                            } else {
                                createLocationRequest();
                                Log.d("------LOCATION-----", "fired create location request");
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    Toast.makeText(getContext(),
                                            "APP requires user location, or search results may fail to be shown",
                                            Toast.LENGTH_SHORT).show();
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback,
                                        null /* Looper */);
                            }
                        }
                    });

            // Permission has already been granted
        }

    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                currentLat = locationResult.getLastLocation().getLatitude();
                currentLon = locationResult.getLastLocation().getLongitude();
                Log.d("---------LOCATION", Double.toString(currentLat));
            }
        };
    }


    public void nearbyQuery(String keyword, String category, int distance, String location) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "";
        String geoLoc = currentLat + "," + currentLon;

        if (!location.equals("")) {
            try {
                url = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/page1?Keyword=" + URLEncoder.encode(keyword, "UTF-8") +
                        "&Category=" + URLEncoder.encode(category, "UTF-8") + "&Distance=" + distance + "&Location=otherLoc&otherLoc=" + URLEncoder.encode(location, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = "http://jay-cs571-hw9.us-east-2.elasticbeanstalk.com/page1?Keyword=" + URLEncoder.encode(keyword, "UTF-8") +
                        "&Category=" + URLEncoder.encode(category, "UTF-8") + "&Distance=" + distance + "&Location=" + geoLoc;


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Log.d("MYURL---", url);

        // Request a string response from the provided URL.
        StringRequest nearbyRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        intent.putExtra("PlaceNearby", response);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        intent.putExtra("PlaceNearby", "FAILED");
                        startActivity(intent);
                    }
                });

        queue.add(nearbyRequest);
    }

    private void setSearchButton(){
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidForm = false;

                String keyword = editKeyword.getText().toString();
                String location = editLocation.getText().toString();

                if (!keyword.matches("\\s*\\S+.*")) {
                    keywordError.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),
                            "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                } else {
                    keywordError.setVisibility(View.GONE);
                    isValidForm = true;
                }
                if (otherLocRadio.isChecked()) {
                    if (!location.matches("\\s*\\S+.*")) {
                        locationError.setVisibility(View.VISIBLE);
                        isValidForm = false;
                        Toast.makeText(getActivity(),
                                "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                    } else {
                        locationError.setVisibility(View.GONE);
                        isValidForm = true;
                    }
                }
                if (isValidForm) {
                    int distance;
                    String category = categorySpinner.getSelectedItem().toString();

                    if (editDistance.getText().toString().equals("")) {
                        distance = 10;
                    } else {
                        distance = Integer.parseInt(editDistance.getText().toString());
                    }
                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Fetching Results");
                    progress.setProgressStyle(ProgressDialog. STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.show();
                    if (!otherLocRadio.isChecked()) {
                        nearbyQuery(keyword, category, distance, "");
                    } else {
                        nearbyQuery(keyword, category, distance, location);
                    }
                }

            }
        });


    }

    private void setClearButton(){
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editKeyword.setText("");
                editDistance.setText("");
                editLocation.setText("");
                locationError.setVisibility(View.GONE);
                keywordError.setVisibility(View.GONE);
                categorySpinner.setSelection(0);
                otherLocRadio.setChecked(false);
                currentLocRadio.setChecked(true);
            }
        });
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
        editLocation.setAdapter(adapter);
        editLocation.setOnItemClickListener(onItemClickListener);
    }

}

