package com.example.lifeeasy.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules the [SyncWorker] to run periodically.
 *
 * Call [schedulePeriodic] from Application.onCreate() or after user login.
 * Call [syncNow] to trigger an immediate one-shot sync (e.g. after a bulk write).
 */
object SyncScheduler {

    private const val PERIODIC_SYNC_TAG = "life_easy_periodic_sync"

    /**
     * Enqueue a periodic sync every [intervalMinutes] minutes.
     * Requires network connectivity.
     */
    fun schedulePeriodic(context: Context, intervalMinutes: Long = 15L) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(PERIODIC_SYNC_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /**
     * Trigger a one-shot sync immediately (still respects network constraint).
     */
    fun syncNow(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = androidx.work.OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    /** Cancel all scheduled syncs. */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_TAG)
    }
}
