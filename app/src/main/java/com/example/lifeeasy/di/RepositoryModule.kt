package com.example.lifeeasy.di

import com.example.lifeeasy.data.repository.AttendanceRepositoryImpl
import com.example.lifeeasy.data.repository.AuthRepositoryImpl
import com.example.lifeeasy.data.repository.CounterRepositoryImpl
import com.example.lifeeasy.data.repository.EventRepositoryImpl
import com.example.lifeeasy.data.repository.SubjectRepositoryImpl
import com.example.lifeeasy.data.repository.TaskRepositoryImpl
import com.example.lifeeasy.data.repository.TransactionRepositoryImpl
import com.example.lifeeasy.data.repository.UserRepositoryImpl
import com.example.lifeeasy.data.repository.RoutineRepositoryImpl
import com.example.lifeeasy.data.repository.GpaRepositoryImpl
import com.example.lifeeasy.data.repository.ExpenseRepositoryImpl
import com.example.lifeeasy.domain.repository.AttendanceRepository
import com.example.lifeeasy.domain.repository.AuthRepository
import com.example.lifeeasy.domain.repository.CounterRepository
import com.example.lifeeasy.domain.repository.EventRepository
import com.example.lifeeasy.domain.repository.SubjectRepository
import com.example.lifeeasy.domain.repository.TaskRepository
import com.example.lifeeasy.domain.repository.TransactionRepository
import com.example.lifeeasy.domain.repository.UserRepository
import com.example.lifeeasy.domain.repository.RoutineRepository
import com.example.lifeeasy.domain.repository.GpaRepository
import com.example.lifeeasy.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds @Singleton
    abstract fun bindCounterRepository(impl: CounterRepositoryImpl): CounterRepository

    @Binds @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds @Singleton
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindDebtRepository(
        debtRepositoryImpl: com.example.lifeeasy.data.repository.DebtRepositoryImpl
    ): com.example.lifeeasy.domain.repository.DebtRepository

    @Binds
    @Singleton
    abstract fun bindPomodoroRepository(
        pomodoroRepositoryImpl: com.example.lifeeasy.data.repository.PomodoroRepositoryImpl
    ): com.example.lifeeasy.domain.repository.PomodoroRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: com.example.lifeeasy.data.repository.NoteRepositoryImpl
    ): com.example.lifeeasy.domain.repository.NoteRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        healthRepositoryImpl: com.example.lifeeasy.data.repository.HealthRepositoryImpl
    ): com.example.lifeeasy.domain.repository.HealthRepository

    @Binds
    @Singleton
    abstract fun bindRoadmapRepository(
        roadmapRepositoryImpl: com.example.lifeeasy.data.repository.RoadmapRepositoryImpl
    ): com.example.lifeeasy.domain.repository.RoadmapRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(
        routineRepositoryImpl: RoutineRepositoryImpl
    ): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindGpaRepository(
        gpaRepositoryImpl: GpaRepositoryImpl
    ): GpaRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}
