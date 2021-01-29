package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieDataSourceFactory;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieEntry;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.INITIAL_LOAD_SIZE_HINT;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.NUMBER_OF_FIXED_THREADS_FIVE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.PAGE_SIZE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.PREFETCH_DISTANCE;

public class MainActivityViewModel extends ViewModel {

    private final MovieRepository mRepository;

    private LiveData<PagedList<Movie>> mMoviePagedList;
    private LiveData<List<MovieEntry>> mFavoriteMovies;
    private String mSortCriteria;

    public MainActivityViewModel(MovieRepository repository, String sortCriteria) {
        mRepository = repository;
        mSortCriteria = sortCriteria;
        init(sortCriteria);
    }

    private void init(String sortCriteria) {
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_FIXED_THREADS_FIVE);

        MovieDataSourceFactory movieDataFactory = new MovieDataSourceFactory(sortCriteria);

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .build();

        mMoviePagedList = new LivePagedListBuilder<>(movieDataFactory, config)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<PagedList<Movie>> getMoviePagedList() {
        return mMoviePagedList;
    }

    public void setMoviePagedList(String sortCriteria) {
        init(sortCriteria);
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public void setFavoriteMovies() {
        mFavoriteMovies = mRepository.getFavoriteMovies();
    }
}
