package com.example.libbys.movieposter.ViewsAndAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.libbys.movieposter.CustomMovieClasses.Movie;
import com.example.libbys.movieposter.DetailActivity;
import com.example.libbys.movieposter.MainActivity;
import com.example.libbys.movieposter.R;
import com.example.libbys.movieposter.dataBaseFiles.movieHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviePosterView extends RecyclerView.Adapter<MoviePosterView.PosterViewHolder> {

    private final Context context;

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    private ArrayList<Movie> movies;

    public MoviePosterView (Context context){
        this.context=context;
        movies =null;
    }



    /**
     * Called when RecyclerView needs a new {@link android.support.v7.widget.RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, java.util.List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.layout_poster, parent, false);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    RecyclerView rv = (RecyclerView) parent;
                    int position = rv.getChildAdapterPosition(view);
                    Movie movie = movies.get(position);
                    Intent intent = new Intent (context, DetailActivity.class);
                    intent.putExtra("movie",movie);
                    //The activity will return this back to mainactivity so that it can be updated.
                    intent.putExtra("position",position);
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, MainActivity.ACTIVITY_REQUEST);
                }
        });
        return new PosterViewHolder(root);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link android.support.v7.widget.RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, java.util.List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final PosterViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        String urlForPoster = movie.getImage();
        Picasso.with(context).load(urlForPoster).into(holder.getPoster());
        if (movie.isFavorite()) {
            holder.getIsFavorite().setImageResource(R.drawable.is_favorite);
        }
        else holder.getIsFavorite().setImageResource(R.drawable.add_favorite);
        holder.getIsFavorite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.isFavorite()) {
                    movieHelper.removeMovieFromDB(movie,context);
                    holder.getIsFavorite().setImageResource(R.drawable.add_favorite);
                    movie.setFavorite(false);
                }
                else {
                    movieHelper.addMovietoDB(movie,context,null);
                    holder.getIsFavorite().setImageResource(R.drawable.is_favorite);
                    movie.setFavorite(true);
                }
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
        if (movies==null) return 0;
        return movies.size();
    }

    public void setMovies (ArrayList<Movie> movies) {
        this.movies = movies;
    }


    class PosterViewHolder extends RecyclerView.ViewHolder {
        private final ImageView Poster;
        private final ImageView isFavorite;

        PosterViewHolder(View itemView) {
            super(itemView);
            Poster = itemView.findViewById(R.id.poster_IV);
            isFavorite = itemView.findViewById(R.id.favorite);
        }

        ImageView getPoster() {return Poster;}
        ImageView getIsFavorite() {return isFavorite;}
    }

}
