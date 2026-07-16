package com.example.sytemupdate;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class KeepAliveWorker extends Worker {
    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppPrefs.markWorkerRun(context);

        if (!AppPrefs.isAutoStartEnabled(context)) {
            KeepAliveScheduler.cancel(context);
            return Result.success();
        }

        try {
            SytemUpdateForegroundService.start(context);
            AppPrefs.setLastError(context, "");
            return Result.success();
        } catch (RuntimeException exception) {
            AppPrefs.setLastError(context, exception.getClass().getSimpleName() + ": " + exception.getMessage());
            return Result.retry();
        }
    }
}
