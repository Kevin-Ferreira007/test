package com.example.sytemupdate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_NOTIFICATIONS = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPrefs.isAutoStartEnabled(this)) {
            startEverythingAndClose();
            return;
        }

        if (AppPrefs.isUserDisabledAutoStart(this)) {
            // O usuário desativou antes pelas configurações do app ou negou a permissão necessária.
            // Não reativa automaticamente contra a decisão dele.
            finishAndRemoveTaskCompat();
            return;
        }

        showFirstRunConsentScreen();
    }

    private void showFirstRunConsentScreen() {
        int padding = dp(24);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Ativar serviço em segundo plano");
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(14));
        root.addView(title, matchWrap());

        TextView message = new TextView(this);
        message.setText("Este app inicia um serviço em primeiro plano com notificação visível, reinício após boot e verificação periódica.");
        message.setTextSize(16);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 0, 0, dp(18));
        root.addView(message, matchWrap());

        Button allowButton = new Button(this);
        allowButton.setText("Permitir e iniciar");
        allowButton.setOnClickListener(view -> requestPermissionOrStart());
        root.addView(allowButton, matchWrap());

        setContentView(root);
    }

    private void requestPermissionOrStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
            return;
        }

        enableAutoStartAndClose();
    }

    private void enableAutoStartAndClose() {
        AppPrefs.setUserDisabledAutoStart(this, false);
        AppPrefs.setAutoStartEnabled(this, true);
        startEverythingAndClose();
    }

    private void startEverythingAndClose() {
        KeepAliveScheduler.schedule(this);
        KeepAliveScheduler.scheduleOneShot(this, 5);
        SytemUpdateForegroundService.start(this);
        finishAndRemoveTaskCompat();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_NOTIFICATIONS) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableAutoStartAndClose();
        } else {
            AppPrefs.setUserDisabledAutoStart(this, true);
            AppPrefs.setAutoStartEnabled(this, false);
            finishAndRemoveTaskCompat();
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(6), 0, dp(6));
        return params;
    }

    private void finishAndRemoveTaskCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
