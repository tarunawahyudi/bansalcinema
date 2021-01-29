package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.cast;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.databinding.CastListItemBinding;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.model.Cast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_BASE_URL;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.IMAGE_FILE_SIZE;

/**
 * {@link CastAdapter} exposes a list of casts to a {@link RecyclerView}
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    /** Member variable for the list of {@link Cast}s */
    private List<Cast> mCasts;

    /**
     * Constructor for CastAdapter that accepts a list of casts to display
     *
     * @param casts The list of {@link Cast}s
     */
    public CastAdapter(List<Cast> casts) {
        mCasts = casts;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        CastListItemBinding castItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.cast_list_item, viewGroup, false);
        return new CastViewHolder(castItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = mCasts.get(position);
        holder.bind(cast);
    }

    @Override
    public int getItemCount() {
        if (null == mCasts) return 0;
        return mCasts.size();
    }

    public void addAll(List<Cast> casts) {
        mCasts.clear();
        mCasts.addAll(casts);
        notifyDataSetChanged();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        /** This field is used for data binding */
        CastListItemBinding mCastItemBinding;

        CastViewHolder(CastListItemBinding castItemBinding) {
            super(castItemBinding.getRoot());
            mCastItemBinding = castItemBinding;
        }

         void bind(Cast cast) {
            // The complete profile image url
            String profile = IMAGE_BASE_URL + IMAGE_FILE_SIZE + cast.getProfilePath();
            // Load image with Picasso library
            Picasso.with(itemView.getContext())
                    .load(profile)
                    // Create circular avatars
                    // Reference: @see "https://stackoverflow.com/questions/26112150/android-create
                    // -circular-image-with-picasso"
                    .into(mCastItemBinding.ivCast, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) mCastItemBinding.ivCast.getDrawable())
                                    .getBitmap();
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                    itemView.getContext().getResources(), // to determine density
                                    imageBitmap); // image to round
                            drawable.setCircular(true);
                            mCastItemBinding.ivCast.setImageDrawable(drawable);
                        }

                        @Override
                        public void onError() {
                            mCastItemBinding.ivCast.setImageResource(R.drawable.account_circle);
                        }
                    });

            // Set the cast name and character name to the TextViews
            mCastItemBinding.setCast(cast);
        }
    }
}
