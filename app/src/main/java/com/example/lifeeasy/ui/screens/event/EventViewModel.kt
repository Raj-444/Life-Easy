package com.example.lifeeasy.ui.screens.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.Event
import com.example.lifeeasy.domain.model.Subject
import com.example.lifeeasy.domain.repository.EventRepository
import com.example.lifeeasy.domain.repository.SubjectRepository
import com.example.lifeeasy.receiver.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

data class EventUiState(
    val showAddEventSheet: Boolean = false,
    val selectedDate: Calendar = Calendar.getInstance(),
    val isLoading: Boolean = false
)

@HiltViewModel
class EventViewModel @Inject constructor(
    application: Application,
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    val allEvents: StateFlow<List<Event>> = eventRepository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectDate(calendar: Calendar) {
        _uiState.value = _uiState.value.copy(selectedDate = calendar)
    }

    fun showAddEventSheet() {
        _uiState.value = _uiState.value.copy(showAddEventSheet = true)
    }

    fun dismissAddEventSheet() {
        _uiState.value = _uiState.value.copy(showAddEventSheet = false)
    }


    fun saveEvent(
        title: String,
        subjectId: String?,
        type: String,
        description: String,
        startTime: Long,
        reminderMinutes: Int
    ) {
        viewModelScope.launch {
            val event = Event(
                id = UUID.randomUUID().toString(),
                title = title,
                subjectId = subjectId,
                eventType = type,
                description = description,
                startTime = startTime,
                reminderMinutes = reminderMinutes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            eventRepository.saveEvent(event)
            
            // Schedule reminder if needed
            if (reminderMinutes > 0) {
                ReminderScheduler.scheduleEvent(getApplication(), event)
            }
            
            dismissAddEventSheet()
        }
    }

    fun deleteEvent(event: com.example.lifeeasy.domain.model.Event) {
        viewModelScope.launch {
            eventRepository.deleteEvent(event)
            ReminderScheduler.cancelEvent(getApplication(), event.id)
        }
    }
}
