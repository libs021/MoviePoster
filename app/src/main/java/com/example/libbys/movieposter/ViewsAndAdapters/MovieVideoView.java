package com.example.libbys.movieposter.ViewsAndAdapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.libbys.movieposter.CustomMovieClasses.MovieVideo;
import com.example.libbys.movieposter.R;

import java.util.ArrayList;
import java.util.List;

public class MovieVideoView extends RecyclerView.Adapter<MovieVideoView.VideoUrlViewHolder> {

    private ArrayList<MovieVideo> mvideos;
    private final Context mcontext;

    public MovieVideoView (ArrayList<MovieVideo> videos, Context context) {
        mvideos=videos;
        mcontext=context;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @NonNull
    @Override
    public VideoUrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.layout_videos, parent, false);
        return new VideoUrlViewHolder(root);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull VideoUrlViewHolder holder, int position) {
        final MovieVideo currentVideo = mvideos.get(position);
        holder.textView.setText(currentVideo.getdescription());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(currentVideo.geturl()),null);
                mcontext.startActivity(intent);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mvideos==null) return 0;
        return mvideos.size();
    }

    public void swapData(ArrayList<MovieVideo> videos) {
        mvideos =videos;
    }

    class VideoUrlViewHolder extends RecyclerView.ViewHolder {

        //Holds a text view that will contain the description of the video and when clicked on will open up the video in the users
        //favorite app
        private final TextView textView;

        VideoUrlViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.TV_videos);
        }
    }

    public ArrayList<MovieVideo> getMvideos() {
        return mvideos;
    }

}
