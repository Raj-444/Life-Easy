package com.example.lifeeasy.ui.screens.task

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.Task
import com.example.lifeeasy.domain.repository.TaskRepository
import com.example.lifeeasy.receiver.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class TaskFilter { ALL, TODAY, UPCOMING, COMPLETED }

data class TaskUiState(
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val editingTask: Task? = null,
    val snackbarMessage: String? = null,
    val activeFilter: TaskFilter = TaskFilter.ALL
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // ── Dialog controls ──────────────────────────────

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingTask = null)
    }

    fun showEditDialog(task: Task) {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingTask = task)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false, editingTask = null)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun setFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(activeFilter = filter)
    }

    // ── CRUD operations ──────────────────────────────

    fun addTask(title: String, description: String, priority: Int, dueDate: Long?, reminderTime: Long?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                dueDate = dueDate,
                reminderTime = reminderTime,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveTask(task)

            // Schedule reminder alarm
            if (reminderTime != null) {
                ReminderScheduler.schedule(context, task)
            }

            _uiState.value = _uiState.value.copy(
                showAddDialog = false,
                snackbarMessage = "Task added"
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(updatedAt = System.currentTimeMillis())
            repository.updateTask(updated)

            // Re-schedule or cancel reminder
            if (updated.reminderTime != null) {
                ReminderScheduler.schedule(context, updated)
            } else {
                ReminderScheduler.cancel(context, updated.id)
            }

            _uiState.value = _uiState.value.copy(showAddDialog = false, editingTask = null)
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            val toggled = task.copy(
                isCompleted = !task.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTask(toggled)

            // Cancel reminder when completed
            if (toggled.isCompleted) {
                ReminderScheduler.cancel(context, toggled.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            ReminderScheduler.cancel(context, task.id)
            _uiState.value = _uiState.value.copy(snackbarMessage = "'${task.title}' deleted")
        }
    }
}
