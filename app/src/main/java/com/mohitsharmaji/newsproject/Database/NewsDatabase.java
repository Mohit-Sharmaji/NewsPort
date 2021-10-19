package com.mohitsharmaji.newsproject.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mohitsharmaji.newsproject.Dao.ArticleDao;
import com.mohitsharmaji.newsproject.Models.Article;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Article.class},version = 1)
public abstract class NewsDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "NewsDatabase";
    public abstract ArticleDao articleDao();
    private static volatile NewsDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecution = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static NewsDatabase getInstance(Context context){
        if (INSTANCE==null){
            synchronized (NewsDatabase.class){
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context,NewsDatabase.class,DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            //.addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
