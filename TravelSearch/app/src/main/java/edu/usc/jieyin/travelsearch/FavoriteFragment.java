package edu.usc.jieyin.travelsearch;

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
import org.w3c.dom.Text;

public class FavoriteFragment extends Fragment {
    public static JSONArray favoriteItems = new JSONArray();
    public static RecyclerView.Adapter fAdapter;
    private RecyclerView fRecyclerView;
    private RecyclerView.LayoutManager fLayoutManager;
    public static View view;

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

        viewSelector();

        fRecyclerView = (RecyclerView) view.findViewById(R.id.favoriteRecycle);
        fLayoutManager = new LinearLayoutManager(getContext());
        fRecyclerView.setLayoutManager(fLayoutManager);
        fAdapter = new FavoriteAdapter(favoriteItems, new ResultAdapterListener() {
            @Override
            public void iconTextViewOnClick(View v, int position) {
                Log.d("TEXT-CLICK","TEXT-CLICK FROM FAVORITE FRAGMENT");
            }

            @Override
            public void iconImageViewOnClick(View v, int position) {
                Log.d("FAVORITE-DELETE","DELETE EVENT DETECTED!");
                try {
                    Toast.makeText(getContext(),
                            favoriteItems.getJSONObject(position).getString("name") + " was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                    favoriteItems.remove(position);
                    fAdapter.notifyDataSetChanged();
                    viewSelector();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Inflate the layout for this fragment

        });
        fRecyclerView.setAdapter(fAdapter);
        return view;
    }

    public static void viewSelector(){
        RecyclerView recyclerView;
        TextView emptyView;
        recyclerView = view.findViewById(R.id.favoriteRecycle);
        emptyView = view.findViewById(R.id.noFavorite);
        if(favoriteItems.length() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}