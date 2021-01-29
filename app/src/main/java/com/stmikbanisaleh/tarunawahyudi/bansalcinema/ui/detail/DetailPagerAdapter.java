package com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.detail;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.cast.CastFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.info.InformationFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.review.ReviewFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.ui.trailer.TrailerFragment;
import com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.CAST;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.INFORMATION;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.REVIEWS;
import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.TRAILERS;

public class DetailPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public DetailPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INFORMATION:
                return new InformationFragment();
            case TRAILERS:
                return new TrailerFragment();
            case CAST:
                return new CastFragment();
            case REVIEWS:
                return new ReviewFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return Constant.PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Constant.TAP_TITLE[position % Constant.PAGE_COUNT].toUpperCase();
    }
}
