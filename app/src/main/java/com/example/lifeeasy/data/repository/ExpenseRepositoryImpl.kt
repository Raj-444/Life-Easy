package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.dao.ExpenseDao
import com.example.lifeeasy.data.local.entity.ExpenseEntity
import com.example.lifeeasy.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    override fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    override suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }
}
