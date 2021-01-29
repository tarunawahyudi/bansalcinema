package com.stmikbanisaleh.tarunawahyudi.bansalcinema.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.AppExecutors;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.MovieDetails;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.ReviewResponse;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.VideoResponse;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.TheMovieApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.API_KEY;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.CREDITS;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.LANGUAGE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.PAGE;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final MovieDao mMovieDao;
    private final TheMovieApi mTheMovieApi;
    private final AppExecutors mExecutors;

    private MovieRepository(MovieDao movieDao,
                            TheMovieApi theMovieApi,
                            AppExecutors executors) {
        mMovieDao = movieDao;
        mTheMovieApi = theMovieApi;
        mExecutors = executors;
    }

    public synchronized static MovieRepository getInstance(
            MovieDao movieDao, TheMovieApi theMovieApi, AppExecutors executors) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Making new repository");
                sInstance = new MovieRepository(movieDao, theMovieApi, executors);
            }
        }
        return sInstance;
    }

    public LiveData<MovieDetails> getMovieDetails(int movieId) {
        final MutableLiveData<MovieDetails> movieDetailsData = new MutableLiveData<>();

        mTheMovieApi.getDetails(movieId, API_KEY, LANGUAGE, CREDITS)
                .enqueue(new Callback<MovieDetails>() {
                    @Override
                    public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                        if (response.isSuccessful()) {
                            MovieDetails movieDetails = response.body();
                            movieDetailsData.setValue(movieDetails);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetails> call, Throwable t) {
                        movieDetailsData.setValue(null);
                        Log.e(TAG, "Failed getting MovieDetails: " + t.getMessage());
                    }
                });
        return movieDetailsData;
    }

    public LiveData<ReviewResponse> getReviewResponse(int movieId) {
        final MutableLiveData<ReviewResponse> reviewResponseData = new MutableLiveData<>();

        mTheMovieApi.getReviews(movieId, API_KEY, LANGUAGE, PAGE)
                .enqueue(new Callback<ReviewResponse>() {
                    @Override
                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                        if (response.isSuccessful()) {
                            ReviewResponse reviewResponse = response.body();
                            reviewResponseData.setValue(reviewResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewResponse> call, Throwable t) {
                        reviewResponseData.setValue(null);
                        Log.e(TAG, "Failed getting ReviewResponse: " + t.getMessage());
                    }
                });
        return reviewResponseData;
    }

    public LiveData<VideoResponse> getVideoResponse(int movieId) {
        final MutableLiveData<VideoResponse> videoResponseData = new MutableLiveData<>();

        mTheMovieApi.getVideos(movieId, API_KEY, LANGUAGE)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (response.isSuccessful()) {
                            VideoResponse videoResponse = response.body();
                            if (videoResponse != null) {
                                videoResponseData.setValue(videoResponse);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        videoResponseData.setValue(null);
                        Log.e(TAG, "Failed getting VideoResponse: " + t.getMessage());
                    }
                });
        return videoResponseData;
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return mMovieDao.loadAllMovies();
    }

    public LiveData<MovieEntry> getFavoriteMovieByMovieId(int movieId) {
        return mMovieDao.loadMovieByMovieId(movieId);
    }
}
