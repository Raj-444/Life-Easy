package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.DebtTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtTransactionDao {
    @Query("SELECT * FROM debt_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<DebtTransactionEntity>>

    @Query("SELECT * FROM debt_transactions WHERE personId = :personId ORDER BY date DESC")
    fun getTransactionsByPerson(personId: String): Flow<List<DebtTransactionEntity>>

    @Query("SELECT * FROM debt_transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): DebtTransactionEntity?

    @Query("SELECT * FROM debt_transactions WHERE syncStatus != :status")
    suspend fun getUnsyncedTransactions(status: SyncStatus = SyncStatus.SYNCED): List<DebtTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: DebtTransactionEntity)

    @Update
    suspend fun update(transaction: DebtTransactionEntity)

    @Delete
    suspend fun delete(transaction: DebtTransactionEntity)

    @Query("UPDATE debt_transactions SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
