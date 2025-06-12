package com.example.diary.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_table")
public class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String category; // бонус
    private long timestamp;
    private boolean isDeleted;
    private long deletedAt;
    public DiaryEntry(String title, String content, String category, long timestamp) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.timestamp = timestamp;
        this.isDeleted = false;
        this.deletedAt = 0;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public long getTimestamp() { return timestamp; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public long getDeletedAt() { return deletedAt; }
    public void setDeletedAt(long deletedAt) { this.deletedAt = deletedAt; }



}