package com.example.libbys.movieposter.NetWorkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import com.example.libbys.movieposter.R;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class Network {
    //Below is a sample uri that will query the movie "Intersteller"
    //https://api.themoviedb.org/3/movie/157336?api_key=f99d0fed8256f83be4de458a1a371ef0&append_to_response=videos,reviews
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String QUERY_PARAM_API_KEY = "?api_key=";
    //allows you to pull up extra data about the movies for now we only care about viceo's anr reviews
    //some other usefull add ones for future implementations include "credits"
    private static final String APPEND_TO_RESPONSE = "&append_to_response=";
    //used with APPEND_TO_RESPONSE TO ADD VIDEOS and/or Reviews.
    private static final String ADD_VIDEOS = "videos";
    private static final String ADD_REVIEWS = "reviews";

    public static String getMovieList(String query, Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) return null;

        String API_KEY = context.getString(R.string.API);
        String urlString = BASE_URL + query + QUERY_PARAM_API_KEY + API_KEY;


        String jsonResponse = null;
        try {
            URL url = createUrl(urlString);
            jsonResponse = makeNetworkRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    private static String makeNetworkRequest(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL makeURLwithID(int id, Context context) {
        //https://api.themoviedb.org/3/movie/157336?api_key=f99d0fed8256f83be4de458a1a371ef0&append_to_response=videos,reviews
        String urltoquery = BASE_URL + "/" + id
                + QUERY_PARAM_API_KEY + context.getString(R.string.API)
                + APPEND_TO_RESPONSE + ADD_VIDEOS + "," + ADD_REVIEWS;
        return createUrl(urltoquery);
    }

    public static String queryMovie(int id, Context context) {
        String JSONResponse =null;
        try {
            JSONResponse = makeNetworkRequest(makeURLwithID(id,context));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONResponse;
    }

    private static URL createUrl(String toUrl) {
        Uri builtUri = Uri.parse(toUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.e(TAG, "createUrl: "+ url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}