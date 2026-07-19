package com.didan.rapi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.didan.rapi.reminder.ReminderWorker

class MainActivity : ComponentActivity() {
    private var requestedTaskId by mutableLongStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedTaskId = intent.getLongExtra(ReminderWorker.EXTRA_TASK_ID, 0)
        val preferences = getSharedPreferences("settings", MODE_PRIVATE)
        var darkMode by mutableStateOf(preferences.getBoolean("dark_mode", false))

        val requestNotifications = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) {}
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            RapiTheme(darkMode) {
                TodoApp(
                    requestedTaskId = requestedTaskId,
                    onRequestHandled = { requestedTaskId = 0 },
                    darkMode = darkMode,
                    onToggleDarkMode = {
                        darkMode = !darkMode
                        preferences.edit().putBoolean("dark_mode", darkMode).apply()
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        requestedTaskId = intent.getLongExtra(ReminderWorker.EXTRA_TASK_ID, 0)
    }
}
