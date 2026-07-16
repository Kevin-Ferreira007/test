package com.example.sytemupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        boolean allowedAction = Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action);

        if (!allowedAction) return;

        AppPrefs.markBoot(context, action);

        if (!AppPrefs.isAutoStartEnabled(context)) return;

        KeepAliveScheduler.schedule(context);
        KeepAliveScheduler.scheduleOneShot(context, 10);

        try {
            SytemUpdateForegroundService.start(context);
            AppPrefs.setLastError(context, "");
        } catch (RuntimeException exception) {
            AppPrefs.setLastError(context, exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
    }
}
