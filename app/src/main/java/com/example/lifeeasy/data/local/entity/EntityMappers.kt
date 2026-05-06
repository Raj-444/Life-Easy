package com.example.lifeeasy.data.local.entity

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.domain.model.*

// ──────────────────────── Task ────────────────────────

fun TaskEntity.toDomain() = Task(
    id = id, title = title, description = description,
    isCompleted = isCompleted, priority = priority,
    dueDate = dueDate, reminderTime = reminderTime,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Task.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = TaskEntity(
    id = id, title = title, description = description,
    isCompleted = isCompleted, priority = priority,
    dueDate = dueDate, reminderTime = reminderTime,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

// ──────────────────────── Counter ─────────────────────

fun CounterEntity.toDomain() = Counter(
    id = id, label = label, count = count,
    targetCount = targetCount, createdAt = createdAt, updatedAt = updatedAt
)

fun Counter.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = CounterEntity(
    id = id, label = label, count = count,
    targetCount = targetCount, createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

// ──────────────────────── Counter History ─────────────

fun CounterHistoryEntity.toDomain() = CounterHistory(
    id = id, counterId = counterId, oldCount = oldCount,
    newCount = newCount, changeType = changeType, timestamp = timestamp
)

fun CounterHistory.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = CounterHistoryEntity(
    id = id.ifBlank { java.util.UUID.randomUUID().toString() },
    counterId = counterId, oldCount = oldCount,
    newCount = newCount, changeType = changeType,
    timestamp = if (timestamp == 0L) System.currentTimeMillis() else timestamp,
    syncStatus = syncStatus
)

// ──────────────────────── Attendance ──────────────────

fun AttendanceEntity.toDomain() = Attendance(
    id = id, subjectId = subjectId, date = date,
    status = status, createdAt = createdAt, updatedAt = updatedAt
)

fun Attendance.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = AttendanceEntity(
    id = id, subjectId = subjectId, date = date,
    status = status, createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

// ──────────────────────── Subject ─────────────────────

fun SubjectEntity.toDomain() = Subject(
    id = id, name = name, teacherName = teacherName,
    totalClasses = totalClasses, attendedClasses = attendedClasses,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Subject.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = SubjectEntity(
    id = id, name = name, teacherName = teacherName,
    totalClasses = totalClasses, attendedClasses = attendedClasses,
    createdAt = createdAt, updatedAt = System.currentTimeMillis(),
    syncStatus = syncStatus
)

// ──────────────────────── Transaction ─────────────────

fun TransactionEntity.toDomain() = Transaction(
    id = id, title = title, amount = amount,
    type = type, category = category, note = note,
    date = date, createdAt = createdAt, updatedAt = updatedAt
)

fun Transaction.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = TransactionEntity(
    id = id, title = title, amount = amount,
    type = type, category = category, note = note,
    date = date, createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

// ──────────────────────── Event ───────────────────────

fun EventEntity.toDomain() = Event(
    id = id, title = title, subjectId = subjectId, eventType = eventType,
    description = description,
    startTime = startTime, endTime = endTime,
    location = location, reminderMinutes = reminderMinutes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Event.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = EventEntity(
    id = id, title = title, subjectId = subjectId, eventType = eventType,
    description = description,
    startTime = startTime, endTime = endTime,
    location = location, reminderMinutes = reminderMinutes,
    createdAt = createdAt, updatedAt = System.currentTimeMillis(),
    syncStatus = syncStatus
)

// ──────────────────────── Lend/Borrow Tracker ─────────

fun PersonEntity.toDomain() = Person(
    id = id, name = name, createdAt = createdAt, updatedAt = updatedAt
)

fun Person.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = PersonEntity(
    id = id, name = name, createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

fun DebtTransactionEntity.toDomain() = DebtTransaction(
    id = id, personId = personId, amount = amount, type = type,
    note = note, date = date, createdAt = createdAt, updatedAt = updatedAt
)

fun DebtTransaction.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = DebtTransactionEntity(
    id = id, personId = personId, amount = amount, type = type,
    note = note, date = date, createdAt = createdAt,
    updatedAt = System.currentTimeMillis(), syncStatus = syncStatus
)

// ──────────────────────── Pomodoro ────────────────────

fun PomodoroEntity.toDomain() = PomodoroSession(
    id = id, startTime = startTime, endTime = endTime,
    durationMinutes = durationMinutes, type = type,
    completed = completed, date = date, createdAt = createdAt
)

fun PomodoroSession.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = PomodoroEntity(
    id = id, startTime = startTime, endTime = endTime,
    durationMinutes = durationMinutes, type = type,
    completed = completed, date = date, createdAt = createdAt,
    syncStatus = syncStatus
)

// ──────────────────────── Notes ───────────────────────

fun NoteEntity.toDomain() = Note(
    id = id, title = title, content = content,
    color = color, isPinned = isPinned,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Note.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = NoteEntity(
    id = id, title = title, content = content,
    color = color, isPinned = isPinned,
    createdAt = createdAt, updatedAt = System.currentTimeMillis(),
    syncStatus = syncStatus
)

// ──────────────────────── Health ───────────────────────

fun HealthEntity.toDomain() = HealthLog(
    id = id, type = type, value = value,
    unit = unit, note = note, date = date
)

fun HealthLog.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING) = HealthEntity(
    id = id, type = type, value = value,
    unit = unit, note = note, date = date,
    syncStatus = syncStatus
)
