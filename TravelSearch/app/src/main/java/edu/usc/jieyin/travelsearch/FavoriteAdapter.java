package edu.usc.jieyin.travelsearch;

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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private JSONArray results;
    public static ResultAdapterListener onClickListener;

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
            place = (TextView) view.findViewById(R.id.place_favorite);
            address = (TextView) view.findViewById(R.id.address_favorite);
            heart = (ImageView) view.findViewById(R.id.heart_favorite);
            placeAddress = (LinearLayout) view.findViewById(R.id.placeAddress_favorite);
            category = (ImageView) view.findViewById(R.id.category_icon_favorite);


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
    public FavoriteAdapter(JSONArray results, ResultAdapterListener listener) {
        onClickListener = listener;
        this.results = results;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        ViewHolder vh;
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_list, parent, false);

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
