package com.example.diary.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.diary.data.DiaryDao;
import com.example.diary.data.DiaryDatabase;
import com.example.diary.data.DiaryEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiaryRepository {
    private final DiaryDao diaryDao;
    private final LiveData<List<DiaryEntry>> allEntries;
    private final ExecutorService executorService;

    public DiaryRepository(Application application) {
        DiaryDatabase db = DiaryDatabase.getDatabase(application);
        diaryDao = db.diaryDao();
        allEntries = diaryDao.getAllEntries();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<DiaryEntry>> getAllEntries() {
        return allEntries;
    }

    public LiveData<List<DiaryEntry>> getTrash() {
        return diaryDao.getDeletedEntries();
    }

    public void softDelete(DiaryEntry e) {
        long now = System.currentTimeMillis();
        executorService.execute(() -> diaryDao.softDelete(e.getId(), now));
    }

    public void restore(DiaryEntry e) {
        executorService.execute(() -> diaryDao.restore(e.getId()));
    }

    public void purgeOld() {
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        executorService.execute(() -> diaryDao.purgeOlderThan(tenDaysAgo));
    }

    public LiveData<List<DiaryEntry>> search(String query) {
        return diaryDao.searchEntries(query);
    }

    public LiveData<List<DiaryEntry>> filterByDate(String date) {
        return diaryDao.filterByDate(date);
    }

    public LiveData<List<DiaryEntry>> getByCategory(String category) {
        return diaryDao.filterByCategory(category);
    }

    public LiveData<DiaryEntry> getById(int id) {
        return diaryDao.getEntryById(id);
    }

    public void insert(DiaryEntry entry) {
        executorService.execute(() -> diaryDao.insert(entry));
    }

    public void update(DiaryEntry entry) {
        executorService.execute(() -> diaryDao.update(entry));
    }

    public void delete(DiaryEntry entry) {
        executorService.execute(() -> diaryDao.delete(entry));
    }

    public void permanentDelete(DiaryEntry entry) {
        executorService.execute(() -> diaryDao.permanentDelete(entry.getId()));
    }
}
