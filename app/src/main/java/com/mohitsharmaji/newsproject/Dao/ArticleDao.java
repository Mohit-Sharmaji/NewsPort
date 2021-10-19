package com.mohitsharmaji.newsproject.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mohitsharmaji.newsproject.Models.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    //InsertNewsArticles
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    //GetAllSavedArticles
    @Query("SELECT * FROM Articles")
    LiveData<List<Article>> getSavedArticles();

    //DeleteAllSavedArticles
    @Query("DELETE FROM Articles")
    void deleteAllSavedArticles();

    //Delete Article where category is general
    @Query("DELETE FROM Articles WHERE category like '%general%'")
    void deleteGeneralCategoryArticles();

    //Delete Article where category is business
    @Query("DELETE FROM Articles WHERE category like '%business%'")
    void deleteBusinessCategoryArticles();

    //Delete Article where category is entertainment
    @Query("DELETE FROM Articles WHERE category like '%entertainment%'")
    void deleteEntertainmentCategoryArticles();

    //Delete Article where category is health
    @Query("DELETE FROM Articles WHERE category like '%health%'")
    void deleteHealthCategoryArticles();

    //Delete Article where category is science
    @Query("DELETE FROM Articles WHERE category like '%science%'")
    void deleteScienceCategoryArticles();

    //Delete Article where category is sports
    @Query("DELETE FROM Articles WHERE category like '%sports%'")
    void deleteSportsCategoryArticles();

    //Delete Article where category is technology
    @Query("DELETE FROM Articles WHERE category like '%technology%'")
    void deleteTechnologyCategoryArticles();
}
