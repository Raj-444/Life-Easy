package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.GpaResult
import kotlinx.coroutines.flow.Flow

interface GpaRepository {
    fun getAllGpaResults(): Flow<List<GpaResult>>
    suspend fun saveGpaResult(result: GpaResult)
    suspend fun deleteGpaResult(result: GpaResult)
}
