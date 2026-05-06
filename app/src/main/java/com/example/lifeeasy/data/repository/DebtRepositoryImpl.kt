package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.DebtTransactionDao
import com.example.lifeeasy.data.local.dao.PersonDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.DebtTransaction
import com.example.lifeeasy.domain.model.Person
import com.example.lifeeasy.domain.repository.DebtRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DebtRepositoryImpl @Inject constructor(
    private val personDao: PersonDao,
    private val debtTransactionDao: DebtTransactionDao,
    private val firestore: FirebaseFirestore
) : DebtRepository {

    override fun getAllPersons(): Flow<List<Person>> =
        personDao.getAllPersons().map { list -> list.map { it.toDomain() } }

    override fun getAllTransactions(): Flow<List<DebtTransaction>> =
        debtTransactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByPerson(personId: String): Flow<List<DebtTransaction>> =
        debtTransactionDao.getTransactionsByPerson(personId).map { list -> list.map { it.toDomain() } }

    override suspend fun savePerson(person: Person) {
        personDao.insert(person.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deletePerson(person: Person) {
        personDao.delete(person.toEntity())
    }

    override suspend fun saveTransaction(transaction: DebtTransaction) {
        debtTransactionDao.insert(transaction.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteTransaction(transaction: DebtTransaction) {
        debtTransactionDao.delete(transaction.toEntity())
    }

    override suspend fun syncPendingDebtData() {
        // Sync Persons
        val unsyncedPersons = personDao.getUnsyncedPersons()
        for (entity in unsyncedPersons) {
            try {
                firestore.collection("persons").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                personDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                personDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }

        // Sync Transactions
        val unsyncedTransactions = debtTransactionDao.getUnsyncedTransactions()
        for (entity in unsyncedTransactions) {
            try {
                firestore.collection("persons").document(entity.personId)
                    .collection("debt_transactions").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                debtTransactionDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                debtTransactionDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
