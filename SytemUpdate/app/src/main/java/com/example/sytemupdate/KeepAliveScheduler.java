package com.example.sytemupdate;

import android.content.Context;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

final class KeepAliveScheduler {
    private static final String PERIODIC_NAME = "sytemupdate_periodic_keepalive";
    private static final String ONESHOT_NAME = "sytemupdate_oneshot_keepalive";

    private KeepAliveScheduler() {}

    static void schedule(Context context) {
        PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(
                KeepAliveWorker.class,
                15,
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodic
        );
    }

    static void scheduleOneShot(Context context, long delaySeconds) {
        OneTimeWorkRequest oneShot = new OneTimeWorkRequest.Builder(KeepAliveWorker.class)
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                ONESHOT_NAME,
                ExistingWorkPolicy.REPLACE,
                oneShot
        );
    }

    static void cancel(Context context) {
        WorkManager manager = WorkManager.getInstance(context);
        manager.cancelUniqueWork(PERIODIC_NAME);
        manager.cancelUniqueWork(ONESHOT_NAME);
    }
}
