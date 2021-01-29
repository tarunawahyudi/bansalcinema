package com.stmikbanisaleh.tarunawahyudi.bansalcinema.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    @SerializedName("page")
    private int mPage;

    @SerializedName("total_results")
    private int mTotalResults;

    @SerializedName("total_pages")
    private int mTotalPages;

    @SerializedName("results")
    private List<Movie> mMovieResults = null;

    @SuppressWarnings({"unused", "used by Retrofit"})
    public MovieResponse() {
    }

    public List<Movie> getMovieResults() {
        return mMovieResults;
    }
}
