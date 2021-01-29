package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main;

import androidx.paging.PagedListAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.MovieListItemBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Movie;
import com.squareup.picasso.Picasso;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_BASE_URL;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_FILE_SIZE;

public class MoviePagedListAdapter extends PagedListAdapter<Movie, MoviePagedListAdapter.MoviePagedViewHolder> {

    private final MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler mOnClickHandler;

    public interface MoviePagedListAdapterOnClickHandler {
        void onItemClick(Movie movie);
    }


    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public MoviePagedListAdapter(MoviePagedListAdapterOnClickHandler onClickHandler) {
        super(MoviePagedListAdapter.DIFF_CALLBACK);
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public MoviePagedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MovieListItemBinding mMovieItemBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.movie_list_item, parent, false);

        return new MoviePagedViewHolder(mMovieItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePagedViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class MoviePagedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MovieListItemBinding mMovieItemBinding;

        public MoviePagedViewHolder(MovieListItemBinding movieItemBinding) {
            super(movieItemBinding.getRoot());
            mMovieItemBinding = movieItemBinding;
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movie.getPosterPath();

            Picasso.with(itemView.getContext())
                    .load(thumbnail)
                    .error(R.drawable.image)
                    .into(mMovieItemBinding.ivThumbnail);

            mMovieItemBinding.tvTitle.setText(movie.getTitle());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = getItem(adapterPosition);
            mOnClickHandler.onItemClick(movie);
        }
    }
}
