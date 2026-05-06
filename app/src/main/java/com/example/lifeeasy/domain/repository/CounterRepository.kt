package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Counter
import com.example.lifeeasy.domain.model.CounterHistory
import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun getAllCounters(): Flow<List<Counter>>
    suspend fun getCounterById(id: String): Counter?
    suspend fun saveCounter(counter: Counter)
    suspend fun increment(id: String)
    suspend fun decrement(id: String)
    suspend fun deleteCounter(counter: Counter)
    fun getHistoryForCounter(counterId: String): Flow<List<CounterHistory>>
    suspend fun syncPendingCounters()
}
