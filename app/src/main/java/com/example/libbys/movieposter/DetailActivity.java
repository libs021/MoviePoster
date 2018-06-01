package com.example.libbys.movieposter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.libbys.movieposter.CustomMovieClasses.Movie;
import com.example.libbys.movieposter.CustomMovieClasses.MovieReview;
import com.example.libbys.movieposter.CustomMovieClasses.MovieVideo;
import com.example.libbys.movieposter.NetWorkUtils.JSONUtils;
import com.example.libbys.movieposter.NetWorkUtils.Network;
import com.example.libbys.movieposter.ViewsAndAdapters.MovieReviewView;
import com.example.libbys.movieposter.ViewsAndAdapters.MovieVideoView;
import com.example.libbys.movieposter.dataBaseFiles.movieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    private Movie movieToDetail;
    private MovieVideoView movieVideoView;
    private MovieReviewView movieReviewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        movieToDetail = getIntent().getParcelableExtra("movie");
        populateViews();


        //if no saved information pull the information from the web
        if (savedInstanceState==null) {
            movieVideoView = new  MovieVideoView(null,this);
            movieReviewView = new MovieReviewView(null);
            new detailActivityNetwork(this,movieToDetail).execute();
        }
        else { //load saved info.
            movieReviewView = new MovieReviewView(savedInstanceState.<MovieReview>getParcelableArrayList("Reviews"));
            movieVideoView = new MovieVideoView(savedInstanceState.<MovieVideo>getParcelableArrayList("Videos"),this);

        }

        //Set Up recycler view for Trailers
        RecyclerView trailerRecyclerView = findViewById(R.id.RV_Videos);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerRecyclerView.setAdapter(movieVideoView);

        //Set up recycler view for reviews
        RecyclerView reviewRecyclerView = findViewById(R.id.RV_Reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(movieReviewView);
    }

    private void populateViews() {
        ImageView imageView = findViewById(R.id.backdrop_IV);
        Picasso.with(this).load(movieToDetail.getBackDropPath()).into(imageView);
        RatingBar ratingBar = findViewById(R.id.rating_RB);
        double rating = movieToDetail.getAverage();
        float floatrating = (float) rating;
        ratingBar.setRating(floatrating);
        populateTextViews();
    }

    private void populateTextViews() {
        TextView viewtoUpdate = findViewById(R.id.title_TV);
        viewtoUpdate.setText(movieToDetail.getTitle());
        viewtoUpdate = findViewById(R.id.description_TV);
        viewtoUpdate.setText(movieToDetail.getPlot());
        viewtoUpdate = findViewById(R.id.release_TV);
        viewtoUpdate.setText(movieToDetail.getRelease());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //We don't add the movie to the bundle as it is already available via the intent.
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Reviews",movieReviewView.getReviews());
        outState.putParcelableArrayList("Videos",movieVideoView.getMvideos());
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //this hides either the remove from favorites or add to favorites depening on wheather
        //the movie is already a favorite.
        if (movieToDetail.isFavorite()) {
            menu.findItem(R.id.add).setVisible(false);
            menu.findItem(R.id.remove).setVisible(true);
        }
        else {
            menu.findItem(R.id.remove).setVisible(false);
            menu.findItem(R.id.add).setVisible(true);
        }
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add:
                addMovietoDB();
                break;
            case R.id.remove:
                removeMovieFromDB();
        }
        return true;
    }

    private void removeMovieFromDB() {
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_MOVIES);
        String where = movieContract.movieEntry.COLUMN_ID + "=?";
        String [] args = {String.valueOf(movieToDetail.getID())};
        getContentResolver().delete(uri,where,args);
        movieToDetail.setFavorite(false);
    }

    private void addMovietoDB () {
        ContentValues values = new ContentValues();
        values.put(movieContract.movieEntry.COLUMN_BACKDROPPATH,movieToDetail.getBackDropPath());
        values.put(movieContract.movieEntry.COLUMN_IMAGEPATH,movieToDetail.getImage());
        values.put(movieContract.movieEntry.COLUMN_PLOT,movieToDetail.getPlot());
        values.put(movieContract.movieEntry.COLUMN_RATING,movieToDetail.getAverage());
        values.put(movieContract.movieEntry.COLUMN_RELEASEDATE,movieToDetail.getRelease());
        values.put(movieContract.movieEntry.COLUMN_TITLE,movieToDetail.getTitle());
        values.put(movieContract.movieEntry.COLUMN_ID,movieToDetail.getID());
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.movieEntry.TABLE_NAME);
        uri = getContentResolver().insert(uri,values);
        //This will happen when you try to enter an invalid movie to the database, or a duplicate.
        //returning here keeps us from entering double entries into the reviews and videos tables
        if (ContentUris.parseId(uri)==-1) return;
        addReviewstoDB(movieToDetail.getID(),movieReviewView.getReviews());
        addVideostoDB(movieToDetail.getID(),movieVideoView.getMvideos());
        movieToDetail.setFavorite(true);
    }


    /**
     * This method adds Videos to the database that are associated to movie with id
     * This assumes that the movie is already in the database
     * @param id   Movie Id that the video's are associated with
     * @param mvideos List if Videos to add to database
     */
    private void addVideostoDB(int id, ArrayList<MovieVideo> mvideos) {
        ContentValues values = new ContentValues();
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_VIDEOS);
        for (MovieVideo video:mvideos) {
            values.put(movieContract.videoEntry.COLUMN_MOVIEID,id);
            values.put(movieContract.videoEntry.COLUMN_DESCRIPTION,video.getdescription());
            values.put(movieContract.videoEntry.COLUMN_URL,video.geturl());
            getContentResolver().insert(uri,values);
        }
    }

    /** This method adds reviews to the database
     * This assumes that the movie with id has already been added to the database
     * @param id   Movie Id that the Reviews are associated with
     * @param reviews List if Reviews to add to database
     */
    private void addReviewstoDB(int id, ArrayList<MovieReview> reviews) {
        ContentValues values = new ContentValues();
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_REVIEWS);
        for (MovieReview review: reviews) {
            values.put(movieContract.reviewEntry.COLUMN_MOVIEID,id);
            values.put(movieContract.reviewEntry.COLUMN_USER,review.getmUser());
            values.put(movieContract.reviewEntry.COLUMN_REVIEW,review.getmReview());
            getContentResolver().insert(uri,values);
        }
    }


    private static class detailActivityNetwork extends AsyncTask<String,Void,String>{

        private final WeakReference<DetailActivity> mActivity;
        private final Movie mMovie;

        detailActivityNetwork(DetailActivity activity, Movie movie) {
            this.mActivity = new WeakReference<>(activity);
            mMovie = movie;

        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... strings) {
            return Network.queryMovie(mMovie.getID(),mActivity.get().getApplication().getApplicationContext());
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            mActivity.get().movieVideoView.swapData(JSONUtils.parseJsonMovieVideos(s));
            mActivity.get().movieVideoView.notifyDataSetChanged();
            mActivity.get().movieReviewView.setReviews(JSONUtils.parseJsonMovieReviews(s));
            mActivity.get().movieReviewView.notifyDataSetChanged();
        }

    }
}
