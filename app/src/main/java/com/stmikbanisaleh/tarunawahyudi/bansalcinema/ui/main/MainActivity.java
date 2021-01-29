package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.GridSpacingItemDecoration;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.detail.DetailActivity;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.settings.SettingsActivity;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieEntry;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MoviePreferences;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.ActivityMainBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.InjectorUtils;

import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.DRAWABLES_ZERO;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.EXTRA_MOVIE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.GRID_INCLUDE_EDGE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.GRID_SPACING;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.GRID_SPAN_COUNT;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.REQUEST_CODE_DIALOG;

public class MainActivity extends AppCompatActivity implements
        FavoriteAdapter.FavoriteAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MoviePagedListAdapter mMoviePagedListAdapter;

    private FavoriteAdapter mFavoriteAdapter;

    private String mSortCriteria;

    private Parcelable mSavedLayoutState;

    private MainActivityViewModel mMainViewModel;

    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initAdapter();
        mMainBinding.navView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            showNetworkDialog(isOnline());
        }

        mSortCriteria = MoviePreferences.getPreferredSortCriteria(this);

        setupViewModel(mSortCriteria);
        updateUI(mSortCriteria);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        setSwipeRefreshLayout();

        setColumnSpacing();

        if (savedInstanceState != null) {
            mSavedLayoutState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
        }

    }

    private void initAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mMainBinding.rvMovie.setLayoutManager(layoutManager);

        mMainBinding.rvMovie.setHasFixedSize(true);

        mMoviePagedListAdapter = new MoviePagedListAdapter(this);
        mFavoriteAdapter = new FavoriteAdapter(this, this);
    }

    private void setupViewModel(String sortCriteria) {
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(
                MainActivity.this, sortCriteria);
        mMainViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);
    }

    private void updateUI(String sortCriteria) {
        mMainViewModel.setFavoriteMovies();

        if (sortCriteria.equals(getString(R.string.pref_sort_by_favorites))) {
            mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
            observeFavoriteMovies();
        } else {
            mMainBinding.rvMovie.setAdapter(mMoviePagedListAdapter);
            observeMoviePagedList();
        }
    }

    private void observeMoviePagedList() {
        mMainViewModel.getMoviePagedList().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                showMovieDataView();
                if (pagedList != null) {
                    mMoviePagedListAdapter.submitList(pagedList);

                    mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }

                if (!isOnline()) {
                    showMovieDataView();
                    showSnackbarOffline();
                }
            }
        });
    }

    private void observeFavoriteMovies() {
        mMainViewModel.getFavoriteMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                mFavoriteAdapter.setMovies(movieEntries);

                mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);

                if (movieEntries == null || movieEntries.size() == 0) {
                    showEmptyView();
                } else if(!isOnline()) {
                    showMovieDataView();
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            mSortCriteria = sharedPreferences.getString(key, getString(R.string.pref_sort_by_default));
        }

        mMainViewModel.setMoviePagedList(mSortCriteria);
        updateUI(mSortCriteria);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onItemClick(Movie movie) {
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, b);
        startActivity(intent);
    }

    @Override
    public void onFavItemClick(MovieEntry movieEntry) {
        int movieId = movieEntry.getMovieId();
        String originalTitle = movieEntry.getOriginalTitle();
        String title = movieEntry.getTitle();
        String posterPath = movieEntry.getPosterPath();
        String overview = movieEntry.getOverview();
        double voteAverage = movieEntry.getVoteAverage();
        String releaseDate = movieEntry.getReleaseDate();
        String backdropPath = movieEntry.getBackdropPath();

        Movie movie = new Movie(movieId, originalTitle, title, posterPath, overview,
                voteAverage, releaseDate, backdropPath);

        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, b);
        startActivity(intent);
    }

    private void setSwipeRefreshLayout() {
        mMainBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mMainBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                showMovieDataView();

                updateUI(mSortCriteria);

                hideRefresh();

                showSnackbarRefresh(isOnline());
            }
        });
    }

    private void showSnackbarRefresh(boolean isOnline) {
        if (isOnline) {
            Snackbar.make(mMainBinding.rvMovie, getString(R.string.snackbar_updated)
                    , Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showNetworkDialog(final boolean isOnline) {
        if (!isOnline) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(getString(R.string.no_network_title));
            builder.setMessage(getString(R.string.no_network_message));
            builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DIALOG);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showMovieDataView() {
        mMainBinding.tvEmpty.setVisibility(View.INVISIBLE);
        mMainBinding.rvMovie.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mMainBinding.tvEmpty.setVisibility(View.VISIBLE);
        mMainBinding.tvEmpty.setText(getString(R.string.message_empty_favorites));
        mMainBinding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(DRAWABLES_ZERO,
                R.drawable.film, DRAWABLES_ZERO, DRAWABLES_ZERO);
        mMainBinding.tvEmpty.setTextColor(Color.WHITE);
    }

    private void showSnackbarOffline() {
        Snackbar snackbar = Snackbar.make(
                mMainBinding.frameMain, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void setColumnSpacing() {
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(
                GRID_SPAN_COUNT, GRID_SPACING, GRID_INCLUDE_EDGE);
        mMainBinding.rvMovie.addItemDecoration(decoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAYOUT_MANAGER_STATE,
                mMainBinding.rvMovie.getLayoutManager().onSaveInstanceState());
    }

    private void hideRefresh() {
        mMainBinding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.home:
                updateUI(mSortCriteria);
                break;
            case R.id.favorite:
                mMainViewModel.setFavoriteMovies();
                mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
                observeFavoriteMovies();
                break;
            case R.id.sort:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
        }

        return true;
    }
}


