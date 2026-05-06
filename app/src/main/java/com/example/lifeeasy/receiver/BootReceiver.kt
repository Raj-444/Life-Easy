package com.example.lifeeasy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lifeeasy.data.local.dao.TaskDao
import com.example.lifeeasy.data.local.entity.toDomain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Restores exact alarms when the device reboots.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskDao: TaskDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {
            
            CoroutineScope(Dispatchers.IO).launch {
                val activeTasks = taskDao.getActiveTasksWithReminders()
                activeTasks.forEach { taskEntity ->
                    ReminderScheduler.schedule(context, taskEntity.toDomain())
                }
            }
        }
    }
}
