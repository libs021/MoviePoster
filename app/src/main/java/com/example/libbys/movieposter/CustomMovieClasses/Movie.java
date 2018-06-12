package com.example.libbys.movieposter.CustomMovieClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    //represents the url to retrieve the thumbnail image
    private final String mImagePath;
    //represents the title of the movie
    private final String mTitle;
    //represents the release date of the movie in String format
    private final String mRelease;
    //represents the average rating of the movie
    private final Double mAverage;
    // Brief multi line description of the move
    private final String mPlot;
    //Url that will retrieve the backdrop image
    private final String mBackDropPath;
    //id used to uniquely identify each movie
    private final int mID;
    // declares if this movie is a favorite
    private boolean isFavorite = false;

    public Movie (String image,String title,String release, Double average, String plot, String BackDropPath, int id) {
        mImagePath = image;
        mTitle = title;
        mRelease = release;
        mAverage = average;
        mPlot = plot;
        mBackDropPath= BackDropPath;
        mID = id;
    }

    private Movie(Parcel in) {
        mImagePath = in.readString();
        mTitle = in.readString();
        mRelease = in.readString();
        mPlot = in.readString();
        mBackDropPath = in.readString();
        mAverage = in.readDouble();
        mID = in.readInt();
        isFavorite = (Boolean) in.readValue(getClass().getClassLoader());
    }

    public Movie (String image,String title,String release, Double average, String plot, String BackDropPath, int id, Boolean favorite) {
        mImagePath = image;
        mTitle = title;
        mRelease = release;
        mAverage = average;
        mPlot = plot;
        mBackDropPath= BackDropPath;
        mID = id;
        isFavorite=favorite;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /** GETTERS AND SETTERS
     * As of know we only use getters as the data is pretty static.
     *
     */


    public String getImage () {return mImagePath;}

    public String getTitle() {
        return mTitle;
    }

    public String getRelease() {
        return mRelease;
    }

    public Double getAverage() {
        return mAverage;
    }

    public String getPlot() {
        return mPlot;
    }

    public String getBackDropPath() {
        return mBackDropPath;
    }

    public int getID() {
        return mID;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    //Writing to parcel will allow us to recreate a custom Movie object, and allow us to pass to
    //a new activity.
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mImagePath);
        parcel.writeString(mTitle);
        parcel.writeString(mRelease);
        parcel.writeString(mPlot);
        parcel.writeString(mBackDropPath);
        parcel.writeDouble(mAverage);
        parcel.writeInt(mID);
        parcel.writeValue(isFavorite);
    }
}
