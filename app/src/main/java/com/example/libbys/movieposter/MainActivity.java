package com.example.libbys.movieposter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.libbys.movieposter.CustomMovieClasses.Movie;
import com.example.libbys.movieposter.NetWorkUtils.JSONUtils;
import com.example.libbys.movieposter.ViewsAndAdapters.MoviePosterView;
import com.example.libbys.movieposter.NetWorkUtils.Network;
import com.example.libbys.movieposter.dataBaseFiles.movieContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public  class MainActivity extends AppCompatActivity {
    public static final int ACTIVITY_REQUEST = 72;
    private MoviePosterView adapter;
    private String currentView ="";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Movies",adapter.getMovies());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new MoviePosterView(this);
        RecyclerView recyclerView = findViewById(R.id.RV_Main);
        recyclerView.setLayoutManager(new GridLayoutManager(this,getResources().getInteger(R.integer.mainActivitySpan)));
        recyclerView.setAdapter(adapter);
        if (savedInstanceState==null) {
            load();
        }
        else {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList("Movies");
            adapter.setMovies(movies);
            adapter.notifyDataSetChanged();
        }

    }


    private void runAsyncTask(String path) {
        if (path.equals(getString(R.string.Favorite_Value))) new getDataBaseInfo(this).execute();
        else new getNetworkinfo(this).execute(path);
    }

    private void setUpMovies(ArrayList<Movie> movies) {
        adapter.setMovies(movies);
        adapter.notifyDataSetChanged();
    }

    private void load() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String sort = sharedPreferences.getString(MainActivity.this.getString(R.string.ListPreference_Key),
                MainActivity.this.getString(R.string.Top_Rated_Value));
        if (!sort.equals(currentView)) {
            runAsyncTask(sort);
            currentView=sort;
        }
    }

    private static class getNetworkinfo extends AsyncTask<String,Void,String>{

        private final WeakReference<MainActivity> mActivity;

        getNetworkinfo(MainActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }


        @Override
        protected String doInBackground(String... strings) {
            return Network.getMovieList(strings[0],mActivity.get().getBaseContext().getApplicationContext());
        }


        @Override
        protected void onPostExecute(String s) {
            //Network call will return null if the network isn't connected.
            TextView error = mActivity.get().findViewById(R.id.TV_MAIN);
            RecyclerView view = mActivity.get().findViewById(R.id.RV_Main);
            if (s == null) {
                error.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                return;
            }
            error.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            ArrayList<Movie> movies = JSONUtils.parseMovieListJSON(s);
            mActivity.get().setUpMovies(movies);
            //This returns every movie id that is in the database thus indicating that it is a favorite and sets the isfavorite variable accordignly
            Cursor cursor = mActivity.get().getContentResolver().query(Uri.withAppendedPath(movieContract.BASE_CONTENT_URI, movieContract.PATH_MOVIES),
                    new String[]{movieContract.movieEntry.COLUMN_ID}, null, null, null);
            if (cursor != null && cursor.getCount()!=0) {

                for (Movie movie : movies) {
                    int id = movie.getID();
                    cursor.moveToFirst();
                    do {
                        if (id == cursor.getInt(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_ID))) {
                            movie.setFavorite(true);
                            Log.e("MainActivity", "onPostExecute: id" + id + ", Favorite ID" + cursor.getInt(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_ID)));

                            return;
                        }
                    } while (cursor.moveToNext());

                }
                cursor.close();
            }
        }
    }

    private static class getDataBaseInfo extends AsyncTask<Void,Void,Cursor>{
        private final WeakReference<MainActivity> mActivity;

        getDataBaseInfo (MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param cursor The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */

        @Override
        protected void onPostExecute(Cursor cursor) {
            ArrayList<Movie> movies = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    String image = cursor.getString(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_IMAGEPATH));
                    String title = cursor.getString(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_TITLE));
                    String release = cursor.getString(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_RELEASEDATE));
                    Double average = cursor.getDouble(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_RATING));
                    String plot = cursor.getString(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_PLOT));
                    String backDropPath = cursor.getString(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_BACKDROPPATH));
                    int id = cursor.getInt(cursor.getColumnIndex(movieContract.movieEntry.COLUMN_ID));
                    movies.add(new Movie(image, title, release, average, plot, backDropPath, id, true));
                } while (cursor.moveToNext());
                TextView error = mActivity.get().findViewById(R.id.TV_MAIN);
                RecyclerView view = mActivity.get().findViewById(R.id.RV_Main);
                error.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                mActivity.get().setUpMovies(movies);
            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Cursor doInBackground(Void... voids) {
            Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_MOVIES);
            return mActivity.get().getBaseContext().getContentResolver().query(uri,null,null,null,null,null);
        }
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
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.remove).setVisible(false);
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
        if (item.getItemId()==R.id.settings) {
            Intent intent = new Intent (MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //this allows us to reload preferences if they changed.
        load();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode What request is providing the result
     * @param resultCode the result of the request
     * @param data Result of the request
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK) {
            boolean favorite = data.getBooleanExtra("isfavorite",false);
            int position = data.getIntExtra("position",-1);
            Movie movie = adapter.getMovies().get(position);
            //User was viewing a detail activity of a movie that was selected from the favorite list
            //so if the movie is no longer a favorite (Detail activity removed the movie) we will remove the movie
            if (!favorite && currentView.equals(getResources().getString(R.string.Favorite_Value))) {
                adapter.getMovies().remove(movie);
                adapter.notifyDataSetChanged();
            }
            else movie.setFavorite(favorite);
        }
        adapter.notifyDataSetChanged();
    }
}
