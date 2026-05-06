package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.dao.GpaDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.GpaResult
import com.example.lifeeasy.domain.repository.GpaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GpaRepositoryImpl @Inject constructor(
    private val gpaDao: GpaDao
) : GpaRepository {
    override fun getAllGpaResults(): Flow<List<GpaResult>> {
        return gpaDao.getAllGpaResults().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveGpaResult(result: GpaResult) {
        gpaDao.insertGpaResult(result.toEntity())
    }

    override suspend fun deleteGpaResult(result: GpaResult) {
        gpaDao.deleteGpaResult(result.toEntity())
    }
}
