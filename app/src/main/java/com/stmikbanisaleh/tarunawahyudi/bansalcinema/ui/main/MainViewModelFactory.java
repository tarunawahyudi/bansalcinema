package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepository;
    private final String mSortCriteria;

    public MainViewModelFactory (MovieRepository repository, String sortCriteria) {
        this.mRepository = repository;
        this.mSortCriteria = sortCriteria;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainActivityViewModel(mRepository, mSortCriteria);
    }
}
