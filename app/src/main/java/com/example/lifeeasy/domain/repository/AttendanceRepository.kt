package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAllAttendance(): Flow<List<Attendance>>
    fun getAttendanceBySubject(subjectId: String): Flow<List<Attendance>>
    fun getAttendanceForDate(startDate: Long, endDate: Long): Flow<Attendance?>
    suspend fun getAttendanceById(id: String): Attendance?
    suspend fun saveAttendance(attendance: Attendance)
    suspend fun updateAttendance(attendance: Attendance)
    suspend fun deleteAttendance(attendance: Attendance)
    suspend fun markAttendance(subjectId: String, status: String)
    suspend fun syncPendingAttendance()
}
