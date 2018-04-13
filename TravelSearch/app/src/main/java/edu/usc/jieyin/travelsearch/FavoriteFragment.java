package edu.usc.jieyin.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

public class FavoriteFragment extends Fragment {
    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static JSONArray favoriteItems = new JSONArray();

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
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.favoriteRecycle);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ResultAdapter(favoriteItems, new ResultAdapterListener() {
            @Override
            public void iconTextViewOnClick(View v, int position) {

            }

            @Override
            public void iconImageViewOnClick(View v, int position) {
                favoriteItems.remove(position);
                mAdapter.notifyDataSetChanged();
                try {
                    Toast.makeText(getContext(),
                            favoriteItems.getJSONObject(position).getString("name") + " was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Inflate the layout for this fragment

        });
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}