package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info;

import android.app.Activity;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.FragmentInfoBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Cast;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Credits;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Crew;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.MovieDetails;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.FormatUtils;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.EXTRA_MOVIE;

public class InformationFragment extends Fragment {

    private FragmentInfoBinding mInfoBinding;

    OnInfoSelectedListener mCallback;

    public interface OnInfoSelectedListener {
        void onInformationSelected(MovieDetails movieDetails);
    }

    OnViewAllSelectedListener mViewAllCallback;

    public interface OnViewAllSelectedListener {
        void onViewAllSelected();
    }

    public static final String TAG = InformationFragment.class.getSimpleName();

    private Movie mMovie;

    private InfoViewModel mInfoViewModel;

    public InformationFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovie = getMovieData();

        setupViewModel(this.getActivity(), mMovie.getId());

        loadDetails();
    }

    private void setupViewModel(Context context, int movieId) {
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = new ViewModelProvider(this, factory).get(InfoViewModel.class);

        mInfoViewModel.getMovieDetails().observe(getViewLifecycleOwner(), new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    mCallback.onInformationSelected(movieDetails);
                    loadMovieDetailInfo(movieDetails);
                    loadCastCrew(movieDetails);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_info, container, false);
        View rootView = mInfoBinding.getRoot();

        mInfoBinding.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewAllCallback.onViewAllSelected();
            }
        });

        return rootView;
    }

    private Movie getMovieData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
    }

    private void loadCastCrew(MovieDetails movieDetails) {
        Credits credits = movieDetails.getCredits();
        List<Cast> castList = credits.getCast();
        List<String> castStrList = new ArrayList<>();
        for (int i = 0; i < castList.size(); i++) {
            Cast cast = castList.get(i);
            String castName = cast.getName();
            castStrList.add(castName);
        }

        Activity activity = getActivity();
        if (activity != null) {
            String castStr = TextUtils.join(getString(R.string.delimiter_comma), castStrList);
            mInfoBinding.tvCast.setText(castStr);

            List<Crew> crewList = credits.getCrew();
            for (int i = 0; i < crewList.size(); i++) {
                Crew crew = crewList.get(i);
                if (crew.getJob().equals(getString(R.string.director))) {
                    mInfoBinding.tvDirector.setText(crew.getName());
                    break;
                }
            }
        }
    }

    private void loadMovieDetailInfo(MovieDetails movieDetails) {
        int voteCount = movieDetails.getVoteCount();
        long budget = movieDetails.getBudget();
        long revenue = movieDetails.getRevenue();
        String status = movieDetails.getStatus();

        mInfoBinding.tvVoteCount.setText(FormatUtils.formatNumber(voteCount));
        mInfoBinding.tvBudget.setText(FormatUtils.formatCurrency(budget));
        mInfoBinding.tvRevenue.setText(FormatUtils.formatCurrency(revenue));
        mInfoBinding.tvStatus.setText(status);
    }

    private void loadDetails() {
        mInfoBinding.tvOverview.setText(mMovie.getOverview());
        mInfoBinding.tvVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
        mInfoBinding.tvOriginalTitle.setText(mMovie.getOriginalTitle());
        mInfoBinding.tvReleaseDate.setText(FormatUtils.formatDate(mMovie.getReleaseDate()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnInfoSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInfoSelectedListener");
        }

        try {
            mViewAllCallback = (OnViewAllSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewAllSelectedListener");
        }
    }
}
