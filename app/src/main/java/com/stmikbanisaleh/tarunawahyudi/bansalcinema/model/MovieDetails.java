package com.stmikbanisaleh.tarunawahyudi.bansalcinema.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetails implements Parcelable {

    @SerializedName("budget")
    private long mBudget;

    @SerializedName("genres")
    private List<Genre> mGenres = null;

    @SerializedName("runtime")
    private int mRuntime;

    @SerializedName("revenue")
    private long mRevenue;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("vote_count")
    private int mVoteCount;

    @SerializedName("credits")
    private Credits mCredits;

    private MovieDetails(Parcel in) {
        mBudget = in.readLong();
        mRuntime = in.readInt();
        mRevenue = in.readLong();
        mStatus = in.readString();
        mVoteCount = in.readInt();
        mCredits = (Credits) in.readValue(Credits.class.getClassLoader());
    }

    @SuppressWarnings("unused")
    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    public void setBudget(long budget) {
        mBudget = budget;
    }

    public long getBudget() {
        return mBudget;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRevenue(long revenue) {
        mRevenue = revenue;
    }

    public long getRevenue() {
        return mRevenue;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setCredits(Credits credits) {
        mCredits = credits;
    }

    public Credits getCredits() {
        return mCredits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mBudget);
        dest.writeInt(mRuntime);
        dest.writeLong(mRevenue);
        dest.writeString(mStatus);
        dest.writeInt(mVoteCount);
        dest.writeValue(mCredits);
    }
}
