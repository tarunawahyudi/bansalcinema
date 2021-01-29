package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.review;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.FragmentReviewBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Review;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.ReviewResponse;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.EXTRA_MOVIE;

public class ReviewFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = ReviewFragment.class.getSimpleName();

    private List<Review> mReviews;

    private FragmentReviewBinding mReviewBinding;

    private ReviewAdapter mReviewAdapter;

    private Movie mMovie;

    private ReviewViewModel mReviewViewModel;

    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        setupViewModel(this.getActivity());
    }

    private void setupViewModel(Context context) {
        ReviewViewModelFactory factory = InjectorUtils.provideReviewViewModelFactory(context, mMovie.getId());
        mReviewViewModel = new ViewModelProvider(this, factory).get(ReviewViewModel.class);

        mReviewViewModel.getReviewResponse().observe(getViewLifecycleOwner(), new Observer<ReviewResponse>() {
            @Override
            public void onChanged(@Nullable ReviewResponse reviewResponse) {
                if (reviewResponse != null) {
                    mReviews = reviewResponse.getReviewResults();
                    reviewResponse.setReviewResults(mReviews);
                    if (!mReviews.isEmpty()) {
                        mReviewAdapter.addAll(mReviews);
                    } else {
                        showNoReviewsMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mReviewBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_review, container, false);
        View rootView = mReviewBinding.getRoot();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mReviewBinding.rvReview.setLayoutManager(layoutManager);
        mReviewBinding.rvReview.setHasFixedSize(true);

        mReviews = new ArrayList<>();

        mReviewAdapter = new ReviewAdapter(mReviews, this);
        mReviewBinding.rvReview.setAdapter(mReviewAdapter);

        showOfflineMessage(isOnline());

        return rootView;
    }

    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showNoReviewsMessage() {
        mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
        mReviewBinding.tvNoReviews.setVisibility(View.VISIBLE);
    }

    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            mReviewBinding.tvOffline.setVisibility(View.INVISIBLE);
            mReviewBinding.rvReview.setVisibility(View.VISIBLE);
        } else {
            mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
            mReviewBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
