package com.example.lifeeasy.ui.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.data.local.entity.RoadmapEntity
import com.example.lifeeasy.data.local.entity.RoadmapItemEntity
import com.example.lifeeasy.domain.repository.RoadmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    private val _selectedGoalId = MutableStateFlow<String?>(null)
    val selectedGoalId: StateFlow<String?> = _selectedGoalId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val goalItems: StateFlow<List<RoadmapItemEntity>> = _selectedGoalId
        .flatMapLatest { id ->
            if (id != null) repository.getItemsForGoal(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectGoal(goalId: String?) {
        _selectedGoalId.value = goalId
    }

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
            if (_selectedGoalId.value == goal.id) {
                selectGoal(null)
            }
        }
    }

    // ── Roadmap Items (Sub-tasks) ───────────────────────

    fun addItem(goalId: String, topic: String, date: String) {
        viewModelScope.launch {
            repository.addItem(
                RoadmapItemEntity(
                    goalId = goalId,
                    topic = topic,
                    date = date
                )
            )
        }
    }

    fun toggleItemCompletion(item: RoadmapItemEntity) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteItem(item: RoadmapItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
}
