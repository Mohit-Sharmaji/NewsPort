package com.mohitsharmaji.newsproject.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.mohitsharmaji.newsproject.Models.News;

public interface ApiInterface {
    // @GET Calls for News type JSON data from the web.
    //getNews is a method with country & apiKey as its args.
    @GET("top-headlines")
    Call<News> getNews(@Query("country") String country,
                       @Query("apiKey") String apiKey,
                       @Query("category") String category,
                       @Query("pageSize") int pageSize);

    @GET("everything") // "everything" is used to search the NEWS
    Call<News> getNewsSearch(
            @Query("q") String keyword, // Keyword search by user
            @Query("language") String language, // Language
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize
    );
}
