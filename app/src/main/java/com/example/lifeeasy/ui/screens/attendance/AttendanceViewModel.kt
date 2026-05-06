package com.example.lifeeasy.ui.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.Subject
import com.example.lifeeasy.domain.repository.AttendanceRepository
import com.example.lifeeasy.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AttendanceUiState(
    val showAddSubjectDialog: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    fun showAddSubjectDialog() {
        _uiState.value = _uiState.value.copy(showAddSubjectDialog = true)
    }

    fun dismissAddSubjectDialog() {
        _uiState.value = _uiState.value.copy(showAddSubjectDialog = false)
    }

    fun addSubject(name: String, teacher: String) {
        viewModelScope.launch {
            val subject = Subject(
                id = UUID.randomUUID().toString(),
                name = name,
                teacherName = teacher,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            subjectRepository.saveSubject(subject)
            dismissAddSubjectDialog()
        }
    }

    fun markAttendance(subjectId: String, status: String) {
        viewModelScope.launch {
            attendanceRepository.markAttendance(subjectId, status)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subject)
        }
    }
}
