package com.example.lifeeasy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lifeeasy.data.local.dao.AttendanceDao
import com.example.lifeeasy.data.local.dao.CounterDao
import com.example.lifeeasy.data.local.dao.CounterHistoryDao
import com.example.lifeeasy.data.local.dao.DebtTransactionDao
import com.example.lifeeasy.data.local.dao.EventDao
import com.example.lifeeasy.data.local.dao.PersonDao
import com.example.lifeeasy.data.local.dao.SubjectDao
import com.example.lifeeasy.data.local.dao.TaskDao
import com.example.lifeeasy.data.local.dao.TransactionDao
import com.example.lifeeasy.data.local.dao.PomodoroDao
import com.example.lifeeasy.data.local.dao.NoteDao
import com.example.lifeeasy.data.local.dao.HealthDao
import com.example.lifeeasy.data.local.entity.AttendanceEntity
import com.example.lifeeasy.data.local.entity.CounterEntity
import com.example.lifeeasy.data.local.entity.CounterHistoryEntity
import com.example.lifeeasy.data.local.entity.DebtTransactionEntity
import com.example.lifeeasy.data.local.entity.EventEntity
import com.example.lifeeasy.data.local.entity.PersonEntity
import com.example.lifeeasy.data.local.entity.SubjectEntity
import com.example.lifeeasy.data.local.entity.TaskEntity
import com.example.lifeeasy.data.local.entity.TransactionEntity
import com.example.lifeeasy.data.local.entity.PomodoroEntity
import com.example.lifeeasy.data.local.entity.NoteEntity
import com.example.lifeeasy.data.local.entity.HealthEntity
import com.example.lifeeasy.data.local.dao.RoadmapDao
import com.example.lifeeasy.data.local.dao.RoutineDao
import com.example.lifeeasy.data.local.dao.GpaDao
import com.example.lifeeasy.data.local.dao.ExpenseDao
import com.example.lifeeasy.data.local.entity.RoutineEntity
import com.example.lifeeasy.data.local.entity.GpaResultEntity
import com.example.lifeeasy.data.local.entity.ExpenseEntity
import com.example.lifeeasy.data.local.entity.RoadmapItemEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        CounterEntity::class,
        CounterHistoryEntity::class,
        AttendanceEntity::class,
        SubjectEntity::class,
        TransactionEntity::class,
        EventEntity::class,
        PersonEntity::class,
        DebtTransactionEntity::class,
        PomodoroEntity::class,
        NoteEntity::class,
        HealthEntity::class,
        RoadmapEntity::class,
        RoutineEntity::class,
        GpaResultEntity::class,
        ExpenseEntity::class,
        RoadmapItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun counterDao(): CounterDao
    abstract fun counterHistoryDao(): CounterHistoryDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun subjectDao(): SubjectDao
    abstract fun transactionDao(): TransactionDao
    abstract fun eventDao(): EventDao
    abstract fun personDao(): PersonDao
    abstract fun debtTransactionDao(): DebtTransactionDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun noteDao(): NoteDao
    abstract fun healthDao(): HealthDao
    abstract fun roadmapDao(): RoadmapDao
    abstract fun routineDao(): RoutineDao
    abstract fun gpaDao(): GpaDao
    abstract fun expenseDao(): ExpenseDao
}
