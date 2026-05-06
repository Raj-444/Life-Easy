package com.example.lifeeasy.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.lifeeasy.domain.model.Task

/**
 * Schedules / cancels exact alarms for task reminders.
 */
object ReminderScheduler {

    fun schedule(context: Context, task: Task) {
        val reminderTime = task.reminderTime ?: return
        if (reminderTime <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(ReminderReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(ReminderReceiver.EXTRA_TASK_DESCRIPTION, task.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, task.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(alarmManager, reminderTime, pendingIntent)
    }

    fun scheduleEvent(context: Context, event: com.example.lifeeasy.domain.model.Event) {
        val reminderTime = event.startTime - (event.reminderMinutes * 60 * 1000L)
        if (reminderTime <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_EVENT_ID, event.id)
            putExtra(ReminderReceiver.EXTRA_EVENT_TITLE, "Upcoming ${event.eventType.uppercase()}: ${event.title}")
            putExtra(ReminderReceiver.EXTRA_EVENT_DESCRIPTION, event.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, event.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(alarmManager, reminderTime, pendingIntent)
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerTime: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
            )
        }
    }

    fun cancel(context: Context, taskId: String) {
        cancelAlarm(context, taskId.hashCode())
    }

    fun cancelEvent(context: Context, eventId: String) {
        cancelAlarm(context, eventId.hashCode())
    }

    private fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
