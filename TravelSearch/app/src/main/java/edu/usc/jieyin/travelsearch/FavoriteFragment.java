package edu.usc.jieyin.travelsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FavoriteFragment extends Fragment {
    private ArrayList<Map.Entry<String, JSONObject>> favoriteItems = new ArrayList<>();
    private RecyclerView.Adapter fAdapter;
    private RecyclerView fRecyclerView;
    private RecyclerView.LayoutManager fLayoutManager;
    private View view;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        editor = this.getActivity().getSharedPreferences("FAVORITE", Context.MODE_PRIVATE).edit();
        preferences = this.getActivity().getSharedPreferences("FAVORITE", Context.MODE_PRIVATE);
        formatPref();
        viewSelector();

        fRecyclerView = (RecyclerView) view.findViewById(R.id.favoriteRecycle);
        fLayoutManager = new LinearLayoutManager(getContext());
        fRecyclerView.setLayoutManager(fLayoutManager);

        fAdapter = new FavoriteAdapter(favoriteItems, new ResultAdapterListener() {
            @Override
            public void iconTextViewOnClick(View v, int position) {
                Log.d("TEXT-CLICK", "TEXT-CLICK FROM FAVORITE FRAGMENT");
                Intent intent = new Intent(getContext(), DetailActivity.class);

                //intent.putExtra("PlaceDetail", favoriteItems.getJSONObject(position).toString());
                intent.putExtra("PlaceDetail", favoriteItems.get(position).getValue().toString());
                startActivity(intent);
            }

            @Override
            public void iconImageViewOnClick(View v, int position) {
                Log.d("FAVORITE-DELETE", "DELETE EVENT DETECTED!");
                try {

                    Toast.makeText(getContext(),
                            favoriteItems.get(position).getValue().getString("name") + " was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                    editor.remove(favoriteItems.get(position).getValue().getString("place_id")).commit();
                    formatPref();
                    viewSelector();
                    fAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Inflate the layout for this fragment

        });
        fRecyclerView.setAdapter(fAdapter);
        return view;
    }

    private void viewSelector() {
        RecyclerView recyclerView;
        TextView emptyView;
        recyclerView = view.findViewById(R.id.favoriteRecycle);
        emptyView = view.findViewById(R.id.noFavorite);
        if (favoriteItems.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void formatPref() {
        favoriteItems.clear();
        Map<String, ?> prefMap = preferences.getAll();
        Map<String, JSONObject> formatMap = new LinkedHashMap<>();

        try {
            for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
                JSONObject valueObject = new JSONObject((String) entry.getValue());
                formatMap.put(entry.getKey(), valueObject);
            }
        } catch (JSONException e) {
            Log.d("Favorite", "formatPref fails");
        }
        favoriteItems.addAll(formatMap.entrySet());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            formatPref();
            viewSelector();
            fAdapter.notifyDataSetChanged();
        }
        else {
            return;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        formatPref();
        viewSelector();
        fAdapter.notifyDataSetChanged();
    }
}