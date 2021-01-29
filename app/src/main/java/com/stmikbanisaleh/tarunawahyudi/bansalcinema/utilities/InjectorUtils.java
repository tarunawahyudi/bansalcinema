package com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities;

import android.content.Context;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.AppExecutors;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieDatabase;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main.FavViewModelFactory;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info.InfoViewModelFactory;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main.MainViewModelFactory;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.review.ReviewViewModelFactory;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.trailer.TrailerViewModelFactory;

public class InjectorUtils {

    public static MovieRepository provideRepository(Context context) {
        MovieDatabase database = MovieDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        TheMovieApi theMovieApi = Controller.getClient().create(TheMovieApi.class);
        return MovieRepository.getInstance(database.movieDao(), theMovieApi, executors);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context, String sortCriteria) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository, sortCriteria);
    }

    public static InfoViewModelFactory provideInfoViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new InfoViewModelFactory(repository, movieId);
    }

    public static ReviewViewModelFactory provideReviewViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new ReviewViewModelFactory(repository, movieId);
    }

    public static TrailerViewModelFactory provideTrailerViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new TrailerViewModelFactory(repository, movieId);
    }

    public static FavViewModelFactory provideFavViewModelFactory(Context context, int movieId) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new FavViewModelFactory(repository, movieId);
    }
}
