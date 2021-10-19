package com.mohitsharmaji.newsproject.Repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import com.mohitsharmaji.newsproject.Api.ApiClient;
import com.mohitsharmaji.newsproject.Api.ApiInterface;
import com.mohitsharmaji.newsproject.Database.NewsDatabase;
import com.mohitsharmaji.newsproject.MainActivity;
import com.mohitsharmaji.newsproject.Models.Article;
import com.mohitsharmaji.newsproject.Models.News;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsRepository {
    //First Get LiveData From NewsDatabase
    private NewsDatabase database;
    LiveData<List<Article>> listLiveData;
    List<Article> list;
    Context context;

    public NewsRepository(Application application){
        context = application;
        database = NewsDatabase.getInstance(application);
        listLiveData = database.articleDao().getSavedArticles(); // Dao is accessible only by using database.articleDao() reference.
    }
    // Get All Articles
    public LiveData<List<Article>> getListLiveData(){
        return listLiveData;
    }
    // Delete All Saved Articles
    public void DeleteAllSavedArticles(){
        new AsyncDeleteAllSavedArticles(database).execute();
    }
    private static class AsyncDeleteAllSavedArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteAllSavedArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteAllSavedArticles();
            return null;
        }
    }
    // Delete Articles where category is general
    public void DeleteGeneralArticles(){
        new AsyncDeleteGeneralArticles(database).execute();
    }
    private static class AsyncDeleteGeneralArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteGeneralArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteGeneralCategoryArticles();
            return null;
        }
    }

    // Delete Articles where category is business
    public void DeleteBusinessArticles(){
        new AsyncDeleteBusinessArticles(database).execute();
    }
    private static class AsyncDeleteBusinessArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteBusinessArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteBusinessCategoryArticles();
            return null;
        }
    }
    // Delete Articles where category is entertainment
    public void DeleteEntertainmentArticles(){
        new AsyncDeleteEntertainmentArticles(database).execute();
    }
    private static class AsyncDeleteEntertainmentArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteEntertainmentArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteEntertainmentCategoryArticles();
            return null;
        }
    }
    // Delete Articles where category is health
    public void DeleteHealthArticles(){
        new AsyncDeleteHealthArticles(database).execute();
    }
    private static class AsyncDeleteHealthArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteHealthArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteHealthCategoryArticles();
            return null;
        }
    }
    // Delete Articles where category is science
    public void DeleteScienceArticles(){
        new AsyncDeleteScienceArticles(database).execute();
    }
    private static class AsyncDeleteScienceArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteScienceArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteScienceCategoryArticles();
            return null;
        }
    }
    // Delete Articles where category is sports
    public void DeleteSportsArticles(){
        new AsyncDeleteSportsArticles(database).execute();
    }
    private static class AsyncDeleteSportsArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteSportsArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteSportsCategoryArticles();
            return null;
        }
    }
    // Delete Articles where category is technology
    public void DeleteTechnologyArticles(){
        new AsyncDeleteTechnologyArticles(database).execute();
    }
    private static class AsyncDeleteTechnologyArticles extends AsyncTask<Void,Void,Void> {
        NewsDatabase database;
        private AsyncDeleteTechnologyArticles(NewsDatabase database){
            this.database = database;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.articleDao().deleteTechnologyCategoryArticles();
            return null;
        }
    }

    public void InsertNewsArticles(List<Article> list) {
        new AsyncInsertNewsArticles(context,database).execute(list);

    }
    private static class AsyncInsertNewsArticles extends AsyncTask<List<Article>,Void,Void>{
        private NewsDatabase database;
        Context context;
        public AsyncInsertNewsArticles(Context context,NewsDatabase database) {
            this.database = database;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new AsyncDeleteAllSavedArticles(database);
        }

        @Override
        protected Void doInBackground(List<Article>... lists) {
            database.articleDao().insertArticles(lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity mainActivity = new MainActivity();
            mainActivity.SwipeRefreshStop();
        }
    }

}
