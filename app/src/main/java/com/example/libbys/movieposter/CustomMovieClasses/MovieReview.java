package com.example.libbys.movieposter.CustomMovieClasses;


import android.os.Parcel;
import android.os.Parcelable;

/**This class holds Movie reviews that we will display on the screen
As of now they will only be created and accessed in the DetailActivity which is specific to one movie.
 so no need to reference the movie. */

public class MovieReview implements Parcelable {
    private final String mUser;
    private final String mReview;

    public MovieReview(String user,String review) {
        mUser = user;
        mReview = review;
    }


    private MovieReview(Parcel in) {
        mUser = in.readString();
        mReview = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getmUser() {
        return mUser;
    }

    public String getmReview() {
        return mReview;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReview);
        dest.writeString(mUser);

    }
}
