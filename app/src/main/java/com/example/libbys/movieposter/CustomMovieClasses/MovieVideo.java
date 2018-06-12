package com.example.libbys.movieposter.CustomMovieClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieVideo implements Parcelable {

    private final String mdescription;
    private final String murl;

    private MovieVideo(Parcel in) {
        mdescription = in.readString();
        murl = in.readString();
    }

    public static final Creator<MovieVideo> CREATOR = new Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel in) {
            return new MovieVideo(in);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };

    public String getdescription() {
        return mdescription;
    }

    public String geturl() {
        return murl;
    }

    public MovieVideo(String description, String url) {
        mdescription = description;
        murl = url;

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
        dest.writeString(mdescription);
        dest.writeString(murl);
    }
}
