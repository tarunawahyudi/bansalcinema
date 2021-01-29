package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.main;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.AppExecutors;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieDatabase;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.data.MovieEntry;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.FavListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.DELETE;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.DELETE_GROUP_ID;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.DELETE_ORDER;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_BASE_URL;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_FILE_SIZE;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<MovieEntry> mMovieEntries;

    private Context mContext;

    private final FavoriteAdapterOnClickHandler mOnClickHandler;

    public interface FavoriteAdapterOnClickHandler {
        void onFavItemClick(MovieEntry movieEntry);
    }

    public FavoriteAdapter(Context context, FavoriteAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        FavListItemBinding favItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.fav_list_item, parent, false);
        return new FavoriteViewHolder(favItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MovieEntry movieEntry = mMovieEntries.get(position);
        holder.bind(movieEntry);
    }

    @Override
    public int getItemCount() {
        if (mMovieEntries == null) return 0;
        return mMovieEntries.size();
    }

    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntries = movieEntries;
        notifyDataSetChanged();
    }

    public List<MovieEntry> getMovies() {
        return mMovieEntries;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        FavListItemBinding mFavItemBinding;

        public FavoriteViewHolder(FavListItemBinding favItemBinding) {
            super(favItemBinding.getRoot());

            mFavItemBinding = favItemBinding;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        void bind(MovieEntry movieEntry) {
            String thumbnail = IMAGE_BASE_URL + IMAGE_FILE_SIZE + movieEntry.getPosterPath();

            Picasso.with(itemView.getContext())
                    .load(thumbnail)
                    .into(mFavItemBinding.ivThumbnail);

            mFavItemBinding.tvTitle.setText(movieEntry.getTitle());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieEntry movieEntry = mMovieEntries.get(adapterPosition);
            mOnClickHandler.onFavItemClick(movieEntry);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int adapterPosition = getAdapterPosition();
            MenuItem item = menu.add(DELETE_GROUP_ID, adapterPosition, DELETE_ORDER, v.getContext().getString(R.string.action_delete));
            item.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getTitle().toString()) {
                case DELETE:
                    int adapterPosition = item.getItemId();
                    MovieEntry movieEntry = mMovieEntries.get(adapterPosition);
                    delete(movieEntry);
                    return true;
                default:
                    return false;
            }
        }

        private void delete(final MovieEntry movieEntry) {
            final MovieDatabase db = MovieDatabase.getInstance(mContext);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.movieDao().deleteMovie(movieEntry);
                }
            });
        }
    }
}
