package com.example.lifeeasy.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.PomodoroSession
import com.example.lifeeasy.domain.repository.PomodoroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

data class PomodoroUiState(
    val timeLeft: Long = 25 * 60 * 1000L,
    val totalTime: Long = 25 * 60 * 1000L,
    val isRunning: Boolean = false,
    val sessionType: String = "work", // "work", "short_break", "long_break"
    val completedSessionsToday: Int = 0,
    val showSettings: Boolean = false,
    val workDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15
)

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val repository: PomodoroRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTodaySessions()
    }

    private fun loadTodaySessions() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000L

        viewModelScope.launch {
            repository.getSessionsForRange(startOfDay, endOfDay).collect { sessions ->
                _uiState.update { it.copy(completedSessionsToday = sessions.count { s -> s.completed && s.type == "work" }) }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1000) }
            }
            onTimerFinished()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        pauseTimer()
        val duration = when (_uiState.value.sessionType) {
            "work" -> _uiState.value.workDuration
            "short_break" -> _uiState.value.shortBreakDuration
            else -> _uiState.value.longBreakDuration
        }
        _uiState.update { it.copy(timeLeft = duration * 60 * 1000L, totalTime = duration * 60 * 1000L) }
    }

    fun setSessionType(type: String) {
        _uiState.update { it.copy(sessionType = type) }
        resetTimer()
    }

    private fun onTimerFinished() {
        val currentState = _uiState.value
        viewModelScope.launch {
            val session = PomodoroSession(
                id = UUID.randomUUID().toString(),
                startTime = System.currentTimeMillis() - currentState.totalTime,
                endTime = System.currentTimeMillis(),
                durationMinutes = (currentState.totalTime / (60 * 1000)).toInt(),
                type = currentState.sessionType,
                completed = true,
                date = System.currentTimeMillis()
            )
            repository.saveSession(session)
        }
        
        // Play Alarm Sound
        try {
            val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            val r = android.media.RingtoneManager.getRingtone(context, notification)
            r.play()
            
            // Auto-stop after 5 seconds to not be too annoying
            viewModelScope.launch {
                delay(5000)
                if (r.isPlaying) r.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        _uiState.update { it.copy(isRunning = false) }
        // Auto switch logic could be added here
    }

    fun updateSettings(work: Int, short: Int, long: Int) {
        _uiState.update {
            it.copy(
                workDuration = work,
                shortBreakDuration = short,
                longBreakDuration = long,
                showSettings = false
            )
        }
        resetTimer()
    }

    fun toggleSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }
}
