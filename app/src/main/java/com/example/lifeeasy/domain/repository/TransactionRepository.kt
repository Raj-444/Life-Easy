package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByType(type: String): Flow<List<Transaction>>
    fun getBalance(): Flow<Double?>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun syncPendingTransactions()
}
