package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.cast;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.FragmentCastBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Cast;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Credits;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.MovieDetails;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.InjectorUtils;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info.InfoViewModel;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info.InfoViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.EXTRA_MOVIE;

public class CastFragment extends Fragment {

    public static final String TAG = CastFragment.class.getSimpleName();

    private List<Cast> mCastList;

    private CastAdapter mCastAdapter;

    private FragmentCastBinding mCastBinding;

    private Movie mMovie;

    private InfoViewModel mInfoViewModel;

    public CastFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCastBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cast, container, false);
        View rootView = mCastBinding.getRoot();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mCastBinding.rvCast.setLayoutManager(layoutManager);
        mCastBinding.rvCast.setHasFixedSize(true);

        // Create an empty ArrayList
        mCastList = new ArrayList<>();

        // The CastAdapter is responsible for displaying each item in the list.
        mCastAdapter = new CastAdapter(mCastList);
        // Set Adapter on RecyclerView
        mCastBinding.rvCast.setAdapter(mCastAdapter);

        // Show a message when offline
        showOfflineMessage(isOnline());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get movie data from the MainActivity
        mMovie = getMovieData();

        // Observe the data and update the UI
        setupViewModel(this.getActivity(), mMovie.getId());
    }

    private void setupViewModel(Context context, int movieId) {
        // Get the ViewModel from the factory
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = new ViewModelProvider(this, factory).get(InfoViewModel.class);

        // Retrieve live data object using the getMovieDetails() method from the ViewModel
        mInfoViewModel.getMovieDetails().observe(getViewLifecycleOwner(), new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    // Display cast of the movie
                    loadCast(movieDetails);
                }
            }
        });
    }

    /**
     * Gets movie data from the MainActivity.
     */
    private Movie getMovieData() {
        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
    }

    /**
     * Display the cast of the movie
     */
    private void loadCast(MovieDetails movieDetails) {
        // Get Credits from the MovieDetails
        Credits credits = movieDetails.getCredits();
        // Get the list of casts
        mCastList = credits.getCast();
        // Set the list of casts
        credits.setCast(mCastList);
        // Add a list of casts to CastAdapter
        mCastAdapter.addAll(mCastList);
    }


    /**
     * Make the offline message visible and hide the cast View when offline
     *
     * @param isOnline True when connected to the network
     */
    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            // First, hide the offline message
            mCastBinding.tvOffline.setVisibility(View.INVISIBLE);
            // Then, make sure the cast data is visible
            mCastBinding.rvCast.setVisibility(View.VISIBLE);
        } else {
            // First, hide the currently visible data
            mCastBinding.rvCast.setVisibility(View.INVISIBLE);
            // Then, show an offline message
            mCastBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if there is the network connectivity
     *
     * @return true if connected to the network
     */
    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
