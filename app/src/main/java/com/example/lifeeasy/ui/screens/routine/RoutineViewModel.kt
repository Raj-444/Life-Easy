package com.example.lifeeasy.ui.screens.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.RoutineItem
import com.example.lifeeasy.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK))
    val selectedDay: StateFlow<Int> = _selectedDay

    val routineItems = _selectedDay.flatMapLatest { day ->
        repository.getRoutineItemsForDay(day)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }

    fun addRoutineItem(item: RoutineItem) {
        viewModelScope.launch {
            repository.saveRoutineItem(item)
        }
    }

    fun deleteRoutineItem(item: RoutineItem) {
        viewModelScope.launch {
            repository.deleteRoutineItem(item)
        }
    }
}
