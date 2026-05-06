package com.example.lifeeasy.ui.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.data.local.entity.RoadmapEntity
import com.example.lifeeasy.domain.repository.RoadmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repository: RoadmapRepository
) : ViewModel() {

    val goals: StateFlow<List<RoadmapEntity>> = repository.getAllGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addGoal(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addGoal(
                RoadmapEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleGoalCompletion(goal: RoadmapEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isCompleted = !goal.isCompleted))
        }
    }

    fun deleteGoal(goal: RoadmapEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}
