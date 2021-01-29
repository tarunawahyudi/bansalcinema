package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieRepository;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.ReviewResponse;

public class ReviewViewModel extends ViewModel {
    private final MovieRepository mRepository;
    private final LiveData<ReviewResponse> mReviewResponse;

    public ReviewViewModel (MovieRepository repository, int movieId) {
        mRepository = repository;
        mReviewResponse = mRepository.getReviewResponse(movieId);
    }

    public LiveData<ReviewResponse> getReviewResponse() {
        return mReviewResponse;
    }
}
