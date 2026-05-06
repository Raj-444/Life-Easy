package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.TransactionDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Transaction
import com.example.lifeeasy.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByType(type: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type).map { list -> list.map { it.toDomain() } }

    override fun getBalance(): Flow<Double?> =
        transactionDao.getBalance()

    override suspend fun getTransactionById(id: String): Transaction? =
        transactionDao.getTransactionById(id)?.toDomain()

    override suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity(SyncStatus.PENDING))
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun syncPendingTransactions() {
        val unsynced = transactionDao.getUnsyncedTransactions()
        for (entity in unsynced) {
            try {
                firestore.collection("transactions").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                transactionDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                transactionDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
