package edu.usc.jieyin.travelsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private JSONArray results;
    public static ResultAdapterListener onClickListener;
    private SharedPreferences preferences;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView place, address;
        public ImageView heart, category;
        public LinearLayout placeAddress;

        public ViewHolder(View view) {
            super(view);
            place = (TextView) view.findViewById(R.id.place);
            address = (TextView) view.findViewById(R.id.address);
            heart = (ImageView) view.findViewById(R.id.heart);
            placeAddress = (LinearLayout) view.findViewById(R.id.placeAddress);
            category = (ImageView) view.findViewById(R.id.category_icon);


            placeAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.iconTextViewOnClick(v, getAdapterPosition());
                }
            });
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.iconImageViewOnClick(v, getAdapterPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ResultAdapter(JSONArray results, ResultAdapterListener listener, Context context) {
        preferences = context.getSharedPreferences("FAVORITE", Context.MODE_PRIVATE);
        onClickListener = listener;
        this.results = results;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        ViewHolder vh;
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_list, parent, false);

        vh = new ViewHolder(itemView);

        // create a new view

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemCount() != 0) {
            try {
                JSONObject placeItem = results.getJSONObject(position);


                holder.place.setText(placeItem.getString("name"));
                holder.address.setText(placeItem.getString("vicinity"));
                boolean isFavorite = false;

                if (preferences.contains(placeItem.getString("place_id"))){
                    isFavorite = true;
                }

                /*
                for (int i = 0; i < FavoriteFragment.favoriteItems.length(); i++) {
                    if (FavoriteFragment.favoriteItems.getJSONObject(i).getString("place_id")
                            .equals(placeItem.getString("place_id"))) {
                        isFavorite = true;
                    }
                }
                */
                if (!isFavorite) {
                    holder.heart.setImageResource(R.drawable.icon_heart_outline_black);
                } else {
                    holder.heart.setImageResource(R.drawable.icon_heart_fill_red);
                }

                Picasso.get().load(placeItem.getString("icon")).fit().into(holder.category);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return results.length();
    }
}