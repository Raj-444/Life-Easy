package com.example.lifeeasy.ui.screens.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.Counter
import com.example.lifeeasy.domain.model.CounterHistory
import com.example.lifeeasy.domain.repository.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CounterUiState(
    val showAddDialog: Boolean = false,
    val selectedCounterIdForHistory: String? = null
)

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val repository: CounterRepository
) : ViewModel() {

    val allCounters: StateFlow<List<Counter>> = repository.getAllCounters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun dismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun addCounter(name: String, initialCount: Int, target: Int?) {
        viewModelScope.launch {
            repository.saveCounter(
                Counter(
                    label = name,
                    count = initialCount,
                    targetCount = target,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            dismissAddDialog()
        }
    }

    fun increment(id: String) {
        viewModelScope.launch {
            repository.increment(id)
        }
    }

    fun decrement(id: String) {
        viewModelScope.launch {
            repository.decrement(id)
        }
    }

    fun deleteCounter(counter: Counter) {
        viewModelScope.launch {
            repository.deleteCounter(counter)
        }
    }

    fun selectCounterForHistory(id: String?) {
        _uiState.value = _uiState.value.copy(selectedCounterIdForHistory = id)
    }

    fun getHistoryForCounter(id: String): Flow<List<CounterHistory>> =
        repository.getHistoryForCounter(id)
}
