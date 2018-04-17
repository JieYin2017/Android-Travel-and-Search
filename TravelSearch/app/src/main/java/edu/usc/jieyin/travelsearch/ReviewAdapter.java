package edu.usc.jieyin.travelsearch;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private JSONArray reviews;

    public static ReviewAdapterListener onClickListener;

    public interface ReviewAdapterListener {
        void iconTextViewOnClick(View v, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView user, time, content;
        public ImageView userIcon;
        public RatingBar ratingBar;
        public RelativeLayout reviewBar;

        public ViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.userName);
            time = (TextView) view.findViewById(R.id.reviewTime);
            content = (TextView) view.findViewById(R.id.reviewContent);
            userIcon = (ImageView) view.findViewById(R.id.userIcon);
            reviewBar = (RelativeLayout) view.findViewById(R.id.reviewDetail);
            ratingBar = (RatingBar) view.findViewById(R.id.reviewRating);


            reviewBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.iconTextViewOnClick(v, getAdapterPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdapter(JSONArray reviews, ReviewAdapterListener listener) {
        onClickListener = listener;
        this.reviews = reviews;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        ViewHolder vh;
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list, parent, false);

        vh = new ViewHolder(itemView);

        // create a new view

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            JSONObject reviewItem = reviews.getJSONObject(position);

            if(reviewItem.has("author_name")){
                holder.user.setText(reviewItem.getString("author_name"));
            }else{
                holder.user.setText(reviewItem.getJSONObject("user").getString("name"));
            }
            holder.content.setText(reviewItem.getString("text"));
            holder.ratingBar.setRating((float)reviewItem.getDouble("rating"));
            if(reviewItem.has("profile_photo_url")){
                Picasso.get().load(reviewItem.getString("profile_photo_url")).fit().into(holder.userIcon);
            }else{
                Picasso.get().load(reviewItem.getJSONObject("user").getString("image_url")).fit().into(holder.userIcon);
            }
            if(reviewItem.has("time_created")){
                holder.time.setText(reviewItem.getString("time_created"));
            }else{
                long millis = reviewItem.getInt("time") * 1000L;
                Date date = new Date(millis);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = sdf.format(date);
                holder.time.setText(formattedDate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviews.length();
    }
}
