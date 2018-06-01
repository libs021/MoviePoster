package com.example.libbys.movieposter.dataBaseFiles;

import android.net.Uri;
import android.provider.BaseColumns;

public class movieContract {

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";
    public static final String CONTENT_AUTHORITY = "com.example.libbys.movieposter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static class movieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_IMAGEPATH = "imagePath";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASEDATE = "releaseDate";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_BACKDROPPATH = "backdropPath";
        public static final String COLUMN_ID = BaseColumns._ID;
    }

    public static class reviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_MOVIEID = "movieID";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_REVIEW = "review";
    }

    public static class videoEntry implements BaseColumns {
        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_MOVIEID = "movieID";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "url";

    }
}
