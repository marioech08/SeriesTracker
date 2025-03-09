package com.example.seriestracker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.seriestracker.database.MediaDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpisodeReminderWorker extends Worker {
    public EpisodeReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MediaDatabase db = MediaDatabase.getInstance(getApplicationContext());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int pendingEpisodes = db.episodeDao().countUnwatchedEpisodes(); // Nuevo método en DAO
            if (pendingEpisodes > 0) {
                NotificationHelper.showNotification(getApplicationContext());
            }
        });

        return Result.success();
    }
}
