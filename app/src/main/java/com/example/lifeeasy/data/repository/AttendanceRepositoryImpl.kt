package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.AttendanceDao
import com.example.lifeeasy.data.local.dao.SubjectDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Attendance
import com.example.lifeeasy.domain.repository.AttendanceRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val subjectDao: SubjectDao,
    private val firestore: FirebaseFirestore
) : AttendanceRepository {

    override fun getAllAttendance(): Flow<List<Attendance>> =
        attendanceDao.getAllAttendance().map { list -> list.map { it.toDomain() } }

    override fun getAttendanceBySubject(subjectId: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceBySubject(subjectId).map { list -> list.map { it.toDomain() } }

    override fun getAttendanceForDate(startDate: Long, endDate: Long): Flow<Attendance?> =
        attendanceDao.getAttendanceForDateRange(startDate, endDate).map { it?.toDomain() }

    override suspend fun getAttendanceById(id: String): Attendance? =
        attendanceDao.getAttendanceById(id)?.toDomain()

    override suspend fun saveAttendance(attendance: Attendance) {
        attendanceDao.insert(attendance.toEntity(SyncStatus.PENDING))
    }

    override suspend fun updateAttendance(attendance: Attendance) {
        attendanceDao.update(attendance.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteAttendance(attendance: Attendance) {
        attendanceDao.delete(attendance.toEntity())
    }

    override suspend fun markAttendance(subjectId: String, status: String) {
        val subject = subjectDao.getSubjectById(subjectId) ?: return
        
        // Create attendance record
        val attendance = Attendance(
            id = UUID.randomUUID().toString(),
            subjectId = subjectId,
            date = System.currentTimeMillis(),
            status = status,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        attendanceDao.insert(attendance.toEntity(SyncStatus.PENDING))
        
        // Update subject counts
        val updatedSubject = subject.copy(
            totalClasses = subject.totalClasses + 1,
            attendedClasses = if (status == "present" || status == "late") subject.attendedClasses + 1 else subject.attendedClasses,
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING
        )
        subjectDao.update(updatedSubject)
    }

    override suspend fun syncPendingAttendance() {
        val unsynced = attendanceDao.getUnsyncedAttendance()
        for (entity in unsynced) {
            try {
                firestore.collection("attendance").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                attendanceDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                attendanceDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
