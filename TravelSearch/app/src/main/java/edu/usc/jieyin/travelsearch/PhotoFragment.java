package edu.usc.jieyin.travelsearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class PhotoFragment extends Fragment {
    protected GeoDataClient mGeoDataClient;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noPhoto;
    private ArrayList<Bitmap> photoList = new ArrayList<>();


    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        noPhoto = view.findViewById(R.id.noPhoto);
        mRecyclerView = view.findViewById(R.id.photoRecycle);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PhotoAdapter(photoList);
        mRecyclerView.setAdapter(mAdapter);

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        return view;
    }

    public void setPhoto(String placeID){
        getPhotos(placeID);

    }

    public void getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                for(int i = 0; i < photoMetadataBuffer.getCount(); i ++){
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            float scaledHeight = pxFromDp(350) * bitmap.getHeight() / bitmap.getWidth();
                            Bitmap resize = Bitmap.createScaledBitmap(bitmap, (int) pxFromDp(350), (int) scaledHeight,true);
                            photoList.add(resize);
                            if(photoList.size() == photoMetadataBuffer.getCount()){
                                Log.d("RecyclerView","notifyDatasetchange");
                                Log.d("RecyclerView","Length of photos" + photoList.size());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                if(photoMetadataBuffer.getCount() == 0){
                    noPhoto.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }else{
                    noPhoto.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private float pxFromDp(float dp) {
        if (isAdded()) {
            Log.d("metrics", "" + dp * getResources().getDisplayMetrics().density);
            return dp * getResources().getDisplayMetrics().density;
        }else{
            return 918.75f;
        }
    }
}
