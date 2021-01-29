package com.stmikbanisaleh.tarunawahyudi.bansalcinema.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.stmikbanisaleh.tarunawahyudi.bansalcinema.R;

public class MoviePreferences {

    public static String getPreferredSortCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortBy = context.getString(R.string.pref_sort_by_key);
        String defaultSortBy = context.getString(R.string.pref_sort_by_default);
        return prefs.getString(keyForSortBy, defaultSortBy);
    }

}
