package com.example.lifeeasy.ui.screens.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.DebtTransaction
import com.example.lifeeasy.domain.model.Person
import com.example.lifeeasy.domain.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class DebtUiState(
    val showAddPersonDialog: Boolean = false,
    val showAddTransactionDialog: Boolean = false,
    val selectedPersonId: String? = null
)

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val repository: DebtRepository
) : ViewModel() {

    val persons: StateFlow<List<Person>> = repository.getAllPersons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allTransactions: StateFlow<List<DebtTransaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Instead of querying all transactions dynamically, let's keep it simple.
    // We can observe all transactions from the repository so we can calculate balances.
    // Wait, the repository doesn't have `getAllTransactions()` yet in the interface.
    // Let me just add a method to get transactions for a person.

    private val _uiState = MutableStateFlow(DebtUiState())
    val uiState: StateFlow<DebtUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPersonTransactions: StateFlow<List<DebtTransaction>> = _uiState
        .map { it.selectedPersonId }
        .flatMapLatest { id ->
            if (id != null) repository.getTransactionsByPerson(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun showAddPersonDialog() {
        _uiState.update { it.copy(showAddPersonDialog = true) }
    }

    fun dismissAddPersonDialog() {
        _uiState.update { it.copy(showAddPersonDialog = false) }
    }

    fun showAddTransactionDialog() {
        _uiState.update { it.copy(showAddTransactionDialog = true) }
    }

    fun dismissAddTransactionDialog() {
        _uiState.update { it.copy(showAddTransactionDialog = false) }
    }

    fun selectPerson(personId: String?) {
        _uiState.update { it.copy(selectedPersonId = personId) }
    }

    fun addPerson(name: String) {
        viewModelScope.launch {
            val person = Person(
                id = UUID.randomUUID().toString(),
                name = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.savePerson(person)
            dismissAddPersonDialog()
        }
    }

    fun addTransaction(amount: Double, type: String, note: String) {
        val personId = _uiState.value.selectedPersonId ?: return
        viewModelScope.launch {
            val transaction = DebtTransaction(
                id = UUID.randomUUID().toString(),
                personId = personId,
                amount = amount,
                type = type, // "given" or "received"
                note = note,
                date = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveTransaction(transaction)
            dismissAddTransactionDialog()
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch {
            repository.deletePerson(person)
            if (_uiState.value.selectedPersonId == person.id) {
                selectPerson(null)
            }
        }
    }

    fun deleteTransaction(transaction: DebtTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}
