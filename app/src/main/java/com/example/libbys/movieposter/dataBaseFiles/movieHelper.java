package com.example.libbys.movieposter.dataBaseFiles;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.example.libbys.movieposter.CustomMovieClasses.Movie;
import com.example.libbys.movieposter.CustomMovieClasses.MovieReview;
import com.example.libbys.movieposter.ViewsAndAdapters.MovieReviewView;
import com.example.libbys.movieposter.ViewsAndAdapters.MovieVideoView;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class movieHelper extends SQLiteOpenHelper {
    private static final int Version = 1;
    private static final String dataBaseName = "movie.db";
    private static final String CREATE_MOVIETABLE = "Create Table " + movieContract.movieEntry.TABLE_NAME + " ( " +
            movieContract.movieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            movieContract.movieEntry.COLUMN_BACKDROPPATH + " TEXT NOT NULL, " +
            movieContract.movieEntry.COLUMN_IMAGEPATH + " TEXT NOT NULL, " +
            movieContract.movieEntry.COLUMN_RATING + " DOUBLE DEFAULT 0 NOT NULL, " +
            movieContract.movieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
            movieContract.movieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL, " +
            movieContract.movieEntry.COLUMN_TITLE + " TEXT NOT NULL); ";

    private static final String CREATE_REVIEWTABLE = "Create Table " + movieContract.reviewEntry.TABLE_NAME + " ( " +
            movieContract.reviewEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            movieContract.reviewEntry.COLUMN_MOVIEID + " INTEGER REFERENCES " + movieContract.movieEntry.TABLE_NAME + "(" + movieContract.movieEntry.COLUMN_ID + "),"+
            movieContract.reviewEntry.COLUMN_REVIEW+ " TEXT NOT NULL, " +
            movieContract.reviewEntry.COLUMN_USER + " TEXT NOT NULL); ";

    private static final String CREATE_VIDEOTABLE = "Create Table " + movieContract.videoEntry.TABLE_NAME+ " ( " +
            movieContract.videoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            movieContract.videoEntry.COLUMN_MOVIEID + " INTEGER REFERENCES " + movieContract.movieEntry.TABLE_NAME + "(" + movieContract.movieEntry.COLUMN_ID + "),"+
            movieContract.videoEntry.COLUMN_DESCRIPTION+ " TEXT NOT NULL, " +
            movieContract.videoEntry.COLUMN_URL + " TEXT NOT NULL); ";



    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    movieHelper(Context context) {
        super(context, dataBaseName, null, Version);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIETABLE);
        Log.e(TAG, "onCreate: " + CREATE_MOVIETABLE);
        db.execSQL(CREATE_REVIEWTABLE);
        Log.e(TAG, "onCreate: " + CREATE_REVIEWTABLE);
        db.execSQL(CREATE_VIDEOTABLE);
        Log.e(TAG, "onCreate: " + CREATE_VIDEOTABLE);

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    //No need to do anything yet as this is our first run at the database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /** This method adds reviews to the database
     * This assumes that the movie with id has already been added to the database
     * @param id   Movie Id that the Reviews are associated with
     * @param reviews List if Reviews to add to database
     */
    private static void addReviewstoDB(int id, ArrayList<MovieReview> reviews, Context context) {
        ContentValues values = new ContentValues();
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_REVIEWS);
        for (MovieReview review: reviews) {
            values.put(movieContract.reviewEntry.COLUMN_MOVIEID,id);
            values.put(movieContract.reviewEntry.COLUMN_USER,review.getmUser());
            values.put(movieContract.reviewEntry.COLUMN_REVIEW,review.getmReview());
            context.getContentResolver().insert(uri,values);
        }
    }

        public static void addMovietoDB (Movie movieToDetail, Context context, MovieReviewView reviews) {
        ContentValues values = new ContentValues();
        values.put(movieContract.movieEntry.COLUMN_BACKDROPPATH,movieToDetail.getBackDropPath());
        values.put(movieContract.movieEntry.COLUMN_IMAGEPATH,movieToDetail.getImage());
        values.put(movieContract.movieEntry.COLUMN_PLOT,movieToDetail.getPlot());
        values.put(movieContract.movieEntry.COLUMN_RATING,movieToDetail.getAverage());
        values.put(movieContract.movieEntry.COLUMN_RELEASEDATE,movieToDetail.getRelease());
        values.put(movieContract.movieEntry.COLUMN_TITLE,movieToDetail.getTitle());
        values.put(movieContract.movieEntry.COLUMN_ID,movieToDetail.getID());
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.movieEntry.TABLE_NAME);
        uri = context.getContentResolver().insert(uri,values);
        //This will happen when you try to enter an invalid movie to the database, or a duplicate.
        //returning here keeps us from entering double entries into the reviews and videos tables
        if (ContentUris.parseId(uri)==-1) return;
        //TODO when adding a movie to favorite from Main Activity we set the reviews to null as MainActivity doesn't have this info
            // implement gathering this info here
        if (reviews!=null) addReviewstoDB(movieToDetail.getID(), reviews.getReviews(), context);
    }

    public static void removeMovieFromDB(Movie movieToDetail, Context context) {
        Uri uri = Uri.withAppendedPath(movieContract.BASE_CONTENT_URI,movieContract.PATH_MOVIES);
        String where = movieContract.movieEntry.COLUMN_ID + "=?";
        String [] args = {String.valueOf(movieToDetail.getID())};
        context.getContentResolver().delete(uri,where,args);
        movieToDetail.setFavorite(false);
    }
}
