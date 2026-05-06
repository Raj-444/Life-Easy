package com.example.lifeeasy.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.Note
import com.example.lifeeasy.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val allNotes = repository.getAllNotes()

    init {
        viewModelScope.launch {
            allNotes.collect { notes ->
                _uiState.update { it.copy(notes = notes) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun saveNote(title: String, content: String, color: Int, isPinned: Boolean, id: String? = null) {
        viewModelScope.launch {
            val note = Note(
                id = id ?: UUID.randomUUID().toString(),
                title = title,
                content = content,
                color = color,
                isPinned = isPinned
            )
            repository.saveNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.saveNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
