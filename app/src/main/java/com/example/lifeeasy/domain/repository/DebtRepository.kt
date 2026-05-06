package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.DebtTransaction
import com.example.lifeeasy.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun getAllPersons(): Flow<List<Person>>
    fun getAllTransactions(): Flow<List<DebtTransaction>>
    fun getTransactionsByPerson(personId: String): Flow<List<DebtTransaction>>
    
    suspend fun savePerson(person: Person)
    suspend fun deletePerson(person: Person)
    
    suspend fun saveTransaction(transaction: DebtTransaction)
    suspend fun deleteTransaction(transaction: DebtTransaction)
    
    suspend fun syncPendingDebtData()
}
