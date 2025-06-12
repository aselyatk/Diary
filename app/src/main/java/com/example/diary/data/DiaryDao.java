package com.example.diary.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiaryEntry entry);

    @Update
    void update(DiaryEntry entry);

    @Delete
    void delete(DiaryEntry entry);

    @Query("SELECT * FROM diary_table WHERE isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> getAllEntries();

    @Query("SELECT * FROM diary_table WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') AND isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> searchEntries(String query);

    @Query("SELECT * FROM diary_table WHERE date(timestamp/1000, 'unixepoch') = :date AND isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> filterByDate(String date);

    @Query("SELECT * FROM diary_table WHERE category = :category AND isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> filterByCategory(String category);

    @Query("SELECT * FROM diary_table WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    LiveData<List<DiaryEntry>> getDeletedEntries();

    @Query("UPDATE diary_table SET isDeleted = 1, deletedAt = :time WHERE id = :id")
    void softDelete(int id, long time);

    @Query("UPDATE diary_table SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    void restore(int id);

    @Query("DELETE FROM diary_table WHERE isDeleted = 1 AND deletedAt <= :cutoff")
    void purgeOlderThan(long cutoff);

    // Добавляем этот метод
    @Query("DELETE FROM diary_table WHERE id = :id")
    void permanentDelete(int id);
    // Получить одну запись по её ID
    @Query("SELECT * FROM diary_table WHERE id = :id")
    LiveData<DiaryEntry> getEntryById(int id);

}
