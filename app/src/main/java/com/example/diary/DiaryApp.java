package com.example.diary;

import android.app.Application;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class DiaryApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PeriodicWorkRequest purgeRequest = new PeriodicWorkRequest.Builder(
                com.example.diary.worker.PurgeWorker.class,
                1, TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "purgeTrash",
                ExistingPeriodicWorkPolicy.KEEP,
                purgeRequest
        );
    }
}
