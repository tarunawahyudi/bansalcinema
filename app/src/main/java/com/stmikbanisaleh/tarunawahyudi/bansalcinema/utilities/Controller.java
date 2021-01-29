package com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.stmikbanisaleh.tarunawahyudi.bansalcinema.utilities.Constant.MOVIE_BASE_URL;


public class Controller {

    private static Retrofit sRetrofit = null;

    public static Retrofit getClient() {
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(MOVIE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
