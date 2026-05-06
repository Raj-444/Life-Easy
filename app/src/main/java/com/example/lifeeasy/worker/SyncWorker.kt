package com.example.lifeeasy.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lifeeasy.domain.repository.AttendanceRepository
import com.example.lifeeasy.domain.repository.CounterRepository
import com.example.lifeeasy.domain.repository.DebtRepository
import com.example.lifeeasy.domain.repository.EventRepository
import com.example.lifeeasy.domain.repository.SubjectRepository
import com.example.lifeeasy.domain.repository.TaskRepository
import com.example.lifeeasy.domain.repository.TransactionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SyncWorkerEntryPoint {
        fun taskRepository(): TaskRepository
        fun counterRepository(): CounterRepository
        fun subjectRepository(): SubjectRepository
        fun attendanceRepository(): AttendanceRepository
        fun eventRepository(): EventRepository
        fun transactionRepository(): TransactionRepository
        fun debtRepository(): DebtRepository
    }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                SyncWorkerEntryPoint::class.java
            )

            val taskRepo = entryPoint.taskRepository()
            val counterRepo = entryPoint.counterRepository()
            val subjectRepo = entryPoint.subjectRepository()
            val attendanceRepo = entryPoint.attendanceRepository()
            val eventRepo = entryPoint.eventRepository()
            val transactionRepo = entryPoint.transactionRepository()
            val debtRepo = entryPoint.debtRepository()

            // Sync all repositories concurrently
            val taskSync = async { taskRepo.syncPendingTasks() }
            val counterSync = async { counterRepo.syncPendingCounters() }
            val subjectSync = async { subjectRepo.syncPendingSubjects() }
            val attendanceSync = async { attendanceRepo.syncPendingAttendance() }
            val eventSync = async { eventRepo.syncPendingEvents() }
            val transactionSync = async { transactionRepo.syncPendingTransactions() }
            val debtSync = async { debtRepo.syncPendingDebtData() }

            // Wait for all to complete
            taskSync.await()
            counterSync.await()
            subjectSync.await()
            attendanceSync.await()
            eventSync.await()
            transactionSync.await()
            debtSync.await()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error (e.g. network timeout), retry
            Result.retry()
        }
    }
}
