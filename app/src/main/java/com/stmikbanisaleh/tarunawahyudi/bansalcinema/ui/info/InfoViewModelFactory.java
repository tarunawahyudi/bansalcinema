package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;

public class InfoViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepository;
    private final int mMovieId;

    public InfoViewModelFactory(MovieRepository repository, int movieId) {
        this.mRepository = repository;
        this.mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new InfoViewModel(mRepository, mMovieId);
    }
}
