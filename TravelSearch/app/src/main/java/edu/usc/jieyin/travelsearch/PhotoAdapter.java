package edu.usc.jieyin.travelsearch;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList<Bitmap> photos;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public ImageView photoGrid;

        public ViewHolder(View view) {
            super(view);
            photoGrid = (ImageView) view.findViewById(R.id.photoGrid);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotoAdapter(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        ViewHolder vh;
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_list, parent, false);

        vh = new ViewHolder(itemView);

        // create a new view

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap photoItem = photos.get(position);
        holder.photoGrid.setImageBitmap(photoItem);
        
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return photos.size();
    }
}
