package com.example.diary.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.diary.data.DiaryDatabase;
import com.example.diary.data.DiaryDao;
import java.util.concurrent.TimeUnit;

public class PurgeWorker extends Worker {

    public PurgeWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Получаем DAO и чистим корзину от записей старше 10 дней
        DiaryDatabase db = DiaryDatabase.getDatabase(getApplicationContext());
        DiaryDao dao = db.diaryDao();
        long cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        dao.purgeOlderThan(cutoff);
        return Result.success();
    }
}
