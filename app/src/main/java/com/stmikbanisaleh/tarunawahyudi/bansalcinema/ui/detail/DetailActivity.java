package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.detail;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.AppExecutors;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieDatabase;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieEntry;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.ActivityDetailBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info.InformationFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.trailer.TrailerFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Genre;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.MovieDetails;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Video;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.FormatUtils;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.InjectorUtils;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main.FavViewModel;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main.FavViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.BACKDROP_FILE_SIZE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.CAST;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.EXTRA_MOVIE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_BASE_URL;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.RELEASE_YEAR_BEGIN_INDEX;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.RELEASE_YEAR_END_INDEX;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.RESULTS_GENRE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.RESULTS_RELEASE_YEAR;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.RESULTS_RUNTIME;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.SHARE_INTENT_TYPE_TEXT;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.SHARE_URL;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.YOUTUBE_BASE_URL;

public class DetailActivity extends AppCompatActivity implements
        InformationFragment.OnInfoSelectedListener, TrailerFragment.OnTrailerSelectedListener,
        InformationFragment.OnViewAllSelectedListener {

    /** Tag for logging */
    public static final String TAG = DetailActivity.class.getSimpleName();

    /** ViewModel for Favorites */
    private FavViewModel mFavViewModel;

    /** True when the movie is in favorites collection, otherwise false */
    private boolean mIsInFavorites;

    /** Member variable for the MovieDatabase*/
    private MovieDatabase mDb;

    /** Member variable for the MovieEntry */
    private MovieEntry mMovieEntry;

    /** Movie object */
    private Movie mMovie;

    /** This field is used for data binding */
    private ActivityDetailBinding mDetailBinding;

    /** The first trailer's YouTube URL */
    private String mFirstVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        mDb = MovieDatabase.getInstance(getApplicationContext());
        mIsInFavorites = isInFavoritesCollection();

        setupUI();

        if (savedInstanceState != null) {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);

            String resultRuntime = savedInstanceState.getString(RESULTS_RUNTIME);
            String resultReleaseYear = savedInstanceState.getString(RESULTS_RELEASE_YEAR);
            String resultGenre = savedInstanceState.getString(RESULTS_GENRE);

            mDetailBinding.tvRuntime.setText(resultRuntime);
            mDetailBinding.tvReleaseYear.setText(resultReleaseYear);
            mDetailBinding.tvGenre.setText(resultGenre);
        }
    }

    private void setupUI() {
        // Show the up button in Collapsing Toolbar
        showUpButton();

        // Give the TabLayout the ViewPager
        mDetailBinding.tabLayout.setupWithViewPager(mDetailBinding.contentDetail.viewpager);
        // Set gravity for the TabLayout
        mDetailBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create an adapter that knows which fragment should be shown on each page
        DetailPagerAdapter pagerAdapter = new DetailPagerAdapter(
                this, getSupportFragmentManager());
        // Set the adapter onto the ViewPager
        mDetailBinding.contentDetail.viewpager.setAdapter(pagerAdapter);

        // Show the title in the app bar when a CollapsingToolbarLayout is fully collapsed
        setCollapsingToolbarTitle();
        // Display the backdrop image
        loadBackdropImage();
        // Display title
        setTitle();

        // When it is online, show loading indicator, otherwise hide loading indicator.
        showLoading(isOnline());
        // When offline, show runtime, release year, and genre of the movie
        if (!isOnline()) {
            loadMovieDetailData();
        }
    }

    public void onFavoriteClick(View view) {
        // Create a new MovieEntry
        mMovieEntry = getMovieEntry();

        if (!mIsInFavorites) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert a movie to the MovieDatabase by using the movieDao
                    mDb.movieDao().insertMovie(mMovieEntry);
                }
            });

            // Show snack bar message "Added to your favorites collection"
            showSnackbarAdded();
        } else {
            mMovieEntry = mFavViewModel.getMovieEntry().getValue();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Delete a movie from the MovieDatabase by using the movieDao
                    mDb.movieDao().deleteMovie(mMovieEntry);
                }
            });

            // Show snack bar message "Removed from your favorites collection"
            showSnackbarRemoved();
        }
    }

    /**
     * Returns a MovieEntry
     */
    private MovieEntry getMovieEntry() {
        String runtime = mDetailBinding.tvRuntime.getText().toString();
        String releaseYear = mDetailBinding.tvReleaseYear.getText().toString();
        String genre = mDetailBinding.tvGenre.getText().toString();

        // Create a MovieEntry
        mMovieEntry = new MovieEntry(mMovie.getId(), mMovie.getOriginalTitle(), mMovie.getTitle(),
                mMovie.getPosterPath(), mMovie.getOverview(), mMovie.getVoteAverage(),
                mMovie.getReleaseDate(), mMovie.getBackdropPath(), new Date(),
                runtime, releaseYear, genre);

        return mMovieEntry;
    }

    /**
     * When offline, display runtime, release year, and genre of the movie.
     */
    private void loadMovieDetailData() {
        FavViewModelFactory factory = InjectorUtils.provideFavViewModelFactory(
                DetailActivity.this, mMovie.getId());
        mFavViewModel = new ViewModelProvider(this, factory).get(FavViewModel.class);

        mFavViewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                if (movieEntry != null) {
                    mDetailBinding.tvRuntime.setText(movieEntry.getRuntime());
                    mDetailBinding.tvReleaseYear.setText(movieEntry.getReleaseYear());
                    mDetailBinding.tvGenre.setText(movieEntry.getGenre());
                }
            }
        });
    }

    private boolean isInFavoritesCollection() {

        FavViewModelFactory factory = InjectorUtils.provideFavViewModelFactory(
                DetailActivity.this, mMovie.getId());
        mFavViewModel = new ViewModelProvider(this, factory).get(FavViewModel.class);

        mFavViewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                if (mFavViewModel.getMovieEntry().getValue() == null) {
                    mDetailBinding.fab.setImageResource(R.drawable.favorite_border);
                    mIsInFavorites = false;
                } else {
                    mDetailBinding.fab.setImageResource(R.drawable.favorite);
                    mIsInFavorites = true;
                }
            }
        });
        return mIsInFavorites;
    }

    private void showSnackbarAdded() {
        Snackbar snackbar = Snackbar.make(
                mDetailBinding.coordinator, R.string.snackbar_added, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void showSnackbarRemoved() {
        Snackbar snackbar = Snackbar.make(
                mDetailBinding.coordinator, R.string.snackbar_removed, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        // Set background color of the snackbar
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    @Override
    public void onTrailerSelected(final Video video) {
        mDetailBinding.ivPlayCircle.setVisibility(View.VISIBLE);

        String firstVideoKey = video.getKey();
        mFirstVideoUrl = YOUTUBE_BASE_URL + firstVideoKey;

        mDetailBinding.ivPlayCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTrailer(mFirstVideoUrl);
            }
        });
    }

    private void launchTrailer(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showUpButton() {
        setSupportActionBar(mDetailBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void loadBackdropImage() {
        // Get the backdrop path
        String backdropPath = mMovie.getBackdropPath();
        // The complete backdrop image url
        String backdrop = IMAGE_BASE_URL + BACKDROP_FILE_SIZE + backdropPath;
        // Load image with Picasso library
        Picasso.with(this)
                .load(backdrop)
                .error(R.drawable.photo)
                .into(mDetailBinding.ivBackdrop);
    }

    private void setTitle() {
        // Get title of the movie
        String title = mMovie.getTitle();
        // Set title to the TextView
        mDetailBinding.tvDetailTitle.setText(title);
    }

    private void showReleaseYear() {
        // Get the release date of the movie (e.g. "2018-06-20")
        String releaseDate = mMovie.getReleaseDate();
        // Get the release year (e.g. "2018")
        String releaseYear = releaseDate.substring(RELEASE_YEAR_BEGIN_INDEX, RELEASE_YEAR_END_INDEX);
        // Set the release year to the TextView
        mDetailBinding.tvReleaseYear.setText(releaseYear);
    }

    private void setCollapsingToolbarTitle() {
        // Set onOffsetChangedListener to determine when CollapsingToolbar is collapsed
        mDetailBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    // Show title when a CollapsingToolbarLayout is fully collapse
                    mDetailBinding.collapsingToolbarLayout.setTitle(mMovie.getTitle());
                    isShow = true;
                } else if (isShow) {
                    // Otherwise hide the title
                    mDetailBinding.collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onInformationSelected(MovieDetails movieDetails) {
        // Hide the loading indicator
        mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);

        // As soon as the loading indicator is gone, show release year
        showReleaseYear();

        // Get the runtime of the movie from MovieDetails object
        int runtime = movieDetails.getRuntime();
        // Convert Minutes to Hours and Minutes (e.g. "118" -> "1h 58m") and set the runtime to the TextView
        mDetailBinding.tvRuntime.setText(FormatUtils.formatTime(this, runtime));

        // Get the genre of the movie from MovieDetails
        List<Genre> genres = movieDetails.getGenres();
        // Create an empty arrayList
        List<String> genresStrList = new ArrayList<>();
        // Iterate through the list of genres, and add genre name to the list of strings
        for (int i = 0; i < genres.size(); i++) {
            Genre genre = genres.get(i);
            // Get the genre name from the genre at ith position
            String genreName = genre.getGenreName();
            // Add genre name to the list of strings
            genresStrList.add(genreName);
        }
        // Join a string using a delimiter
        String genreStr = TextUtils.join(getString(R.string.delimiter_comma), genresStrList);
        // Display the genre
        mDetailBinding.tvGenre.setText(genreStr);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_share:
                // Share movie information using share intent
                startActivity(createShareIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent createShareIntent() {
        // Text message to share
        String shareText = getString(R.string.check_out) + mMovie.getTitle()
                + getString(R.string.new_line) + SHARE_URL + mMovie.getId();
        // If there is the first trailer, add the first trailer's YouTube URL to the text message
        if (mFirstVideoUrl != null) {
            shareText += getString(R.string.new_line) + getString(R.string.youtube_trailer)
                    + mFirstVideoUrl;
        }

        // Create share intent
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType(SHARE_INTENT_TYPE_TEXT)
                .setText(shareText)
                .setChooserTitle(getString(R.string.chooser_title))
                .createChooserIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String resultRuntime = mDetailBinding.tvRuntime.getText().toString();
        outState.putString(RESULTS_RUNTIME, resultRuntime);

        String resultReleaseYear = mDetailBinding.tvReleaseYear.getText().toString();
        outState.putString(RESULTS_RELEASE_YEAR, resultReleaseYear);

        String resultGenre = mDetailBinding.tvGenre.getText().toString();
        outState.putString(RESULTS_GENRE, resultGenre);
    }

    @Override
    public void onViewAllSelected() {
        mDetailBinding.contentDetail.viewpager.setCurrentItem(CAST);
    }

    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showLoading(boolean isOnline) {
        if (!isOnline) {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.GONE);
        } else {
            mDetailBinding.pbDetailLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }
}
