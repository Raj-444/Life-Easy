package com.example.lifeeasy.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.repository.AttendanceRepository
import com.example.lifeeasy.domain.repository.DebtRepository
import com.example.lifeeasy.domain.repository.SubjectRepository
import com.example.lifeeasy.domain.repository.PomodoroRepository
import com.example.lifeeasy.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.lifeeasy.domain.model.Task
import com.example.lifeeasy.domain.model.PomodoroSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class FocusData(val day: String, val hours: Float)

data class LifeProgress(
    val day: Float,
    val week: Float,
    val month: Float,
    val year: Float
)

data class HomeUiState(
    val pendingTasksCount: Int = 0,
    val totalDebtBalance: Double = 0.0,
    val isPresentToday: Boolean = false,
    val focusData: List<FocusData> = emptyList(),
    val lowestAttendance: Double = 100.0,
    val lowestAttendanceSubject: String = "",
    val lifeProgress: LifeProgress = LifeProgress(0f, 0f, 0f, 0f),
    val productivityScore: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val debtRepository: DebtRepository,
    private val attendanceRepository: AttendanceRepository,
    private val subjectRepository: SubjectRepository,
    private val pomodoroRepository: PomodoroRepository
) : ViewModel() {

    private val todayStart: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val pendingTasksFlow = taskRepository.getAllTasks().map { tasks ->
        tasks.count { !it.isCompleted }
    }

    private val debtBalanceFlow = debtRepository.getAllTransactions().map { transactions ->
        transactions.sumOf { if (it.type == "given") it.amount else -it.amount }
    }

    private val attendanceFlow = attendanceRepository.getAttendanceForDate(
        startDate = todayStart,
        endDate = todayStart + 24 * 60 * 60 * 1000
    ).map { 
        it != null && it.status == "present"
    }

    private val subjectAttendanceFlow = subjectRepository.getAllSubjects().map { subjects ->
        if (subjects.isEmpty()) return@map Pair(100.0, "")
        val lowest = subjects.minByOrNull { 
            if (it.totalClasses > 0) it.attendedClasses.toDouble() / it.totalClasses else 1.0 
        } ?: return@map Pair(100.0, "")
        
        val percentage = if (lowest.totalClasses > 0) {
            (lowest.attendedClasses.toDouble() / lowest.totalClasses) * 100
        } else 100.0
        Pair(percentage, lowest.name)
    }

    // Mock focus data for now as we don't have a FocusRepository yet
    private val mockFocusData = listOf(
        FocusData("Mon", 4.5f),
        FocusData("Tue", 6.2f),
        FocusData("Wed", 3.8f),
        FocusData("Thu", 5.5f),
        FocusData("Fri", 7.1f),
        FocusData("Sat", 2.4f),
        FocusData("Sun", 1.5f)
    )

    private fun calculateLifeProgress(): LifeProgress {
        val now = Calendar.getInstance()
        
        // Day progress
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        val dayProgress = (hour * 60 + minute).toFloat() / (24 * 60)
        
        // Week progress
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val adjustedDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        val weekProgress = (adjustedDayOfWeek - 1 + (hour.toFloat() / 24)) / 7
        
        // Month progress
        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
        val totalDaysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val monthProgress = (dayOfMonth - 1 + (hour.toFloat() / 24)) / totalDaysInMonth
        
        // Year progress
        val dayOfYear = now.get(Calendar.DAY_OF_YEAR)
        val totalDaysInYear = now.getActualMaximum(Calendar.DAY_OF_YEAR)
        val yearProgress = (dayOfYear - 1 + (hour.toFloat() / 24)) / totalDaysInYear
        
        return LifeProgress(dayProgress, weekProgress, monthProgress, yearProgress)
    }

    private val pomodoroFlow = pomodoroRepository.getSessionsForRange(
        startDate = todayStart,
        endDate = todayStart + 24 * 60 * 60 * 1000
    )

    private val allTasksFlow = taskRepository.getAllTasks()

    val uiState: StateFlow<HomeUiState> = combine(
        pendingTasksFlow,
        debtBalanceFlow,
        attendanceFlow,
        subjectAttendanceFlow,
        allTasksFlow,
        pomodoroFlow
    ) { args ->
        val pendingTasks = args[0] as Int
        val debtBalance = args[1] as Double
        val isPresent = args[2] as Boolean
        val subjectAtt = args[3] as Pair<Double, String>
        val allTasks = args[4] as List<Task>
        val pomodoroSessions = args[5] as List<PomodoroSession>

        // Calculate Productivity Score
        val completedToday = allTasks.count { it.isCompleted && (it.dueDate ?: 0L) >= todayStart }
        val pomodoroCount = pomodoroSessions.size
        val attendanceBonus = if (isPresent) 20 else 0
        val taskScore = (completedToday * 15).coerceAtMost(50)
        val focusScore = (pomodoroCount * 10).coerceAtMost(30)
        val totalScore = (taskScore + focusScore + attendanceBonus).coerceAtMost(100)

        HomeUiState(
            pendingTasksCount = pendingTasks,
            totalDebtBalance = debtBalance,
            isPresentToday = isPresent,
            focusData = mockFocusData,
            lowestAttendance = subjectAtt.first,
            lowestAttendanceSubject = subjectAtt.second,
            lifeProgress = calculateLifeProgress(),
            productivityScore = totalScore,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}

