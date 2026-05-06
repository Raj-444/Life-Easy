package com.example.lifeeasy.ui.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.HealthLog
import com.example.lifeeasy.domain.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class HealthUiState(
    val todayWater: Float = 0f,
    val todayWorkout: Float = 0f,
    val recentLogs: List<HealthLog> = emptyList(),
    val waterGoal: Float = 3000f, // 3000ml
    val workoutGoal: Float = 60f // 60 mins
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getTodayLogsByType("hydration").collect { logs ->
                _uiState.update { it.copy(todayWater = logs.sumOf { l -> l.value.toDouble() }.toFloat()) }
            }
        }
        viewModelScope.launch {
            repository.getTodayLogsByType("workout").collect { logs ->
                _uiState.update { it.copy(todayWorkout = logs.sumOf { l -> l.value.toDouble() }.toFloat()) }
            }
        }
        viewModelScope.launch {
            repository.getAllLogs().collect { logs ->
                _uiState.update { it.copy(recentLogs = logs.take(20)) }
            }
        }
    }

    fun addWater(amount: Float) {
        viewModelScope.launch {
            val log = HealthLog(
                id = UUID.randomUUID().toString(),
                type = "hydration",
                value = amount,
                unit = "ml"
            )
            repository.saveLog(log)
        }
    }

    fun addWorkout(duration: Float, note: String) {
        viewModelScope.launch {
            val log = HealthLog(
                id = UUID.randomUUID().toString(),
                type = "workout",
                value = duration,
                unit = "mins",
                note = note
            )
            repository.saveLog(log)
        }
    }

    fun deleteLog(log: HealthLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}
