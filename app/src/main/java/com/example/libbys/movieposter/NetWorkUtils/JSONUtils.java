package com.example.libbys.movieposter.NetWorkUtils;

import com.example.libbys.movieposter.CustomMovieClasses.Movie;
import com.example.libbys.movieposter.CustomMovieClasses.MovieReview;
import com.example.libbys.movieposter.CustomMovieClasses.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    public static ArrayList<Movie> parseMovieListJSON(String JSONData) {
        ArrayList<Movie> movies = new ArrayList<>();
        String name;
        String plot;
        String release;
        Double averageVote;
        String posterImage;

        String backDropImage;
        int id;

        try {
            JSONObject JSON = new JSONObject(JSONData);
            JSONArray rawData = JSON.getJSONArray("results");
            for (int i =0; i<rawData.length(); i++)
            {
                JSONObject values = rawData.getJSONObject(i);
                name = values.getString("title");
                plot = values.getString("overview");
                release = values.getString("release_date");
                averageVote = values.getDouble("vote_average");
                posterImage = values.getString("poster_path");
                backDropImage = values.getString("backdrop_path");
                String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342/";
                posterImage = POSTER_BASE_URL + posterImage;
                String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w1280";
                backDropImage = BACKDROP_BASE_URL + backDropImage;
                id = values.getInt("id");

                movies.add(new Movie(posterImage,name,release,averageVote,plot,backDropImage,id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static ArrayList<MovieReview> parseJsonMovieReviews(String JSON) {
        ArrayList<MovieReview> reviews = new ArrayList<>();
        String user;
        String review;
        try {
            JSONObject object = new JSONObject(JSON);
            object = object.getJSONObject("reviews");
            JSONArray array = object.getJSONArray("results");
            for (int i=0; i<array.length(); i++) {
                JSONObject reviewObj = array.getJSONObject(i);
                user = reviewObj.getString("author");
                review = reviewObj.getString("content");
                reviews.add(new MovieReview(user,review));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public static ArrayList<MovieVideo> parseJsonMovieVideos(String JSON){
        String Baseurl = "http://youtube.com/watch?v=";
        ArrayList<MovieVideo> videos = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(JSON);
            object = object.getJSONObject("videos");
            JSONArray array = object.getJSONArray("results");
            for (int i=0; i<array.length(); i++) {
                JSONObject videoObj = array.getJSONObject(i);
                String url = Baseurl + videoObj.getString("key");
                String description = videoObj.getString("name");
                videos.add(new MovieVideo(description,url));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videos;

    }

}
