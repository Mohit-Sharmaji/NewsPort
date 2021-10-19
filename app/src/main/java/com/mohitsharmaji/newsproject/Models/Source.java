package com.mohitsharmaji.newsproject.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "source")
public class Source {
    @PrimaryKey(autoGenerate = true)
    private int primaryKey;

    private String id;
    private String name;

    public Source(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
