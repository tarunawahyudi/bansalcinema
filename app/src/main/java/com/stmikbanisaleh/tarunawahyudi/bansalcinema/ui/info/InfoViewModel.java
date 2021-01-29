package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.MovieDetails;

public class InfoViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private final LiveData<MovieDetails> mMovieDetails;

    public InfoViewModel (MovieRepository repository, int movieId) {
        mRepository = repository;
        mMovieDetails = mRepository.getMovieDetails(movieId);
    }

    public LiveData<MovieDetails> getMovieDetails() {
        return mMovieDetails;
    }
}
