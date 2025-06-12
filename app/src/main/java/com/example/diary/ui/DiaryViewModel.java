package com.example.diary.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.diary.data.DiaryEntry;
import com.example.diary.repository.DiaryRepository;
import java.util.List;


public class DiaryViewModel extends AndroidViewModel {
    private final DiaryRepository repository;
    private final LiveData<List<DiaryEntry>> allEntries;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        repository = new DiaryRepository(application);
        allEntries = repository.getAllEntries();
    }
    /** Возвращает заметки из корзины */
    public LiveData<List<DiaryEntry>> getTrash() {
        return repository.getTrash();
    }

    /** Умеет возвращать все записи */
    public LiveData<List<DiaryEntry>> getAllEntries() {
        return allEntries;
    }

    /** Поиск по ключевым словам */
    public LiveData<List<DiaryEntry>> search(String query) {
        return repository.search(query);
    }

    /** Фильтрация по дате */
    public LiveData<List<DiaryEntry>> filterByDate(String date) {
        return repository.filterByDate(date);
    }
    /** Фильтрует записи по категории */
    public LiveData<List<DiaryEntry>> filterByCategory(String category) {
        return repository.getByCategory(category);
    }


    /** Получить одну запись для редактирования */
    public LiveData<DiaryEntry> getById(int id) {
        return repository.getById(id);
    }

    public void insert(DiaryEntry entry)   { repository.insert(entry); }
    public void update(DiaryEntry entry)   { repository.update(entry); }
    public void delete(DiaryEntry entry)   { repository.delete(entry); }
    public void softDelete(DiaryEntry entry) { repository.softDelete(entry); }
    public void restore(DiaryEntry entry)    { repository.restore(entry); }
    public void permanentDelete(DiaryEntry entry) { repository.permanentDelete(entry); }
    public void purgeOld()                  { repository.purgeOld(); }
}
