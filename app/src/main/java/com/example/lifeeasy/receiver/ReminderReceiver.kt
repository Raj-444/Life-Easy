package com.example.lifeeasy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lifeeasy.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fires a notification when a task reminder alarm triggers.
 * Handles notification actions like 'Done' and 'Snooze'.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_DESCRIPTION = "extra_task_description"
        
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_TITLE = "extra_event_title"
        const val EXTRA_EVENT_DESCRIPTION = "extra_event_description"

        const val ACTION_DONE = "com.example.lifeeasy.ACTION_DONE"
        const val ACTION_SNOOZE = "com.example.lifeeasy.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        Log.d("ReminderReceiver", "onReceive: action=$action, taskId=$taskId")

        if (action == ACTION_DONE && taskId != null) {
            handleDoneAction(context, taskId)
            return
        }

        if (action == ACTION_SNOOZE && taskId != null) {
            handleSnoozeAction(context, taskId)
            return
        }

        // Standard notification trigger
        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Reminder"
        val description = intent.getStringExtra(EXTRA_TASK_DESCRIPTION) ?: intent.getStringExtra(EXTRA_EVENT_DESCRIPTION) ?: ""

        val notificationHelper = NotificationHelper(context)
        
        if (taskId != null) {
            notificationHelper.showTaskReminderNotification(taskId, title, description)
        } else if (eventId != null) {
            notificationHelper.showEventReminderNotification(eventId, title, description)
        }
    }

    private fun handleDoneAction(context: Context, taskId: String) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.cancelNotification(taskId.hashCode())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = taskRepository.getAllTasks().first()
                val task = tasks.find { it.id == taskId }
                if (task != null) {
                    taskRepository.updateTask(task.copy(isCompleted = true, updatedAt = System.currentTimeMillis()))
                    Log.d("ReminderReceiver", "Task $taskId marked as done")
                }
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "Error marking task as done", e)
            }
        }
    }

    private fun handleSnoozeAction(context: Context, taskId: String) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.cancelNotification(taskId.hashCode())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = taskRepository.getAllTasks().first()
                val task = tasks.find { it.id == taskId }
                if (task != null) {
                    // Snooze for 10 minutes
                    val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000)
                    val updatedTask = task.copy(reminderTime = snoozeTime)
                    taskRepository.updateTask(updatedTask)
                    ReminderScheduler.schedule(context, updatedTask)
                    Log.d("ReminderReceiver", "Task $taskId snoozed for 10 mins")
                }
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "Error snoozing task", e)
            }
        }
    }
}
