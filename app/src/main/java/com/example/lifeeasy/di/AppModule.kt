package com.example.lifeeasy.di

import android.content.Context
import androidx.room.Room
import com.example.lifeeasy.data.local.AppDatabase
import com.example.lifeeasy.data.local.UserDao
import com.example.lifeeasy.data.local.dao.AttendanceDao
import com.example.lifeeasy.data.local.dao.CounterDao
import com.example.lifeeasy.data.local.dao.CounterHistoryDao
import com.example.lifeeasy.data.local.dao.DebtTransactionDao
import com.example.lifeeasy.data.local.dao.EventDao
import com.example.lifeeasy.data.local.dao.PersonDao
import com.example.lifeeasy.data.local.dao.SubjectDao
import com.example.lifeeasy.data.local.dao.TaskDao
import com.example.lifeeasy.data.local.dao.TransactionDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "life_easy_db"
        ).build()
    }

    // ── DAO Providers ──────────────────────────────────

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
    @Provides fun provideCounterDao(db: AppDatabase): CounterDao = db.counterDao()
    @Provides fun provideCounterHistoryDao(db: AppDatabase): CounterHistoryDao = db.counterHistoryDao()
    @Provides fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()
    @Provides fun provideSubjectDao(db: AppDatabase): SubjectDao = db.subjectDao()
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()
    @Provides fun providePersonDao(db: AppDatabase): PersonDao = db.personDao()
    @Provides fun provideDebtTransactionDao(db: AppDatabase): DebtTransactionDao = db.debtTransactionDao()
    @Provides fun providePomodoroDao(db: AppDatabase): com.example.lifeeasy.data.local.dao.PomodoroDao = db.pomodoroDao()
    @Provides fun provideNoteDao(db: AppDatabase): com.example.lifeeasy.data.local.dao.NoteDao = db.noteDao()
    @Provides fun provideHealthDao(db: AppDatabase): com.example.lifeeasy.data.local.dao.HealthDao = db.healthDao()
    @Provides fun provideRoadmapDao(db: AppDatabase): com.example.lifeeasy.data.local.dao.RoadmapDao = db.roadmapDao()

    // ── Firebase Providers ─────────────────────────────

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
