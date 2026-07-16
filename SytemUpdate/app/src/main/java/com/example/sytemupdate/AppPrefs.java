package com.example.sytemupdate;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.util.Date;

final class AppPrefs {
    private static final String PREFS = "sytemupdate_prefs";
    private static final String KEY_AUTO_START = "auto_start_enabled";
    private static final String KEY_LAST_SERVICE_START = "last_service_start";
    private static final String KEY_LAST_WORKER_RUN = "last_worker_run";
    private static final String KEY_LAST_BOOT = "last_boot";
    private static final String KEY_LAST_ERROR = "last_error";
    private static final String KEY_USER_DISABLED_AUTO_START = "user_disabled_auto_start";

    private AppPrefs() {}

    static boolean isAutoStartEnabled(Context context) {
        return prefs(context).getBoolean(KEY_AUTO_START, false);
    }

    static void setAutoStartEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_AUTO_START, enabled).apply();
    }

    static boolean isUserDisabledAutoStart(Context context) {
        return prefs(context).getBoolean(KEY_USER_DISABLED_AUTO_START, false);
    }

    static void setUserDisabledAutoStart(Context context, boolean disabled) {
        prefs(context).edit().putBoolean(KEY_USER_DISABLED_AUTO_START, disabled).apply();
    }

    static void markServiceStart(Context context) {
        put(context, KEY_LAST_SERVICE_START, now());
    }

    static void markWorkerRun(Context context) {
        put(context, KEY_LAST_WORKER_RUN, now());
    }

    static void markBoot(Context context, String action) {
        put(context, KEY_LAST_BOOT, now() + " — " + action);
    }

    static void setLastError(Context context, String error) {
        put(context, KEY_LAST_ERROR, error == null ? "" : now() + " — " + error);
    }

    static String lastServiceStart(Context context) {
        return prefs(context).getString(KEY_LAST_SERVICE_START, "Nunca");
    }

    static String lastWorkerRun(Context context) {
        return prefs(context).getString(KEY_LAST_WORKER_RUN, "Nunca");
    }

    static String lastBoot(Context context) {
        return prefs(context).getString(KEY_LAST_BOOT, "Nunca");
    }

    static String lastError(Context context) {
        String value = prefs(context).getString(KEY_LAST_ERROR, "");
        return value == null || value.isEmpty() ? "Nenhum" : value;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static void put(Context context, String key, String value) {
        prefs(context).edit().putString(key, value).apply();
    }

    private static String now() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(new Date());
    }
}
