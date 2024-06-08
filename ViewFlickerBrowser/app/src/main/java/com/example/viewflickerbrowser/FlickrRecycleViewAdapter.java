package com.example.viewflickerbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickrRecycleViewAdapter  extends RecyclerView.Adapter<FlickrRecycleViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecycleViewAda";
    private List<Photo> mPhotoList;
    private Context mContext;//a context isn't always needed in an adapter, but we're going to be using it as
    //an external library to take care of downloading those thumbnails for us from Flickr


    public FlickrRecycleViewAdapter( Context context,List<Photo> photoList) {
        mPhotoList = photoList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//inflate a view from the browse.xml layout we created and then return the view
        //called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        //final parameter false tells the inflater whether to attach the view to its root or not.
        return new FlickrImageViewHolder(view);
    }

    @Override//called by the RecycleView when it wants new data to be stored in a ViewHolder, so that it can display it
    //Now as items scroll off the screen, the recycle view will provide a recycle ViewHolder object and tell us the position
    //of the data object that it needs to display.
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        // called by the layout manager when it wants new data in an existing row
        Photo photoItem = mPhotoList.get(position);
        Log.d(TAG, "onBindViewHolder: starts" + photoItem.getTitle() +"---->" + position);
        Picasso.get().load(photoItem.getImage())//picasso is singleton, instead of using new, use static to make sure there is only one picasso in our app
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);

        holder.title.setText(photoItem.getTitle());

    }

    @Override
    public int getItemCount() {//return the number of photos in the list
        Log.d(TAG, "getItemCount: called");
        return ((mPhotoList  != null) && (mPhotoList.size() != 0) ? mPhotoList.size(): 0);
    }

    void loadNewData(List<Photo> newPhotos){
        mPhotoList = newPhotos;
        notifyDataSetChanged();//tell the recycle view that the data has changed, so that it can go ahead and refresh the display
    }

    public Photo getPhoto(int position){
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.imageTitle);
        }
    }
}
